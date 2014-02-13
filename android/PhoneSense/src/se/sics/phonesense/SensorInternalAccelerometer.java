package se.sics.phonesense;

import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorDataType;
import se.sics.sensorservices.SensorsManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
public class SensorInternalAccelerometer extends SensorBase implements SensorEventListener{
	
	private static final long serialVersionUID = 1L;
	
	private final int SAMPLING_RATE = 50;
//	private final int SAMPLE_DURATION_SECONDS = 3;
	private final int SAMPLING_INTERVAL_US = 1000000 / SAMPLING_RATE;
//	private final int BUFFER_SIZE = SAMPLING_RATE * SAMPLE_DURATION_SECONDS;
	
	private Context context;
	
	private SensorManager sensorManager;
/*	
	private AccelerometerDataBuffer dataBuffer;
	private Float previousX, previousY, previousZ;
*/	
	public static final String FILE_ACCELEROMETER_LOG = "phone_accelerometer.csv";
	public SensorDataType typeAccX = new SensorDataType("accX", "Acceleration X", "ms-2", 2, SensorDataType.VARIABLE_TYPE_FLOAT, SensorDataType.VARIABLE_CLASS_REALTIME);

	
	public SensorInternalAccelerometer(Context newContext){
		super();
		TAG = "SensorInternalAccelerometer";
		
		//Fill in this sensor's information
		setSensorType(PhoneSensorsManager.SENSOR_TYPE_INTERNAL_ACCELEROMETER);
		setSensorName("Accelerometer");
		setSensorDescription("This sensor is built into your Android device, and is used to log detailed movement data.");
		//setSensorIconResourceId(R.drawable.sensor_acc);
		
		declareProvidedType(typeAccX);

		
		context = newContext;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
//		dataBuffer = new AccelerometerDataBuffer(BUFFER_SIZE);			//This buffer will hold one second of data
	}

	@Override
	public void connect(SensorsManager sensorsManager) {
		super.connect(sensorsManager);
		//Request the system to provide accelerometer updates
		Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		boolean connected = false;
		//Register the listener, and set return code to true if successfull
		if(sensorManager.registerListener(this, sensorAccelerometer, SAMPLING_INTERVAL_US)) connected = true;
		if(connected){
			Log.d(TAG, "CONNECTED");
			connectionSucceeded();
			//writeDataToOutputFile(FILE_ACCELEROMETER_LOG, "Time,X acceleration,Y acceleration,Z acceleration\n");
		}
		else{
			Log.d(TAG, "NOT CONNECTED");
			connectionFailed();
		}
	}
/*
	private void bufferAccelerometerReading(float accX, float accY, float accZ) {
		if(previousX!=null){		//This is not the first point			
			dataBuffer.storeSample(Math.abs(accX - previousX), Math.abs(accY - previousY), Math.abs(accZ - previousZ));
			if(dataBuffer.getStoreOperations()%SAMPLING_RATE==0 && dataBuffer.isBufferFull()){		//One second has elapsed
				Log.d(TAG, "VALUE: "+dataBuffer.getAmplitude());
			}
		}
		previousX = accX;
		previousY = accY;
		previousZ = accZ;
	}
	
	private void logRawAccelerometerReading(long timestamp, float accX, float accY, float accZ) {
		String fileText = timestamp+","+Utils.round(accX,2)+","+Utils.round(accY, 2)+","+Utils.round(accZ, 2)+"\n";
		writeDataToOutputFile(FILE_ACCELEROMETER_LOG, fileText);
    }
*/
	@Override
	public void disconnect(){
		super.disconnect();
		sensorManager.unregisterListener(this);	//Remove the accelerometer listener from the sensor manager
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (connected && event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			//Extract the three acceleration values
			float accX = event.values[0];
			float accY = event.values[1];
			float accZ = event.values[2];
			long timestamp = event.timestamp / 1000 / 1000;	//Also get the timestamp (which is in nanoseconds), and convert it down to milliseconds
			long newMillenium = 946684800000L;
			//Here we reach a problem due to some stupid methods in the Android API: Early version of android (up to 4.1 I think) represent the timestamp as time since boot, but versions 4.2 and later represent it as time since the epoch.
			//So we have to check to see if this value is way too small (indicating it is time since boot), and add the boot time if required
			if(timestamp<newMillenium){		//If the timestamp is from the last millenium then we assume it must be missing the boot time component
				timestamp+= systemBooted;
			}
			Bundle dataBundle = new Bundle();
			//dataBundle.putFloat(DataTypes.getTypeFromCode(DataTypes.ELEVATION).getShortCode(), (float) Utils.round(currentElevation, 2));
			dataBundle.putFloat("accX", accX);
			sendMessage(PhoneSensorsManager.MESSAGE_TYPE_DATA, dataBundle);
			
//			bufferAccelerometerReading(accX, accY, accZ);
/*			if(recordingEnabled){					//Only handle the event if this sensor is enabled						
				logRawAccelerometerReading(timestamp, accX, accY, accZ);	//Pass the values off to a function that will log the data on the SD card
			}
			*/
        }
	}
}