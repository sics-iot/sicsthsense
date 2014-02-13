package se.sics.phonesense;

import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorsManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorInternalGyroscope extends SensorBase implements SensorEventListener {
	private static final long serialVersionUID = 1L;
	
	private Context context;
	
	private SensorManager sensorManager;	
	//private SensorEventListener gyroscopeListener;
	
	private final String FILE_GYROSCOPE_LOG = "internal_gyroscope.csv";
	
	public SensorInternalGyroscope(Context newContext){
		super();
		TAG = "SensorInternalGyroscope";
		
		//Fill in this sensor's information
/*
		setSensorType(SkilabSensorsManager.SENSOR_TYPE_INTERNAL_GYROSCOPE);
*/
		setSensorName("Gyroscope");
		setSensorDescription("This sensor is built into your Android device, and can be used to log detailed data about the rotational speed of the device in three axes. This data is saved in a log file for custom analysis.");
		declareOutputFile(FILE_GYROSCOPE_LOG);
		
		context = newContext;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}
	
	@Override
	public void connect(SensorsManager sensorsManager) {
		super.connect(sensorsManager);
		//Request the system to provide accelerometer updates
		Sensor sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		boolean connected = false;
		//Register the listener, and set return code to true if successfull
		if(sensorManager.registerListener(this, sensorGyroscope, samplingRate)) connected = true;
		if(connected){
			connectionSucceeded();
			writeDataToOutputFile(FILE_GYROSCOPE_LOG, "Time,X angular velocity,Y angular velocity,Z angular velocity\n");
		}
		else{
			connectionFailed();
		}
	}
	
	protected void logRawGyroscopeReading(long timestamp, float rotationX, float rotationY, float rotationZ) {
		String fileText = timestamp+","+rotationX+","+rotationY+","+rotationZ+"\n";
		writeDataToOutputFile(FILE_GYROSCOPE_LOG, fileText);
	}

	@Override
	public void disconnect(){
		super.disconnect();
		sensorManager.unregisterListener(this);	//Remove the accelerometer listener from the sensor manager
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (connected && event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
			//Extract the three angular velocity values
			float rotationX = event.values[0];
			float rotationY = event.values[1];
			float rotationZ = event.values[2];
			long timestamp = event.timestamp / 1000 / 1000;	//Also get the timestamp (which is in nanoseconds), and convert it down to milliseconds
			long newMillenium = 946684800000L;
			//Here we reach a problem due to some stupidity in the Android API: Early version of android (up to 4.1 I think) represent the timestamp as time since boot, but versions 4.2 and later represent it as time since the epoch.
			//So we have to check to see if this value is way too small (indicating it is time since boot), and add the boot time if required
			if(timestamp<newMillenium){		//If the timestamp is from the last millenium then we assume it must be missing the boot time component
				timestamp+= systemBooted;
			}
			if(recordingEnabled){					//Only handle the event if this sensor is enabled						
				logRawGyroscopeReading(timestamp, rotationX, rotationY, rotationZ);	//Pass the values off to a function that will log the data on the sd card
			}
        }
	}
}