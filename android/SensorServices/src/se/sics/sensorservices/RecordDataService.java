package se.sics.sensorservices;

import java.io.File;
import java.util.HashMap;

import se.sics.sensorservices.DataStore;
import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorBase.SensorStatus;
import se.sics.sensorservices.SensorsManager;
import se.sics.sensorservices.SensorsManager.RecordDataEvents;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * A base {@link Service} that handles communication with a {@link SensorsManager}. You may extend this class in order to create a service that records data from sensors.
 * This class takes care of all the basic service-related stuff, such as optionally acquiring a {@link WakeLock}.
 * In your extended service, you should override {@link Service#onStartCommand(Intent, int, int)} and {@link Service#onDestroy()}, and place code in these two events for starting and stopping your service.
 * Note that you must call super.onStartCommand() at the end of your own onStartCommand(), and super.onDestroy() at the end of your own onDestroy(). Additionally, you should call {@link RecordDataService#stopRecording()}
 * at some point during your onDestroy().
 */
public class RecordDataService extends Service implements RecordDataEvents{
	private final static String TAG = "RecordDataService";

	public static final int STATE_NOT_CONNECTED = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	public static final int STATE_RECORDING = 4;
	public static final int STATE_DISCONNECTING = 5;

	public static int currentState = STATE_NOT_CONNECTED;

	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock = null;

	private static SensorsManager sensorsManager;

	private boolean useWakeLock = false;
	protected static Handler uiMessageHandler;

	//protected File dataLoggingDirectory = null;
	
	protected DataStore dataStore;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		currentState = STATE_CONNECTING;

		if(useWakeLock){
			powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
			wakeLock.acquire();
			Log.d(TAG, "WakeLock acquired");
		}

		if(sensorsManager!=null){
			//sensorsManager.requestAllDataTypes();			//And all data types
			sensorsManager.connectToAvailableSensors();	//Connect to the sensors
			sensorsManager.addRecordDataListener(this);
		}
		else{
			Log.e(TAG, "CRITICAL ERROR, SENSORS MANAGER NOT SET");
			currentState = STATE_NOT_CONNECTED;
			stopSelf();			//We cannot continue without a SensorsManager
		}

		//We don't want the OS to restart this service on its own
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy(){
		currentState = STATE_DISCONNECTING;
		
		if(sensorsManager!=null){
			sensorsManager.disconnectFromSensors();
			sensorsManager.removeRecordDataListener(this);
		}
		if(wakeLock!=null && wakeLock.isHeld()){		//True if the wakeLock has been instantiated and is currently being used
			wakeLock.release();
			Log.d(TAG, "WakeLock released");
		}

		//Set the static variables back to their fail-safe defaults, as they will persist
		useWakeLock = false;
		currentState = STATE_NOT_CONNECTED;
		if(uiMessageHandler!=null){
			uiMessageHandler.sendEmptyMessage(SensorsManager.MESSAGE_TYPE_DISCONNECTION_COMPLETE);	//Inform the handler that disconnection is complete
																					//Do this here as the child service may have already removed the event listener
		}
		super.onDestroy();
	}
	
	public static void startRecordingFromSensors(File dataLoggingDirectory){
		if(currentState==STATE_CONNECTED && sensorsManager!=null){
			sensorsManager.startRecordingFromSensors(dataLoggingDirectory);
			currentState = STATE_RECORDING;
		}
	}
	
	/**
	 * Signals to the {@link SensorsManager} that it should stop recording sensor data. This will not disconnect from the sensors,
	 * that is done when the service is destroyed.
	 */
	public static void stopRecordingFromSensors(){
		if(currentState==STATE_RECORDING && sensorsManager!=null){
			sensorsManager.stopRecordingFromSensors();
			currentState = STATE_CONNECTED;
		}
	}
	
	/**
	 * Sets the Handler that is used to send data to the class that created this ServiceRecordTrack
	 * @param handler		A reference to a Handler, which will be sent events and sensor data
	 */
	public static void setHandler(Handler handler){
		uiMessageHandler = handler;
	}
	
	/**
	 * Sets the {@link SensorsManager} that this service should use for communicating with sensors. If a {@link SensorsManager}
	 * is not provided, then this service will not be able to collect any data. The system is designed in this way because this
	 * base service is part of a separate project to the project where you define your custom {@link SensorsManager}, and therefore
	 * has no knowledge of your custom {@link SensorsManager}.
	 * @param sensorsManager		The custom {@link SensorsManager}.
	 */
	protected void setSensorsManager(SensorsManager newSensorsManager){
		sensorsManager = newSensorsManager;
	}
	
	/**
	 * Used to set whether or not this service should use a CPU wakelock to prevent the device from sleeping whilst the track is recording.
	 * Setting this to true will increase battery usage, but will ensure the service is not stopped by the operating system.
	 * The increase in battery usage may not be that great, as the track will most likely require constant GPS and accelerometer updates,
	 * as well as possibly continuous communciation with a Bluetooth sensor, all of which will reduce the time that the CPU is able to sleep.
	 * @param enableWakelock		Whether or not the device should be prevented from sleeping.
	 */
	protected void setUseWakeLock(boolean enableWakelock){
		useWakeLock = enableWakelock;
	}

	/**
	 * Sets the directory into which sensor data should be written during the data recording session.
	 * @param loggingDirectory		A {@link File} representing the logging directory. If this directory does not exist, it will be created. 
	 */
/*
	protected void setLoggingDirectory(File loggingDirectory){
		dataLoggingDirectory = loggingDirectory;
		if(!dataLoggingDirectory.exists()){		//Ensure the directory exists
			dataLoggingDirectory.mkdirs();
		}
	}
*/	
	/**
	 * Returns a reference to the directory into which sensor data should be written during the data recording session.
	 * @return			A {@link File} representing the logging directory, or null if one has not been set.
	 */
/*
	protected File getLoggingDirectory(){
		return sensorsManager.get;
	}
*/	
	/**
	 * Returns the {@link SensorsManager} object that this service is using for sensor communication. This method is not normally required, but is provided
	 * in case you wish to manually access the SensorsManager.
	 * @return		The {@link SensorsManager}.
	 */
	protected SensorsManager getSensorsManager(){
		return sensorsManager;
	}

	//The next 7 methods are implemented from the RecordDataEvents interface, defined in SensorsManager
	@Override
	public void connectionComplete() {
//		sensorsManager.startRecordingFromSensors();
		currentState = STATE_CONNECTED;		
		if(uiMessageHandler!=null){
			uiMessageHandler.sendEmptyMessage(SensorsManager.MESSAGE_TYPE_CONNECTION_COMPLETE);		//Inform the handler
		}
	}

	@Override
	public void disconnectionComplete(){
	}

	@Override
	public void recordingStarted(){
		uiMessageHandler.sendEmptyMessage(SensorsManager.MESSAGE_TYPE_RECORDING_STARTED);		//Inform the handler
	}

	@Override
	public void recordingStopped(DataStore summaryData){
		uiMessageHandler.sendEmptyMessage(SensorsManager.MESSAGE_TYPE_RECORDING_STOPPED);		//Inform the handler
	}

	@Override
	public void newSensorData(DataStore newData) {
		if(newData!=null){
			dataStore = newData;
			//Send the data to the UI (Activity) that created this service
			if(uiMessageHandler!=null){
				Message handlerMessage = uiMessageHandler.obtainMessage(SensorsManager.MESSAGE_TYPE_DATA);	//Create a message for the data we will send
				Bundle messageData = new Bundle();
				messageData.putSerializable("dataStore", dataStore);
				handlerMessage.setData(messageData);
				uiMessageHandler.sendMessage(handlerMessage);		//Send this message on to the handler
			}
		}
	}

	@Override
	public void newSensorStatusData(HashMap<Integer, SensorStatus> sensorStatusData) {
		if(uiMessageHandler!=null && sensorStatusData!=null){
			Message handlerMessage = uiMessageHandler.obtainMessage(SensorsManager.MESSAGE_TYPE_SENSOR_STATUS_DATA);	//Create a message for the data we will send
			Bundle messageData = new Bundle();
			messageData.putSerializable("statusData", sensorStatusData);
			handlerMessage.setData(messageData);
			uiMessageHandler.sendMessage(handlerMessage);		//Send this message on to the handler
		}
	}

	@Override
	public void recordingError(int errorCode) {
	}

	@Override
	public void sensorConnectionFailed(SensorBase sensor) {
	}

	@Override
	public void sensorConnectionSucceeded(SensorBase sensor) {
	}

	@Override
	public void sensorDisconnected(SensorBase sensor) {
	}

	@Override
	public void newLapDetected(DataStore lapData, int lapNumber) {
	}
}