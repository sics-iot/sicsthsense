package se.sics.phonesense;

import java.io.File;

import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorsManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
public class SensorInternalBarometer extends SensorBase implements SensorEventListener{
	private static final long serialVersionUID = 1L;

	public static final String FILE_RAW_ELEVATION = "raw_elevation_barometer.csv";
	
	private final int SAMPLING_RATE = 10;				//Sampling rate in Hz
	private final int MEDIAN_WINDOW = 3;				//Length of the median buffer in this class (in seconds)
	private final int MEAN_WINDOW = 8;					//Number of samples that the mean buffer will hold
	private static final boolean DEBUG_BUFFERS = false;	//Whether or not the buffers should count their execution time
	
	private final int PRIORITY_ELEVATION = 80;			//The priority that this sensor's elevation-based data types will be given

	private Context context;
	
	private SensorManager sensorManager;
	
	private Double initialGpsElevation;
	private boolean firstPoint;
	private float elevationOffset;
	private MedianBuffer medianBuffer;
	private MeanBuffer meanBuffer;
	
	private Float currentElevation, previousElevation, elevationChange;
	
	private float elevationClimbedTotal, elevationClimbedLap;
	
	public SensorInternalBarometer(Context newContext){
		super();
		TAG = "SensorBarometer";
		
		//Fill in this sensor's information
		setSensorType(PhoneSensorsManager.SENSOR_TYPE_INTERNAL_BAROMETER);
		setSensorName("Barometer");
		setSensorDescription("mySkiLab uses this sensor to calculate your elevation during a track.");
		//setSensorIconResourceId(R.drawable.sensor_acc);

		declareOutputFile(FILE_RAW_ELEVATION);
		
		//declareProvidedType(new DataTypes.TypeElevation().setPriority(PRIORITY_ELEVATION));
		//declareProvidedType(new DataTypes.TypeElevationClimbed().setPriority(PRIORITY_ELEVATION));

		context = newContext;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	@Override
	public void connect(SensorsManager sensorsManager) {
		super.connect(sensorsManager);
		medianBuffer = new MedianBuffer(SAMPLING_RATE * MEDIAN_WINDOW);
		meanBuffer = new MeanBuffer(MEAN_WINDOW);
		meanBuffer.setDebugEnabled(DEBUG_BUFFERS);
		medianBuffer.setDebugEnabled(DEBUG_BUFFERS);
		//elevationFilter = new SmoothingFilter(FILTER_WINDOW, MINIMUM_CHANGE, MAXIMUM_CHANGE);
		//Request the system to provide accelerometer updates
		Sensor sensorBarometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		setSamplingRate(SensorsManager.FREQUENCY_1HZ_US / SAMPLING_RATE);
		
		boolean connected = false;
		//Register the listener, and set return code to true if successfull
		connected = sensorManager.registerListener(this, sensorBarometer, samplingRate);
		if(connected){
			Log.d(TAG, "CONNECTED");
			connectionSucceeded();
			initialGpsElevation = getAltitudeFromGps();
			firstPoint = true;
		}
		else{
			connectionFailed();
			Log.d(TAG, "NOT CONNECTED");
		}
	}
	
	@Override
	public void startRecording(File loggingDirectory){
		super.startRecording(loggingDirectory);
		elevationClimbedTotal = 0;
		elevationClimbedLap = 0;
	}
	
	@Override
	public void newLapDetected(int lapNumber, long lapDuration){

	}
	
	@Override
	public void stopRecording(long trackDuration){
		super.stopRecording(trackDuration);
		//storeSummaryValue(DataTypes.getTypeFromCode(DataTypes.ELEVATION_CLIMBED).getShortCode(), elevationClimbedTotal);
		elevationClimbedTotal = 0;
		if(DEBUG_BUFFERS){
			Log.d(TAG, "Median execution time: "+medianBuffer.getExecutionTime());
			Log.d(TAG, "Mean execution time: "+meanBuffer.getExecutionTime());
		}
	}

	@Override
	public void disconnect(){
		super.disconnect();
		sensorManager.unregisterListener(this);	//Remove the accelerometer listener from the sensor manager
	}

	/**
	 * Retrieves the last known altitude from the GPS.
	 * @return		The altitude in metres, or null if not available.
	 */
	private Double getAltitudeFromGps() {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(lastLocation!=null && lastLocation.hasAltitude()){
			return lastLocation.getAltitude();
		}
		else{
			return null;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(connected){
			Float median = null;
			if (event.sensor.getType()==Sensor.TYPE_PRESSURE){
				float pressure = event.values[0];
				float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);		//Get the altitude difference between standard sea level pressure and this reading.
				//Log.d(TAG, "Raw: "+altitude);
				if(firstPoint){									//True if this is the first point, which means we should try and calibrate the barometer
					if(initialGpsElevation!=null){				//Altitude is not neccessarily correct so we will now compare this value with the elevation we got from the gps
						elevationOffset = (float) (initialGpsElevation - altitude);
						Log.d(TAG, "Elevation offset: "+elevationOffset);
					}
					else{
						elevationOffset = 0;						//No altitude from GPS available, so offset is 0
					}
					firstPoint = false;
				}
				float tempElevation = altitude + elevationOffset;		//Add the offset to the reading, to (hopefully) make it more accurate
				
				medianBuffer.storeValue(tempElevation);
				
				if(medianBuffer.getPointer()%SAMPLING_RATE==0 && medianBuffer.isBufferFull()){		//True if 1 second has elapsed since the last smoothed sample was collected from the buffer, it is time to get the next one
					median = medianBuffer.getFilteredValue();
					if(median!=null){
						meanBuffer.storeValue(median);
						if(meanBuffer.isBufferFull()){
							currentElevation = meanBuffer.getFilteredValue();
							if(currentElevation!=null){
								if(messageHandler!=null){				//First of all, send the elevation to the manager
									Bundle dataBundle = new Bundle();
									//dataBundle.putFloat(DataTypes.getTypeFromCode(DataTypes.ELEVATION).getShortCode(), (float) Utils.round(currentElevation, 2));
									sendMessage(PhoneSensorsManager.MESSAGE_TYPE_DATA, dataBundle);
								}
								if(recordingEnabled && previousElevation!=null){		//Only add to the elevation climbed totals if we are currently recording
									elevationChange = currentElevation - previousElevation;
									if(elevationChange>0){			//If the filter has an elevation delta (changed since last point) value for us, add it to the lap and summary totals
										elevationClimbedTotal+= elevationChange;
										elevationClimbedLap+= elevationChange;
									}
								}
								previousElevation = currentElevation;
							}
						}
					}
				}
				writeDataToOutputFile(FILE_RAW_ELEVATION, System.currentTimeMillis()+"-"+tempElevation+"-"+median+"-"+currentElevation+"\n");
			}
		}
	}
}