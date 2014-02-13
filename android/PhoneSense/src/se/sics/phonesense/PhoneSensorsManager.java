package se.sics.phonesense;


import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorsManager;
import android.content.Context;
import android.hardware.Sensor;

public class PhoneSensorsManager extends SensorsManager{
	//private final static String TAG = "SkilabSensorsManager";

	//DEVICE CODES
	//Device codes for internal sensors. Internal sensors can have codes 0 to 9. This should be enough for any device. Where possible, we copy the sensor type codes from the Android definitions
	public static final int SENSOR_TYPE_INTERNAL_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
	public static final int SENSOR_TYPE_INTERNAL_GPS = 2;
	//public static final int SENSOR_TYPE_INTERNAL_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
	public static final int SENSOR_TYPE_INTERNAL_BAROMETER = Sensor.TYPE_PRESSURE;

	
	//SENSOR STATUS DATA TYPE CODES
	//Shared types
	public static final int SENSOR_STATUS_BATTERY_LEVEL = 1;
	public static final int SENSOR_STATUS_ACCURACY = 2;

	//Types for GPS
	public static final int SENSOR_STATUS_NUM_GPS_SATELLITES = 10;
	
	public static final int SENSOR_TYPE_DEVICE_STATUS = 110;
	
	//This is the shared single instance of this class that all other classes will use. This ensures that state is maintained across different classes
	private static PhoneSensorsManager instance = null;
	
	/**
	 * Use this to get a copy of the global {@link PhoneSensorsManager}. This is a singleton class, menaing that all copies of it share a common state.
	 * This is useful for keep multi-fragment / activity UIs in sync.
	 * @param context		The context in which this {@link PhoneSensorsManager} will be used. Because (behind the scenes) there is only one instance of this class,
	 * 						the context passed during the first call to this method is re-used. For this reason, it is recommended to use
	 * 						{@link Context#getApplicationContext()} to get the {@link Context} to pass here.
	 * @return				A copy of the {@link PhoneSensorsManager}. 
	 */
	public static PhoneSensorsManager getSensorsManager(Context context){
		if(instance==null){
			instance = new PhoneSensorsManager(context);
		}
		return instance;
	}
	
	private PhoneSensorsManager(Context context){
		super(context);

		declareVirtualSensor(SENSOR_TYPE_DEVICE_STATUS);
	}

	@Override
	protected SensorBase instantiateSensor(Integer sensorType, String sensorAddress) {
		SensorBase sensor;
		switch(sensorType){		//This switch will instantiate the sensor, using the sensor type, to determine which class should be used
		case SENSOR_TYPE_INTERNAL_ACCELEROMETER:
			sensor = new SensorInternalAccelerometer(context);
			break;
/*
		case SENSOR_TYPE_INTERNAL_GYROSCOPE:
			sensor = new SensorInternalGyroscope(context);
			break;
*/
/*		case SENSOR_TYPE_INTERNAL_GPS:
			sensor = new SensorInternalGps(context);
			break;
*/		case SENSOR_TYPE_INTERNAL_BAROMETER:
			sensor = new SensorInternalBarometer(context);
			break;

		case SENSOR_TYPE_DEVICE_STATUS:
			sensor = new SensorDeviceStatus(context);
			break;
		default:
			sensor = null;
			break;
		}		
		return sensor;
	}
}