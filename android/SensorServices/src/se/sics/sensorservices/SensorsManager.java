package se.sics.sensorservices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;

import se.sics.sensorservices.SensorBase.SensorStatus;
import se.sics.sensorservices.SensorDatabase.SensorInfo;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 	<p>This class is provided to you to make working with multiple sensors simple. It can be used to scan for new sensors, add them to the system,
 * 	connect to them at any time, receive data from them, disconnect, and also remove them from the system.</p>
 * 
 * 	<p>Getting started:</p>
 * 	<ol>
 * 		<li>Create a new class of your own that extends this class. For example:
 * 			<pre>public class MyCustomSensorsManager extends SensorsManager</pre>
 *		</li>
 *		<li>Create a new sensor class that extends extends this class. For example:
 * 			<pre>public class MyHeartRateSensor extends SensorBase</pre>
 * 			See {@link SensorBase} for a full explanation of how to write a new sensor class.
 *		</li>
 * 		<li>Define {@link Integer} codes in your class for each type of sensor you will use in your system. For example:
 * 			<pre>public static final int SENSOR_TYPE_MY_BLUETOOTH_HEART_RATE_SENSOR = 1;</pre>
 *		</li>
 * 		<li>Define {@link String} codes in your class for each variable that these sensors will provide. For example:
 * 			<pre>public static final String DATA_TYPE_HEART_RATE = "hrt";</pre>
 * 		</li>
 * 		<li>If your system will contain external (Bluetooth) sensors, you wil need to call {@link SensorsManager#addDeviceNameCode(String, Integer)}
 * 			in your new class's constructor for each type of external sensor you wish to use. For example:
 * 			<pre>addDeviceNameCode("HR", SENSOR_TYPE_MY_BLUETOOTH_HEART_RATE_SENSOR);</pre>
 * 		</li>
 * 		<li>Finally, you will need to override the method {@link SensorsManager#instantiateSensor(Integer, String)}. It is best to think of this method
 * 			as a "Sensor factory", which creates new sensor objects based on the sensor type provided. For example:
 * 			<pre>@Override
 *			protected SensorBase instantiateSensor(Integer sensorType, String sensorAddress) {
 *				SensorBase sensor = null;
 *				switch(sensorType){
 *				case SENSOR_TYPE_MY_BLUETOOTH_HEART_RATE_SENSOR:
 *					sensor = new MyHeartRateSensor();
 *					break;
 *				default:
 *					break;
 *				}
 *				return sensor;
 *			}
 *			</pre>
 * 		</li>
 * 	</ol>
 * 
 * 	<p>Once these initial steps have been completed for each sensor and variable you want to use in your system, you are ready to use your new MyCustomSensorsManager
 * 	class in your project. You can get a create an instance of your manager as follows:
 * 		<pre>MyCustomSensorsManager mySensorsManager = new MyCustomSensorsManager(context);</pre>
 * 	</p>
 * 	<p>Now you may use this object to perform any sensor administration work, from scanning for sensors, to receiving data from them.</p>
 *
 */
@SuppressLint("UseSparseArrays")
public class SensorsManager{
	private final static String TAG = "SensorsManager";
	
	public static final String INTENT_SIGNATURE_SENSOR_ADDED = "se.sics.sensorservices.sensorAdded";
	public static final String INTENT_SIGNATURE_SENSOR_REMOVED = "se.sics.sensorservices.sensorRemoved";
	
	public static final String VERSION_NAME = "2.7.1";
	
	/**
	 * Code for an error message
	 */
	public static final int MESSAGE_TYPE_ERROR = 1;	
	/**
	 * Code indicating a sensor was connected successfully
	 */
	public static final int MESSAGE_TYPE_SENSOR_CONNECTION_SUCCEEDED = 2;
	/**
	 * Code indicating that conection to a sensor failed
	 */
	public static final int MESSAGE_TYPE_SENSOR_CONNECTION_FAILED = 3;
	/**
	 * Code indicating a sensor was disconnected
	 */
	public static final int MESSAGE_TYPE_SENSOR_DISCONNECTED = 4;
	/**
	 * Code indicating that the connection process has completed (all sensors have finished attempting to connect)
	 */
	public static final int MESSAGE_TYPE_CONNECTION_COMPLETE = 5;
	/**
	 * Code indicating that the disconnection process has completed
	 */
	public static final int MESSAGE_TYPE_DISCONNECTION_COMPLETE = 6;
	/**
	 * Code indicating that recording of a track has just started
	 */
	public static final int MESSAGE_TYPE_RECORDING_STARTED = 7;
	/**
	 * Code indicating that recording of a track has just stopped
	 */
	public static final int MESSAGE_TYPE_RECORDING_STOPPED = 8;
	/**
	 * Code for an data message
	 */
	public static final int MESSAGE_TYPE_DATA = 10;
	/**
	 * Code for a sensor status data message
	 */
	public static final int MESSAGE_TYPE_SENSOR_STATUS_DATA = 11;
	
	//Data type for time. Defined here as all implementation use time.
	
	public static final String DATA_TYPE_CURRENT_TIME = "tim";
	public static final String DATA_TYPE_DURATION = "dur";
	public static final String DATA_TYPE_LAP_NUMBER = "lap";

	//Frequencies in microseconds
	
	/**
	 * Interval in microseconds for 50HZ
	 */
	public static final int FREQUENCY_50HZ_US = 20000;
	
	/**
	 * Interval in microseconds for 1HZ
	 */
	public static final int FREQUENCY_1HZ_US = 1000000;
	
	//Frequencies in milliseconds
	/**
	 * Interval in milliseconds for 0.5HZ
	 */
	public static final int FREQUENCY_0_5HZ_MS = 2000;
	/**
	 * Interval in milliseconds for 1HZ
	 */
	public static final int FREQUENCY_1HZ_MS = 1000;
	/**
	 * Interval in milliseconds for 2HZ
	 */
	public static final int FREQUENCY_2HZ_MS = 500;
	/**
	 * Interval in milliseconds for 5HZ
	 */
	public static final int FREQUENCY_5HZ_MS = 200;
	
	public static final int SENSOR_TYPE_SENSOR_MANAGER = 199;
	
	private int dataMessageInterval = FREQUENCY_1HZ_MS;
	
	private SensorDataType dataTypeTime, dataTypeDuration;
	
	private int nSensorsToConnectTo = 0;
	private int nSensorsReplied = 0;
	
	private ArrayList<SensorBase> sensorsPendingConnection = new ArrayList<SensorBase>();
	
	private boolean currentlyRecording = false;
	
	//private Thread periodicRefresh = null;
	
	private volatile DataStore dataStore = null;
	
	private SensorsManager thisManager = null;
	
	private SensorDatabase sensorDatabase;
	
	protected Context context;

	public HashMap<String, Integer> deviceNameCodesToTypeCodes;
	
	private ArrayList<Integer> virtualSensors;
	
	
	private ArrayList<SensorScanEvents> sensorScanInterfaces;
	private ArrayList<RecordDataEvents> recordDataInterfaces;
	private ArrayList<SensorDataListener> sensorDataListeners;
	
	private Timer timer;
	
	//Unique UUID for the device. This particular UUID is the one required for Serial Port Profile (also called RFCOMM) Bluetooth communication
	private final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	//private BluetoothAdapter bluetoothAdapter = null;

	private boolean bluetoothEnabledBeforeOperation = false;
	private ArrayList<Integer> desiredSensorTypesList = null;
	private boolean automaticallyAddSensors = false;
	
	private SensorBase sensorPendingPairing = null;
	
	private ArrayList<SensorBase> sensorsCurrentlyConnectedList;

	private ArrayList<String> requestedDataTypes;
	private boolean allDataTypesRequested = true;
	
	private HashMap<String, SensorDataType> availableDataTypes;
	
	private File dataLoggingDirectory = null;
	
	private boolean saveLapData = true;
	
	public static final String FILE_LAP_DATA = "lap_data.json";
	
	private File lapDataFile;
	private FileWriter lapDataFileWriter = null;
	private int currentLap;
	private int lastPointLap;
	private long lastLapTimestamp;
	private DataStore lapData, summaryData;

	@SuppressWarnings("unused")
	private SensorBase managerSensor;
	
	/**
	 * Constructor
	 * @param context		The {@link Context} in which this class was created
	 */
	public SensorsManager(Context context){
		this.context = context;
		
		sensorScanInterfaces = new ArrayList<SensorScanEvents>();
		recordDataInterfaces = new ArrayList<RecordDataEvents>();
		sensorDataListeners = new ArrayList<SensorDataListener>();

		thisManager = this;


		sensorDatabase = new SensorDatabase(this.context);
		
		managerSensor = new SensorManagerSensor(context);
		
		dataTypeTime = new TypeCurrentTime();
		dataTypeDuration = new TypeDuration();
		
		refreshAvailableDataTypes();
		
		requestedDataTypes = new ArrayList<String>();
		
		sensorsCurrentlyConnectedList = new ArrayList<SensorBase>();

		//Set up the map of name codes
		deviceNameCodesToTypeCodes = new HashMap<String, Integer>();
		
		//Set up the list of virtual sensors
		virtualSensors = new ArrayList<Integer>();
	}
	
	/**
	 * Adds a {@link SensorScanEvents} listener to this {@link SensorsManager}. This listener will be informed of events that occur during any sensor scans.
	 * You must remove this listener when you are finished with it, by calling {@link SensorsManager#removeSensorScanListener(SensorScanEvents)}. 
	 * @param sensorScanListener		The event listener to add.
	 */
	public void addSensorScanListener(SensorScanEvents sensorScanListener){
		if(sensorScanListener!=null){
			sensorScanInterfaces.add(sensorScanListener);
		}
	}
	
	/**
	 * Removes a {@link SensorScanEvents} listener from this {@link SensorsManager}. You must remove all {@link SensorScanEvents} listeners when your {@link Activity} is destroyed, or before if possible.
	 * @param sensorScanListener		The event listener to remove.
	 */
	public void removeSensorScanListener(SensorScanEvents sensorScanListener){
		if(sensorScanListener!=null){
			sensorScanInterfaces.remove(sensorScanListener);
		}
	}
	
	/**
	 * Adds a {@link RecordDataEvents} listener to this {@link SensorsManager}. This listener will be informed of events that occur during any sensor scans.
	 * You must remove this listener when you are finished with it, by calling {@link SensorsManager#removeRecordDataListener(RecordDataEvents)}. 
	 * @param recordDataListener		The event listener to add.
	 */
	public void addRecordDataListener(RecordDataEvents recordDataListener){
		if(recordDataListener!=null){
			recordDataInterfaces.add(recordDataListener);
		}
	}
	
	/**
	 * Removes a {@link RecordDataEvents} listener from this {@link SensorsManager}. You must remove all {@link RecordDataEvents} listeners when your {@link Activity} is destroyed, or before if possible.
	 * @param recordDataListener		The event listener to remove.
	 */
	public void removeRecordDataListener(RecordDataEvents recordDataListener){
		if(recordDataListener!=null){
			recordDataInterfaces.remove(recordDataListener);
		}
	}
	
	public void addSensorDataListener(SensorDataListener sensorDataListener){
		Log.d(TAG, "N listeners: "+sensorDataListeners.size());
		Log.d(TAG, "NULL: "+(sensorDataListener==null));
		if(sensorDataListener!=null){
			sensorDataListeners.add(sensorDataListener);
			Log.d(TAG, "N listeners: "+sensorDataListeners.size());
		}
	}
	
	public void removeSensorDataListener(SensorDataListener sensorDataListener){
		if(sensorDataListener!=null){
			sensorDataListeners.remove(sensorDataListener);
		}
	}
	
	/**
	 * Returns the enabled state of the Bluetooth adapter this SensorsManager is using.
	 * @return	Boolean, true if Bluetooth is enabled, false otherwise.
	 */
	public boolean isBluetoothEnabled(){
		return false;
		//return bluetoothAdapter.isEnabled();
	}
	
	/**
	 * Adds a Bluetooth name code to the list of available name codes. A name code is a two-character string that is used to identify a Bluetooth sensor during a scan,
	 * and should always be the first two characters of your Bluetooth sensor's visible name. For example, if you have a Bluetooth heart rate sensor, you should decide on a code
	 * (e.g. HR) and place this at the beginning of the sensor's Bluetooth name, e.g. ("HR 1234"). This code will then be used when scanning for Bluetooth sensors, and any devices
	 * with names beginning with "HR" will be assumed to be one of your heart rate sensors. For this reason, it is strongly recommened to use a code that is not likely to be found in
	 * other devices' names.
	 * @param deviceNameCode		A two-character string that will identify your Bluetooth sensor
	 * @param sensorType			The type of sensor that this code will refer to. You should define Integer codes for each of your sensors somewhere
	 * 								(a good place to do this is in your custom implementation of the {@link SensorsManager} class).
	 */
	public void addDeviceNameCode(String deviceNameCode, Integer sensorType){
		deviceNameCodesToTypeCodes.put(deviceNameCode, sensorType);
	}
	
	/**
	 * Adds a virtual sensor to the system. This must be done for each virtual sensor you want to use in your system.
	 * @param sensorCode		The sensor type code of your virtual sensor.
	 */
	public void declareVirtualSensor(int sensorCode) {
		virtualSensors.add(sensorCode);
	}
	
	/**
	 * Adds a sensor to the persistent sensor configuration. This sensor will remain in the configuration until removed, or the app's data is cleared.
	 * @param sensorToAdd		The sensor you wish to add. This may be an object from any class that extends the {@link SensorBase} class.
	 */
	public void addSensorToConfiguration(SensorBase sensorToAdd){
		sensorDatabase.addSensor(sensorToAdd);
		refreshAvailableDataTypes();
	}
	
	/**
	 * Attempt to pair with a bluetooth sensor. Pairing will trigger the OS to display a dialog box asking the user to enter the bluetooth PIN of the sensor
	 * in question. If pairing is successfull, the sensor is added to the persistent sensor configuration.
	 * @param sensorToPairWith			The sensor you wish to pair with and add to the system, which must be an instance of a class that extends {@link SensorBase}.
	 * 									If this sensor is an internal sensor (i.e. not Bluetooth), no action will be taken.
	 */
	public void pairWithSensorAndAddToConfiguration(final SensorBase sensorToPairWith){
		if(sensorToPairWith!=null){								//Check to see if we were passed a valid sensor object
			if(sensorToPairWith.isExternal()){					//True if this sensor is an  external one
				sensorPendingPairing = sensorToPairWith;		//Store this for later
				
				Thread initiatePairingThread = new Thread(new Runnable() {		//DO the connecting in another thread as it will block
					@Override
					public void run() {
					
					}
				});
				initiatePairingThread.start();
			}
		}
	}
	
	/**
	 * Removes a sensor from the persistent sensor configuration.
	 * @param sensorToAdd		The sensor you wish to remove. This may be an object from any class that extends the {@link SensorBase} class.
	 */
	public void removeSensorFromConfiguration(SensorBase sensorToRemove){
		sensorDatabase.removeSensor(sensorToRemove);
		refreshAvailableDataTypes();
	}
	
	/**
	 * Enables or disables a sensor. A disabled sensor is not connected to when {@link SensorsManager#connectToAvailableSensors(File)} is called.
	 * @param sensor				The sensor you wish to enable / disable.
	 * @param enabled				The new enabled state.
	 */
	public void setSensorEnabled(SensorBase sensor, boolean enabled){
		sensorDatabase.setSensorEnabled(sensor, enabled);
		refreshAvailableDataTypes();
	}
	
	/**
	 * Starts a scan for any sensors that are not currently stored in the persistent sensor configuration.
	 * To be informed of any new sensors found, create a {@link SensorScanListener} in your code and pass it as a parameter when calling this method.
	 * When a new sensor is found, the method {@link SensorScanListener#newSensorFound(SensorBase, boolean)} will be called in your listener, with the new sensor provided as a parameter.
	 * @param sensorScanListener	A {@link SensorScanListener} that will be notified of any events that occur during the scan (e.g. new sensor detected)
	 * @param desiredSensorTypes	An ArrayList of sensor type codes. If this is not null, only sensors matching the codes in this list will be considered
	 * @param automaticallyAdd		If true, any sensors found will be automatically added to the configuration. If false, it is up to your {@link SensorScanListener} to decide what to do.
	 * 								Note that you may still use a {@link SensorScanListener} to receive events, even if this is set to true.
	 */
	public void scanForNewSensors(ArrayList<Integer> desiredSensorTypes, boolean automaticallyAdd){
		//Send a message to all registered SensorScanEvents listeners
		Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
		SensorScanEvents tempInterface;
		while(scanInterfacesIterator.hasNext()){
			tempInterface = scanInterfacesIterator.next();
			if(tempInterface!=null){		//If this interface is not null, notify it that the scan has started
				//try{
					tempInterface.sensorScanStarted();
				//}
				//catch(BadTokenException e){
				//	Log.e(TAG, "WINDOW NULL: "+e.toString());
				//}
			}
		}
		//The list of desired sensor types is saved globally because it needs to be read by some asynchronous code in the bluetooth receiver
		desiredSensorTypesList = desiredSensorTypes;
		automaticallyAddSensors = automaticallyAdd;			//Ditto for this flag
		
		//Check which types of sensors were requested
		boolean internalRequested = false, virtualRequested = false, externalRequested = false;
		
		Integer tempType = null;
		
		if(desiredSensorTypesList!=null){
			Iterator<Integer> iterator = desiredSensorTypesList.iterator();
			while(iterator.hasNext()){
				tempType = iterator.next();
				if(tempType<100){						//This is an internal type
					internalRequested = true;
				}
				else if(tempType<200){					//This is a virtual type
					virtualRequested = true;
				}
				else{									//This is an external type
					externalRequested = true;
				}
			}
		}
		else{
			internalRequested = true;
			virtualRequested = true;
			externalRequested = true;
		}
		
		if(internalRequested){
			scanForInternalSensors();
		}
		if(virtualRequested){
			scanForVirtualSensors();
		}
		if(externalRequested){
			startBluetoothSensorScan();
		}
		else{
			//Send a message to all registered SensorScanEvents listeners
			scanInterfacesIterator = sensorScanInterfaces.iterator();		//Re-use the iterator from the beginning of this method
			while(scanInterfacesIterator.hasNext()){
				tempInterface = scanInterfacesIterator.next();
				if(tempInterface!=null){		//If this interface is not null, notify it that the scan has started
					tempInterface.sensorScanFinished();
				}
			}
		}
	}
	
	/**
	 * Sets the list of data types that this SensorsManager should send to the handler
	 * @param types		An ArrayList of Strings, where each element is a data type's three letter code
	 */
	public void requestDataTypes(ArrayList<String> types){
		requestedDataTypes = types;
		allDataTypesRequested = false;
	}
	
	/**
	 * Used to inform this SensorsManager that it should send all available data types to the handler
	 */
	public void requestAllDataTypes(){
		allDataTypesRequested = true;
		requestedDataTypes = new ArrayList<String>();
	}
	
	/**
	 * Sets the interval between data messages sent from this SensorsManager. It is recommended to use one of the FREQUENCY_XX_MS constants defined in this class
	 * @param interval		The interval between data messages, in milliseconds
	 */
	public void setDataMessageInterval(int interval){
		dataMessageInterval = interval;
	}
	
	public void enableLapRecording(boolean recordLaps){
		saveLapData = recordLaps;
	}
	
	public File getDataLoggingDirectory(){
		return dataLoggingDirectory;
	}
	
	/**
	 * Returns a boolean representing whether or not there is a sensor of the requested type available to the system. Available to the system in this context means stored in the
	 * persistent sensor configuration, it does not refer to whether the sensor is currently connected.
	 * @param sensorType		The type of sensor you wish to check. Must be one of the SENSOR_xxx types defined in this class .
	 * @return					Boolean, true if the sensor is available, false otherwise.
	 */
	public boolean isSensorTypeAvailable (int sensorType){
		boolean sensorAvailable = false;
		//Get the list of currently connected sensors
		Iterator<SensorBase> iterator = getAvailableSensors(true, true).iterator();
		SensorBase tempSensor;
		while(iterator.hasNext()){			//Loop through this list and check to see if each sensor is the type we are looking for
			tempSensor = iterator.next();
			if(tempSensor!=null && tempSensor.getType()==sensorType){	//True if we found a match and the sensor is enabled
				sensorAvailable = true;
				break;								//We no longer need to loop
			}
		}
		return sensorAvailable;
	}

	/**
	 * Returns a boolean representing whether or not there is a sensor of the requested type enabled in the system. This is similar to {@link #isSensorTypeAvailable(int)},
	 * but will only return true if the sensor in question is enabled.
	 * @param sensorType		The type of sensor you wish to check. Must be one of the SENSOR_xxx types defined in this class .
	 * @return					Boolean, true if the sensor is available, false otherwise.
	 */
	public boolean isSensorTypeEnabled(int sensorType){
		boolean sensorAvailable = false;
		//Get the list of currently connected sensors
		Iterator<SensorBase> iterator = getAvailableSensors(true, true).iterator();
		SensorBase tempSensor;
		while(iterator.hasNext()){			//Loop through this list and check to see if each sensor is the type we are looking for
			tempSensor = iterator.next();
			if(tempSensor!=null && tempSensor.getType()==sensorType && tempSensor.isEnabled()){	//True if we found a match and the sensor is enabled
				sensorAvailable = true;
				break;								//We no longer need to loop
			}
		}
		return sensorAvailable;
	}

	/**
	 * Returns the list of sensors that are available for use in the system. This is not the list of sensors currently connected, but the list of sensors currently stored in the persistent sensor configuration.
	 * @param includeInvisibleSensors	If true, all sensors, including those that are invisible, will be returned. If false, only visible sensors will be returned.		
	 * @return		An ArrayList of sensors, one for each sensor connected. Each sensor is an object from any class that extends the {@link SensorBase} class.
	 */
	public ArrayList<SensorBase> getAvailableSensors(boolean includeInvisibleSensors, boolean includeVirtualSensors){
		ArrayList<SensorInfo> sensorConfigurationList = sensorDatabase.getSensorsInConfiguration();
		
		int nSensors = sensorConfigurationList.size();
		
		ArrayList<SensorBase> sensorList = new ArrayList<SensorBase>();
		
		SensorInfo tempInfo = null;
		SensorBase tempSensor = null;
		//Instantiate each sensor and add it to the list
		for(int i=0; i<nSensors; i++){
			tempInfo = sensorConfigurationList.get(i);							//Get the SensorInfo object
			Integer sensorType = tempInfo.getType();							//Extract the information from it
			String sensorAddress = tempInfo.getAddress();
			tempSensor = instantiateSensor(sensorType, sensorAddress);			//Create a SensorBase-compatible sensor from the relevant sensor class
			if((includeInvisibleSensors || tempSensor.isVisible()) && (includeVirtualSensors || !tempSensor.isVirtual())){				//True if invisible sensors have been requested OR the sensor is visible, AND if virtual sensors have been requested OR the sensor is not virtual
				tempSensor.setEnabled(tempInfo.isEnabled());					//Disable the sensor if it is disabled in the config
				sensorList.add(tempSensor);										//Add the sensor to the list
			}
		}
		return sensorList;
	}
	
	/**
	 * Returns the type of external sensor represented by the two-character code provided.
	 * @param nameCode		A bluetooth name code, typically the first two characters of a bluetooth device's public name.
	 * @return				The type of the sensor, one of the SENSOR_XX constants defined in this class, or null if a type was not found for this code.
	 */
	public Integer getExternalSensorTypeFromNameCode(String nameCode){
		return deviceNameCodesToTypeCodes.get(nameCode);
	}
	
	/**
	 * Returns the list of sensors currently connected to this SensorsManager.
	 * @return		An ArrayList of sensors, one for each sensor connected. Each sensor is an object from any class that extends the {@link SensorBase} class.
	 */
	public ArrayList<SensorBase> getConnectedSensors(){
		return sensorsCurrentlyConnectedList;
	}

	/**
	 * Refreshes the internal list of data types. 
	 */
	private void refreshAvailableDataTypes() {
		availableDataTypes = new HashMap<String, SensorDataType>();
		availableDataTypes.put(dataTypeTime.getShortCode(), dataTypeTime);			//These two always go in the set of realtime types
		availableDataTypes.put(dataTypeDuration.getShortCode(), dataTypeDuration);
		
		ArrayList<SensorBase> sensorsList = getAvailableSensors(true, true);
		int nSensors = sensorsList.size();
		SensorBase sensor = null;
		for(int i=0; i<nSensors; i++){				//Loop through each sensor
			sensor = sensorsList.get(i);
			ArrayList<SensorDataType> dataTypes = new ArrayList<SensorDataType>(sensor.getProvidedTypes().values());		//Get the realtime types this sensor provides
			int nTypes = dataTypes.size();
			SensorDataType dataType;
			for(int j=0; j<nTypes; j++){			//Add each type to the list, ONLY IF the type is not already in the list. We do not want duplicate data types
				dataType = dataTypes.get(j);
				dataType.setEnabled(sensor.isEnabled());	//The data type inherits its enabled state from its parent sensor
				if(shouldAddDataType(dataType)){
					availableDataTypes.put(dataType.getShortCode(), dataType);
				}
			}
		}
	}
	
	private boolean shouldAddDataType(SensorDataType newType) {
		if(!newType.isEnabled()){											//New type is disabled, we do not want it
			return false;
		}
		else if(!availableDataTypes.containsKey(newType.getShortCode())){	//Type does not exist, return true
			return true;
		}
		else{
			SensorDataType type = getDataType(newType.getShortCode());
			if(!type.isEnabled()){											//Type already exists but is disabled, so the new type should replace the existing one
				return true;
			}
			else if(newType.getPriority()>type.getPriority()){				//Type already exists and is enabled, but the new type is a higher priority, so the new type should replace the existing one
				return true;
			}
			else{															//Any other combinations are not useful to us
				return false;
			}
		}
	}

	/**
	 * Returns a list of data types available to the system. This is the list of all unique types provided by all sensors stored in the persistent configuration.
	 * @return			An HashMap of {@link SensorDataType} objects, one for each data type that is available. This is indexed by the type's shortCode. See {@link SensorDataType#getShortCode()}.
	 */
	public HashMap<String, SensorDataType> getAvailableDataTypes(){
		return availableDataTypes;
	}
	
	/**
	 * Returns the data type specified by the provided type code.
	 * @param typeCode		The three-letter unique data type code of the type you wish to retrieve.
	 * @return				The {@link SensorDataType}, or null if the specified type does not exist.
	 */
	public SensorDataType getDataType(String typeCode){
		return availableDataTypes.get(typeCode);
	}
	
	/**
	 * Use this to check if a particular data type is available.
	 * @param dataType		The data type you wish to check. Must be one of the DATA_TYPE_XX constants defined in your custom SensorsManager.
	 * @return				Boolean, true if this data type is available, false otherwise.
	 */
	public boolean isDataTypeAvailable(String dataType){
		return getAvailableDataTypes().containsKey(dataType);
	}
	
	/**
	 * Use this to check if a particular data type is connected (being logged by one or more sensors on the system). This is useful for when you need to check if a virtual sensor will have access to the data it needs in order to run.
	 * @param dataType		The data type you wish to check. Must be one of the DATA_TYPE_XX constants defined in your custom SensorsManager.
	 * @return				Boolean, true if this data type is available, false otherwise.
	 */
	public boolean isDataTypeConnected(String dataType){
		return getConnectedDataTypes().containsKey(dataType);
	}
	
	/**
	 * Returns the list of all unique data types provided by all sensors currently connected to the system. This can be used to retrieve the list of data types in the current track, for example.
	 * @return			An HashMap of {@link SensorDataType} objects, one for each data type that is currently connected to the system. This is indexed by the type's shortCode. See {@link SensorDataType#getShortCode()}.
	 */
	public HashMap<String, SensorDataType> getConnectedDataTypes(){
		HashMap<String, SensorDataType> connectedDataTypes = new HashMap<String, SensorDataType>();
		connectedDataTypes.put(dataTypeTime.getShortCode(), dataTypeTime);
		
		ArrayList<SensorBase> sensorsList = getConnectedSensors();
		int nSensors = sensorsList.size();
		for(int i=0; i<nSensors; i++){				//Loop through each sensor
			ArrayList<SensorDataType> dataTypes = new ArrayList<SensorDataType>(sensorsList.get(i).getProvidedTypes().values());		//Get the types this sensor provides
			int nTypes = dataTypes.size();
			for(int j=0; j<nTypes; j++){			//Add each type to the list, ONLY IF the type is not already in the list. We do not want duplicate data types
				if(!connectedDataTypes.containsKey(dataTypes.get(j).getShortCode())){
					connectedDataTypes.put(dataTypes.get(j).getShortCode(), dataTypes.get(j));
				}
			}
		}
		return connectedDataTypes;
	}
	
	/**
	 * Returns the list of all sensor log files available
	 * @return		An ArrayList of File objects, each representing a log file created by a sensor
	 */
	public HashMap<String, File> getAvailableLogFiles(){
		HashMap<String, File> logFiles = new HashMap<String, File>();
		//Get an iterator that will allow us to loop through each currently connected sensor
		Iterator<SensorBase> iterator = sensorsCurrentlyConnectedList.iterator();
		SensorBase sensor = null;
		ArrayList<File> sensorLogFiles = null;
		Iterator<File> fileIterator = null;
		File file = null;
		while(iterator.hasNext()){
			sensor = iterator.next();
			sensorLogFiles = sensor.getLogFiles();
			fileIterator = sensorLogFiles.iterator();
			while(fileIterator.hasNext()){
				file = fileIterator.next();
				if(file.length()>0){		//Only add the file to the list if it exists and is not 0-length 
					logFiles.put(file.getName(), file);
				}
			}
		}
		if(saveLapData){					//If lap data recording is enabled, add the laps log file to the list
			logFiles.put(FILE_LAP_DATA, lapDataFile);
		}
		return logFiles;
	}
	
	/**
     * Creates new instances of any sensor class that extends {@link SensorBase} . You MUST override this class and place code in your method that checks the sensor type and returns a new object from the appropriate class.
     * 
     * @param sensorType			The type of sensor to instantiate. You should define Integer codes for each of your sensors somewhere (a good place to do this is in your custom implementation of the {@link SensorsManager} class).
     * @param sensorAddress			The MAC address of the sensor. For internal sensors this should be set to null
     * @return						A sensor that extends the {@link SensorBase} class
     */
	protected SensorBase instantiateSensor(Integer sensorType, String sensorAddress) {
		return null;
	}

	/**
	 * Connects to all available sensors, and optionally start logging data to file. Since some sensors may perform their connection routines asynchronously,
	 * it is not guaranteed that all sensors will be connected once this method has comepleted. Instead, you should provide this SensorsManager with a handler using {@link SensorsManager#setHandler(Handler)},
	 * and listen for a the message with the type {@link SensorsManager#MESSAGE_TYPE_CONNECTION_COMPLETE}.
	 */
	public void connectToAvailableSensors(){
		bluetoothEnabledBeforeOperation = isBluetoothEnabled();
		registerBluetoothReceiver();
		if(dataLoggingDirectory!=null){				//True if a File object was passed to this method
			if(!dataLoggingDirectory.exists()){		//True if this directory does not exist
				dataLoggingDirectory.mkdirs();		//Create it, and any missing parents
			}
		}
		ArrayList<SensorBase> sensorsList = getAvailableSensors(true, true);
		sensorsPendingConnection = new ArrayList<SensorBase>();				//Reinitialise this to prevent old sensors being kept in the list
		int nSensors = sensorsList.size();
		SensorBase sensor = null;
		for(int i=0; i<nSensors; i++){					//Loop through each sensor
			sensor = sensorsList.get(i);
			if(sensor.isEnabled()){						//True if the sensor is enabled in the persistent config
				nSensorsToConnectTo++;
				sensor.setHandler(messageHandler);		//Set the handler so we can receive events and data
/*
				if(dataLoggingDirectory!=null){			//If this SensorsManager has a logging directory, provide it to the sensor
					sensor.setLoggingDirectory(dataLoggingDirectory);
				}
*/
				
												//True if this sensor is an internal one, or bluetooth is already enabled. In this case we can begin connecting immediately
				sensor.connect(thisManager);	//Connect to the sensor and pass it a reference to this SensorsManager. Upon successfull connection, or connection failed, we will receive an event to the Handler messageHandler
				
			}
		}
		if(sensorsPendingConnection.size()>0){		//True if there is at least one sensor that requires bluetooth to be enabled
			//bluetoothAdapter.enable();
		}
		dataStore = new DataStore(this);							//This will hold sensor data
		timer = new Timer();
		TimerTask periodicRefresh = new TimerTask() {
			@Override
			public void run() {
				sendDataToRecordDataInterfaces();
			}
		};
		timer.scheduleAtFixedRate(periodicRefresh, 0, dataMessageInterval);
	}
	
	/**
	 * Disconnects from all connected sensors. Unlike {@link SensorsManager#connectToAvailableSensors(File)}, this method currently exits immediately. This may change in future versions of this package,
	 * but if you set a {@link RecordDataEvents} listener using {@link SensorsManager#addRecordDataListener(RecordDataEvents)}, you can use the event
	 * {@link RecordDataEvents#disconnectionComplete()} to listen for when disconnection is complete, and your code will work both now and with any future versions.
	 */
	public void disconnectFromSensors(){
		//Check that the sensors have been sent the stop recording signal
		if(currentlyRecording){						//True if the signal has not been sent, so we need should send it
			stopRecordingFromSensors();
		}
		timer.cancel();
		//Disconnection from sensors is done in a separate Thread, as some sensors (typically Bluetooth ones) block whilst disconnecting
		Thread disconnectSensorsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Iterator<SensorBase> iterator = sensorsCurrentlyConnectedList.iterator();
				while(iterator.hasNext()){				//Loop through each connected sensor and disconnect from it
					iterator.next().disconnect();
					iterator.remove();
/*
					try{
						sensor.disconnect();
					}
					catch(ConcurrentModificationException e){
						Log.e(TAG, "Exception whilst disconnecting from sensor: "+sensor.getName()+": "+e.toString());
					}
*/
				}
				//Send a message to all registered RecordDataEvents listeners
				Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
				RecordDataEvents tempInterface;
				while(recordInterfacesIterator.hasNext()){
					tempInterface = recordInterfacesIterator.next();
					if(tempInterface!=null){
						tempInterface.disconnectionComplete();		//Inform the parent class that disconnection has been completed
					}
				}
			}
		});
		disconnectSensorsThread.start();
		unRegisterBluetoothReceiver();

	}

	/**
	 * Signals to each connected sensor that it should begin recording and / or transmitting data. If there is at least one sensor connected, this will cause
	 * data messages to be sent to the handler provided at regular intervals. These messages are of the type {@link SensorsManager#MESSAGE_TYPE_DATA}, and the sending interval can be set
	 * by calling {@link SensorsManager#setDataMessageInterval(int)}. If this is not called, the interval defaults to {@link SensorsManager#FREQUENCY_1HZ_MS}.
	 * @param dataLoggingDirectory		A File object representing a directory in the device's file system where the data should be logged. If null, data will not be logged. This logging directory will be created if neccessary
	 
	 * @return		True if recording was successfully started, false otherwise.
	 * 
	 */
	public boolean startRecordingFromSensors(final File dataLoggingDirectory) {
		if(isCurrentlyRecording()){
			return false;
		}
		Log.d(TAG, "Start recording from sensors");
		this.dataLoggingDirectory = dataLoggingDirectory;

		lastLapTimestamp = System.currentTimeMillis();
		if(dataLoggingDirectory!=null && saveLapData){
			lapDataFile = new File(dataLoggingDirectory, FILE_LAP_DATA);
			try {
				lapDataFileWriter = new FileWriter(lapDataFile);
				lapDataFileWriter.append("{\n");
			}
			catch (IOException e) {
				Log.e(TAG, "Error opening lap file data FileWriter: "+e.toString());
			}
		}
		currentLap = 0;
		lastPointLap = 0;
		lapData = new DataStore(this);
		summaryData = new DataStore(this);
		dataStore.setStartTime(System.currentTimeMillis());		//Set the start time of this DataStore. This is used for calculating the track's duration
		ArrayList<SensorBase> connectedSensors = getConnectedSensors();
		int nConnectedSensors = connectedSensors.size();
		for(int i=0; i<nConnectedSensors; i++){
			connectedSensors.get(i).startRecording(dataLoggingDirectory);
		}

		currentlyRecording = true;
		
		//Send a message to all registered RecordDataEvents listeners
		Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
		RecordDataEvents tempInterface;
		while(recordInterfacesIterator.hasNext()){
			tempInterface = recordInterfacesIterator.next();
			if(tempInterface!=null){
				tempInterface.recordingStarted();		//Inform the parent class that recording has started
			}
		}
		return true;
	}

	/**
	 * Use this to check if this {@link SensorsManager} is currently recording data.
	 * @return		True if a data is being recorded, false otherwise.
	 */
	public boolean isCurrentlyRecording(){
		return currentlyRecording;
	}
	
	/**
	 * Use this to request that this {@link SensorsManager} send any sensor data it has to any connected {@link RecordDataEvents} interfaces.
	 * This is useful if you wish to get data as soon as possible, without waiting for the next periodic update. Note that the resulting
	 * call to {@link RecordDataEvents#newSensorData(DataStore)} will be synchronous.
	 */
	public void requestImmediateDataUpdate(){
		sendDataToRecordDataInterfaces();
	}
	
	/**
	 * Signals to each sensor that a new lap has been started. It is up to each sensor to decide how it will react to this signal. Some sensors may make some data about the previous lap
	 * publicly available by calling {@link SensorBase#storeLapValue(String, Object)}.
	 * @param fromSensor	Set this to true if this call has originated from a sensor, otherwise set this to false. If true, {@link RecordDataEvents#newLapDetected(DataStore, int)} will
	 * 						be called in every registered {@link RecordDataEvents} interface. If false, it will not.
	 */
	public void newLap(boolean fromSensor){
		if(saveLapData){
			long timestamp = System.currentTimeMillis();
			long lapDuration = timestamp - lastLapTimestamp;
			lastLapTimestamp = timestamp;
			currentLap++;
			Iterator<SensorBase> iterator = getConnectedSensors().iterator();
			while(iterator.hasNext()){										//Loop through each sensor and send the new lap signal
				iterator.next().newLapDetected(currentLap, lapDuration);	//Calling this will signal to each sensor that it should publish its lap-specific data now
			}
			//Check if there is any data to broadcast to the sensors
			if(lapData==null){
				lapData = new DataStore(this);
			}
			lapData.put(DATA_TYPE_DURATION, lapDuration);
			
			iterator = getConnectedSensors().iterator();
			while(iterator.hasNext()){								//Loop through each sensor again and send the new lap data available signal
				iterator.next().newLapDataAvailable(timestamp, currentLap, lapData);		//Calling this will signal to each sensor that there is new lap data available
			}
			//Save the lap data to file
			if(lapDataFileWriter!=null){
				try {
					String jsonToWrite = "\""+currentLap+"\":"+lapData.getJSONObject().toString();
					if(currentLap>1){					//Ad a comma if this is not the first JSON object
						jsonToWrite = ","+jsonToWrite;
					}
					lapDataFileWriter.append(jsonToWrite);
				}
				catch (IOException e) {
					Log.e(TAG, "Error writing lap data to file: "+e.toString());
				}
				catch (JSONException e) {
					Log.e(TAG, "Error retrieving lap JSON: "+e.toString());
				}
			}
			
			if(fromSensor){
				//Send a message to all registered RecordDataEvents listeners
				Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
				RecordDataEvents tempInterface;
				while(recordInterfacesIterator.hasNext()){
					tempInterface = recordInterfacesIterator.next();
					if(tempInterface!=null){		//Notify each interface that connection has been completed
						tempInterface.newLapDetected(lapData, currentLap);
					}
				}
			}
			
			//Finally, clear the lap data, ready for the next lap
			lapData.clear();
		}
	}
	
	/**
	 * Returns the current value of the requested data type.
	 * @param key		The data type to retrieve. Must be one of the DATA_TYPE_XX constants defined in this class, such as {@link SensorsManager#DATA_TYPE_CURRENT_TIME}.
	 * @return			The value, or null if not available. Note that the value is returned as an Object, and it is up to your code to attempt to cast it to anther type.
	 */
	public Object getDataValue(String key){
		if(dataStore!=null){
			return dataStore.get(key);
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns a set of all available summary data (from all sensors). Summary data are values that sensors may have calculated during the track
	 * that are not suitable for periodic logging (a good example is that whilst current heart rate should be logged periodically, average heart rate
	 * over the entire track does not need to be logged periodically, and is more suited to just being reported on request).
	 * It is up to each sensor to decide what summary data it returns, and a sensor does not have to return any summary data.
	 * @return		A HashMap of summary data values (stored as Objects), indexed by their type code. This HashMap may be empty (initialised but 0 size).
	 */
	public HashMap<String, Object> getSummaryData(){
		return summaryData;
	}

	/**
	 * Signals to each connected sensor that it should stop recording and / or transmitting data. Once you call this method, you will not receive any more sensor data.
	 * Additionally, when this method is called, each sensor may optionally perform some final processing (such as generating a summary report of the data it has collected).
	 * If you want your sensor to do something when this method is called, you should override {@link SensorBase#stopRecording()} and place your code in your overriding method.
	 * @return		True if recording was successfully stopped, false otherwise.
	 */
	public boolean stopRecordingFromSensors() {
		if(!isCurrentlyRecording()){
			return false;
		}
		newLap(false);							//Send the new lap signal to ensure that the last lap is properly handled
		
		long trackDuration = dataStore.getDuration();
		
		Iterator<SensorBase> iterator = getConnectedSensors().iterator();
		while(iterator.hasNext()){								//Loop through each sensor and send the recording stopped signal
			iterator.next().stopRecording(trackDuration);
		}
		
		//Check if there is any data to broadcast to the sensors
		if(summaryData==null){
			summaryData = new DataStore(this);
		}
		summaryData.put(DATA_TYPE_DURATION, trackDuration);
		
		long stopTimestamp = System.currentTimeMillis();
		//Check if there is any summary data to broadcast to the sensors
		if(summaryData!=null && summaryData.size()>0){
			iterator = getConnectedSensors().iterator();
			while(iterator.hasNext()){								//Loop through each sensor again and send the summary data to it. This is another loop because we need to send the stop signal to all sensors BEFORE sending the summary data.
				iterator.next().summaryDataAvailable(stopTimestamp, summaryData);
			}
		}
		
		if(lapDataFileWriter!=null && saveLapData){
			try {
				String jsonToWrite = ",\"all\":"+summaryData.getJSONObject().toString();
				lapDataFileWriter.append(jsonToWrite);
				lapDataFileWriter.append("}");
				lapDataFileWriter.close();
			}
			catch (IOException e) {
				Log.e(TAG, "Error closing lap data FileWriter: "+e.toString());
			}
			catch (JSONException e) {
				Log.e(TAG, "Error retrieving summary JSON: "+e.toString());
			}
		}
		
		currentlyRecording = false;
		
		//Send a message to all registered RecordDataEvents listeners
		Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
		RecordDataEvents tempInterface;
		while(recordInterfacesIterator.hasNext()){
			tempInterface = recordInterfacesIterator.next();
			if(tempInterface!=null){
				tempInterface.recordingStopped(summaryData);		//Inform the parent class that recording has stopped and deliver the sumary data as a parameter
			}
		}
		return false;
	}
	
	/**
	 * Disables the Bluetooth adapter this SensorsManager is using.
	 */
	public void disableBluetooth(){
		//bluetoothAdapter.disable();
	}
	
	public boolean storeSummaryValue(SensorBase parentSensor, String dataType, Object dataValue){
		boolean returnCode = false;
		if(parentSensor!=null && shouldAcceptDataType(dataType, parentSensor.getType())){
			if(summaryData!=null){
				summaryData.put(dataType, dataValue);
				returnCode = true;
			}
		}
		return returnCode;
	}
	
	public boolean storeLapValue(SensorBase parentSensor, String dataType, Object dataValue) {
		boolean returnCode = false;
		Log.d(TAG, "TYPE: "+dataType+", PARENT: "+parentSensor.getType()+", SHOULD ACCEPT: "+shouldAcceptDataType(dataType, parentSensor.getType()));
		if(parentSensor!=null && shouldAcceptDataType(dataType, parentSensor.getType())){
			if(lapData!=null){
				lapData.put(dataType, dataValue);
				returnCode = true;
			}
		}
		return returnCode;
	}
	
	//Private methods from here onwards
	
	/**
	 * Sends any available sensor data and sensor status information to all {@link RecordDataEvents} interfaces that have been registered with this {@link SensorsManager}.
	 */
	private void sendDataToRecordDataInterfaces() {
		if(dataStore!=null){
			dataStore.put(dataTypeTime.getShortCode(), System.currentTimeMillis());				//Always add the timestamp field to the datastore
			if(currentLap!=lastPointLap){		//True if this is the first point after a newLap() event
				dataStore.put(DATA_TYPE_LAP_NUMBER, currentLap);
				lastPointLap = currentLap;
			}
			else{
				dataStore.remove(DATA_TYPE_LAP_NUMBER);
			}
			
			//Collect any available sensor status data
			HashMap<Integer, SensorStatus> statusData = new HashMap<Integer, SensorBase.SensorStatus>();
			Iterator<SensorBase> sensorIterator = getConnectedSensors().iterator();
			SensorBase tempSensor;
			while(sensorIterator.hasNext()){				//Loop through connected sensors
				tempSensor = sensorIterator.next();
				statusData.put(tempSensor.getType(), tempSensor.getStatusData());		//Store the status data in the HashMap
			}
			
			//Send a message to all registered RecordDataEvents listeners
			Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
			RecordDataEvents tempInterface;
			while(recordInterfacesIterator.hasNext()){
				tempInterface = recordInterfacesIterator.next();
				if(tempInterface!=null){		//Notify each interface that is not null
					tempInterface.newSensorData(dataStore);			//Send the sensor data
					tempInterface.newSensorStatusData(statusData);	//Send the sensor status data
				}
			}

			//Send a message to all registered SensorDataListener listeners
			Iterator<SensorDataListener> sensorDataListenersIterator = sensorDataListeners.iterator();
			SensorDataListener tempListener;
			while(sensorDataListenersIterator.hasNext()){
				tempListener = sensorDataListenersIterator.next();
				if(tempListener!=null){		//Notify each interface that is not null
					sendDataToSensorDataListener(tempListener);
				}
			}
		}
	}
	
	private void sendDataToSensorDataListener(SensorDataListener sensorDataListener) {
		ArrayList<String> requestedTypes = sensorDataListener.getRequestedDataTypes();
		if(requestedTypes!=null){
			Iterator<String> iterator = requestedTypes.iterator();
			String type;
			DataStore newData = new DataStore(this);
			while(iterator.hasNext()){
				type = iterator.next();
				if(dataStore.containsKey(type)){			//True if this requested type is available
					newData.put(type, dataStore.get(type));	//Copy into the new data store
				}
				if(!newData.isEmpty()){						//If at least one requested type has been found, send the new data to the listener
					sensorDataListener.newSensorData(newData);
				}
			}
		}
	}

	/**
	 * Scans for any internal sensors that have not already been added to the system, and notifies a {@link SensorScanListener} when new sensors are found.
	 * To be informed of any new sensors found, create a {@link SensorScanListener} in your code and pass it as a parameter to {@link SensorsManager#scanForNewSensors(SensorScanListener, ArrayList).
	 * When a new sensor is found, the method {@link SensorScanListener#newSensorFound(SensorBase, boolean)} will be called in your listener, with the new sensor provided as a parameter.
	 */
	private void scanForInternalSensors(){
		//Note that the Sensor and SensorManager classes referenced in this method are the Android built in ones, not the classes defined in this project
		//First, get a list of internal sensors present on the device
		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> supportedSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);	//Request details of all sensors present on the device
		Iterator<Sensor> iterator = supportedSensors.iterator();
		Sensor tempSensor;
		while(iterator.hasNext()){
			tempSensor = iterator.next();
			int sensorType = tempSensor.getType();
			/*
			Log.d(TAG, "NAME: "+tempSensor.getName());
			Log.d(TAG, "INTERVAL: "+tempSensor.getMinDelay());
			Log.d(TAG, "RANGE: "+tempSensor.getMaximumRange());
			Log.d(TAG, "RESOLUTION: "+tempSensor.getResolution());
			*/
			//Check if this type of sensor was requested
			if(desiredSensorTypesList==null || desiredSensorTypesList.contains(sensorType)){	//True if the list of desired types was not provided (meaning all types were requested), or if this Sensor's type was found in the list of desired types
				//Check if this type of sensor is already connected
				if(!isSensorTypeAvailable(sensorType)){										//True if this type of sensor is NOT already connected to the system
					SensorBase sensor = instantiateSensor(sensorType, null);				//Attempt to instantiate this sensor					
					if(sensor!=null){														//True if we successfully instantiated a sensor. This means the sensor's type was found in the set of SENSOR_TYPE_XX constants defined in this class AND
																							//there exists code in the overridden instantiateSensor() method in the custom SensorsManager class for instantiating this type of sensor
						if(automaticallyAddSensors){										//True if the calling class requested that sensors be added automatically
							addSensorToConfiguration(sensor);
						}
						//Send a message to all registered SensorScanEvents listeners
						Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
						SensorScanEvents tempInterface;
						while(scanInterfacesIterator.hasNext()){
							tempInterface = scanInterfacesIterator.next();
							if(tempInterface!=null){		//If this interface is not null, notify it about the sensor we found
								tempInterface.newSensorFound(sensor, false);
							}
						}
					}
				}
			}			
		}
	}
	
	/**
	 * Scans for any available virtual sensors that have not already been added to the system, and notifies a {@link SensorScanListener} when new sensors are found.
	 * To be informed of any new sensors found, create a {@link SensorScanListener} in your code and pass it as a parameter to {@link SensorsManager#scanForNewSensors(SensorScanListener, ArrayList).
	 * When a new sensor is found, the method {@link SensorScanListener#newSensorFound(SensorBase, boolean)} will be called in your listener, with the new sensor provided as a parameter.
	 */
	private void scanForVirtualSensors(){
		Integer sensorType = null;
		Iterator<Integer> iterator = virtualSensors.iterator();
		while(iterator.hasNext()){
			sensorType = iterator.next();
			//Check if this type of sensor was requested
			if(desiredSensorTypesList==null || desiredSensorTypesList.contains(sensorType)){	//True if the list of desired types was not provided (meaning all types were requested), or if this Sensor's type was found in the list of desired types
				//Check if this type of sensor is already connected
				if(!isSensorTypeAvailable(sensorType)){											//True if this type of sensor is NOT already connected to the system					
					SensorBase sensor = instantiateSensor(sensorType, null);				//Attempt to instantiate this sensor					
					if(sensor!=null){														//True if we successfully instantiated a sensor. This means the sensor's type was found in the set of SENSOR_TYPE_XX constants defined in this class AND
																							//there exists code in the overridden instantiateSensor() method in the custom SensorsManager class for instantiating this type of sensor
						if(automaticallyAddSensors){										//True if the calling class requested that sensors be added automatically
							addSensorToConfiguration(sensor);					
						}
						//Send a message to all registered SensorScanEvents listeners
						Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
						SensorScanEvents tempInterface;
						while(scanInterfacesIterator.hasNext()){
							tempInterface = scanInterfacesIterator.next();
							if(tempInterface!=null){		//If this interface is not null, notify it about the sensor we found
								tempInterface.newSensorFound(sensor, false);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Scans for any Bluetooth sensors that have not already been added to the system, and notifies a {@link SensorScanListener} when new sensors are found. If Bluetooth is not enabled on the device,
	 * it will be enabled automatically. If Bluetooth had to be enabled, then it will be disabled once the scan is complete.
	 */
	private void startBluetoothSensorScan(){
	/*	if(bluetoothAdapter!=null){						//True if there is a Bluetooth adapter on this device
			registerBluetoothReceiver();
			if(!bluetoothAdapter.isEnabled()){			//True if the Bluetooth adapter is not enabled
				if(bluetoothAdapter.enable()){			//True if the adapter was enabled successfully
					//The adapter will take some time to enable, so we will now listen for it becoming ready in the receiver
				}
				else{									//Failed to enable the adapter
					bluetoothScanComplete();			//Signal the end of the scan
				}
			}
			else{										//True if the adapter is already enabled
				bluetoothEnabledBeforeScan = true;		//This flag will prevent the adapter from being disabled when the scan is complete
				bluetoothAdapter.startDiscovery();		//Start discovery
			}
		}
		else{											//No Bluetooth adapter, so we cannot continue
			//Send a message to all registered SensorScanEvents listeners
			Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
			SensorScanEvents tempInterface;
			while(scanInterfacesIterator.hasNext()){
				tempInterface = scanInterfacesIterator.next();
				if(tempInterface!=null){		//If this interface is not null, notify it about the sensor we found
					tempInterface.sensorScanFinished();		//If a listener was provided, notify it that the scan has finished. In this case, we couldn't scan for Bluetooth sensors, but we still need to send the finished signal
				}
			}
		}*/
	}
/*	
	/**
	 * Check if this device supports Bluetooth Low-Energy. BTLE requires API level 18 (4.3) or higher and a compatible device.
	 * @return		True if BTLE is supported, false otherwise.
	 */
/*
	@TargetApi(18)
	private boolean deviceHasBtLe(){
		if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){			//BTLE might be available
			return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);	//BTLE is available
		}
		else{						//BTLE not available on API 17 and below
			return false;
		}
	}

	@TargetApi(18)
	private void startBtLeScan() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bluetoothAdapter.stopLeScan(btLeScanCallback);
				bluetoothScanComplete();
			}
		}, 60000);
		bluetoothAdapter.startLeScan(btLeScanCallback);
	}
	
	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback btLeScanCallback = new LeScanCallback() {
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.d(TAG, "BTLE Device: "+device.toString());
			bluetoothAdapter.stopLeScan(btLeScanCallback);
			device.connectGatt(context, false, btLeGattCallback);
		}
	};
	
	@SuppressLint("NewApi")
	private BluetoothGattCallback btLeGattCallback = new BluetoothGattCallback() {
		@Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if(newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server");
                Log.i(TAG, "Attempting to start service discovery:" +gatt.discoverServices());

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }
		
		@Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.d(TAG, "Services event: "+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	Iterator<BluetoothGattService> iterator = gatt.getServices().iterator();
            	BluetoothGattService gattService;
            	while(iterator.hasNext()){
            		gattService = iterator.next();
            		Log.d(TAG, "BTLE SERVICE UUID: "+gattService.getUuid());
            	}
            }
        }
	};
*/

	/**
	 * Called once a Bluetooth device scan is complete. This disables Bluetooth if it was disabled when the scan was requested, and calls removes any IntentFilters that were listening for Bluetooth events.
	 */
	private void bluetoothScanComplete(){
/*		//unRegisterBluetoothReceiver();
		if (!bluetoothEnabledBeforeScan){	//True if bluetooth was not enabled before the sensor scan. In this case, we should disable the adapter, basically leaving everything as we found it, to avoid annoying the user
			bluetoothAdapter.disable();			//Turn off the bluetooth adapter
		}
		//Send a message to all registered SensorScanEvents listeners
		Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
		SensorScanEvents tempInterface;
		while(scanInterfacesIterator.hasNext()){
			tempInterface = scanInterfacesIterator.next();
			if(tempInterface!=null){		//If this interface is not null, notify it about the sensor we found
				tempInterface.sensorScanFinished();		//If a listener was provided, notify it that the scan has finished
			}
		}
		desiredSensorTypesList = null;			//Clear this list just ot be on the safe side
		if(bluetoothAdapter.isDiscovering()){
			bluetoothAdapter.cancelDiscovery();
		}
		unRegisterBluetoothReceiver(); */
	}
	
	/**
	 * Registers the IntentFilters that listen for various Bluetooth events
	 */
	private void registerBluetoothReceiver(){

	}
	
	/**
	 * Unregisters the IntentFilters that listen for various Bluetooth events
	 */
	private void unRegisterBluetoothReceiver(){
		try{
			context.unregisterReceiver(bluetoothReceiver);
		}
		catch (IllegalArgumentException e){
			Log.d(TAG, "Receiver already unregistered");
		}
	}
	
	/**
	 * Called every time a sensor has finished attempting to connect. Once all sensors have "reported in",
	 * then the event {@link RecordDataEvents#connectionComplete()} is fired in any {@link RecordDataEvents}
	 * listeners that have been added to this SensorsManager.
	 */
	private void receivedConnectionReplyFromSensor(){
		nSensorsReplied++;
		if(nSensorsReplied>=nSensorsToConnectTo){				//True if all sensors have now replied
			//Send a message to all registered RecordDataEvents listeners
			Iterator<RecordDataEvents> recordInterfacesIterator = recordDataInterfaces.iterator();
			RecordDataEvents tempInterface;
			while(recordInterfacesIterator.hasNext()){
				tempInterface = recordInterfacesIterator.next();
				if(tempInterface!=null){		//Notify each interface that connection has been completed
					tempInterface.connectionComplete();
				}
			}
			if(!bluetoothEnabledBeforeOperation){				//Bluetooth was disabled before we started the connection process. So we should check to see if any bluetooth sensors were successfully connected to. If not, we should disable bluetooth
				Iterator<SensorBase> iterator = getConnectedSensors().iterator();
				boolean bluetoothSensorConnected = false;
				while(iterator.hasNext()){
					if(iterator.next().isExternal()){
						bluetoothSensorConnected = true;
						break;
					}
				}
				if(!bluetoothSensorConnected){					//True if no bluetooth sensors are connected

				}
			}
			nSensorsReplied = 0;			//Reset both counters, ready for another connection in the future
			nSensorsToConnectTo = 0;
		}
	}
	
	/**
	 * A Handler that receives events and messages from various sensors
	 */
	@SuppressLint("HandlerLeak")
	private Handler messageHandler = new Handler(){
		public void handleMessage(Message message){
    		int messageType = message.what;
    		Bundle messageData = message.getData();
    		int originatingSensorType = message.arg1;
    		switch(messageType){
    		case MESSAGE_TYPE_SENSOR_CONNECTION_SUCCEEDED:		//A Sensor has been connected successfully
				SensorBase connectedSensor = (SensorBase) messageData.getSerializable("sensor");
    			if(connectedSensor!=null){
    				sensorsCurrentlyConnectedList.add(connectedSensor);
    			}
    			receivedConnectionReplyFromSensor();
    			break;
    		case MESSAGE_TYPE_SENSOR_CONNECTION_FAILED:			//Connection to a sensor failed
    			receivedConnectionReplyFromSensor();
				break;
/*
    		case MESSAGE_TYPE_SENSOR_DISCONNECTED:	//A Sensor has been disconnected
    			SensorBase disconnectedSensor = (SensorBase) messageData.getSerializable("sensor");
    			if(disconnectedSensor!=null){
    				sensorsCurrentlyConnectedList.remove(disconnectedSensor);
    			}
    			break;
*/
    		case MESSAGE_TYPE_DATA:					//We received a data message
    			if(dataStore!=null){
        			Set<String> messageDataTypes = messageData.keySet();		//This is the list of all extra variables in the bundle
    				String dataType = null;
    				//Integer decimalPlaces = null;
    				Iterator<String> messageDataTypesIterator = messageDataTypes.iterator();
    				while(messageDataTypesIterator.hasNext()){					//Loop through each type and remove any that were not requested
    					dataType = messageDataTypesIterator.next();
    					if(shouldAcceptDataType(dataType, originatingSensorType)){					//Only handle this data type if it is in the list of available types, and the types in the list was declared by the sensor that sent this message
	            			if(allDataTypesRequested || requestedDataTypes.contains(dataType)){		//True if either all types have been requested, OR this type is in the list of requested types
	    						SensorDataType sensorDataType = availableDataTypes.get(dataType);
	    						if(sensorDataType!=null){
	    							Integer variableType = sensorDataType.getVariableType();
	    							//Check what kind of variable this value is
	    							if(variableType!=null){
	    								switch(variableType){
	    								case SensorDataType.VARIABLE_TYPE_INTEGER:
	    									Integer valueInt = messageData.getInt(dataType);
	    									if(valueInt!=null){
	    										dataStore.put(dataType, valueInt);
	    									}
	    									break;
	    								case SensorDataType.VARIABLE_TYPE_LONG:
	    									Long valueLong = messageData.getLong(dataType);
	    									if(valueLong!=null){
	    										dataStore.put(dataType, valueLong);
	    									}
	    									break;
	    								case SensorDataType.VARIABLE_TYPE_FLOAT:
	    									Float valueFloat = messageData.getFloat(dataType);
	    									if(valueFloat!=null){
	    										dataStore.put(dataType, valueFloat);
	    									}
	    									break;
	    								case SensorDataType.VARIABLE_TYPE_DOUBLE:
	    									Double valueDouble = messageData.getDouble(dataType);
	    									if(valueDouble!=null){
	    										dataStore.put(dataType, valueDouble);
	    									}
	    									break;
	    								default:
	    									break;
	    								}
	    							}
	    						}
	    					}
	    				}
    				}
    			}
    			break;
    		case MESSAGE_TYPE_ERROR:				//We received an error message
    			break;
    		default:
    			break;
    		}
		}
	};
	
	/**
	 * Receives broadcast intents from the Android system regarding Bluetooth status events
	 */
	private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			String action = intent.getAction();
			//When discovery finds a device
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				//Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device!=null && device.getName()!=null){									//True if we got a valid device from the intent, AND it has a valid name
					String deviceNameCode = device.getName().substring(0, 2);				//We identify the types of Bluetooth sensors by looking at the first two characters in the device's Bluetooth display name
					Integer sensorType = getExternalSensorTypeFromNameCode(deviceNameCode);	//Look up this sensor by its name code
					if(sensorType!=null){													//True if this sensor is one we have a definition for
						//Check if this type of sensor was requested
						if(desiredSensorTypesList==null || desiredSensorTypesList.contains(sensorType)){		//True if the list of desired types was not provided (meaning all types were requested), or if this Sensor's type was found in the list of desired types
							//Check if this type of sensor is already connected
							if(!isSensorTypeAvailable(sensorType)){									//True if this type of sensor is NOT already connected to the system
								String sensorAddress = device.getAddress();							//We will need the MAC address of the sensor
								SensorBase sensor = instantiateSensor(sensorType, sensorAddress);
								//Check if this device has already been paired to the phone
								boolean sensorRequiresPairing = false; //!bluetoothAdapter.getBondedDevices().contains(device);
								if(sensor!=null){
									if(automaticallyAddSensors){							//True if the calling class requested that sensors be added automatically
										addSensorToConfiguration(sensor);					
									}
									//Send a message to all registered SensorScanEvents listeners
									Iterator<SensorScanEvents> scanInterfacesIterator = sensorScanInterfaces.iterator();
									SensorScanEvents tempInterface;
									while(scanInterfacesIterator.hasNext()){
										tempInterface = scanInterfacesIterator.next();
										if(tempInterface!=null){							//If a listener was provided, notify it that we have found a new sensor
											tempInterface.newSensorFound(sensor, sensorRequiresPairing);
										}
									}
								}
							}
						}
					}
				}
			}
			else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){	//True if a device was just paired or unpaired
				int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);	//Get the bond state
				if(bondState==BluetoothDevice.BOND_BONDED && sensorPendingPairing!=null){
					//Get the BluetoothDevice object from the Intent				
					BluetoothDevice bondedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String bondedDeviceMAC = bondedDevice.getAddress();
					//Check if the MAC address of the device in the event matches the MAC of the device we wish to pair with
					if(bondedDeviceMAC.equals(sensorPendingPairing.getAddress())){		//True if this is the device that we initiated a pairing request with
						addSensorToConfiguration(sensorPendingPairing);
						//sensorPendingPairing.disconnect();
						sensorPendingPairing = null;
						standardBluetoothScanComplete();				//The scan is complete at this stage
					}
				}
			}
			else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){	//True if the device's bluetooth adapter has changed state
				int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				if(newState==BluetoothAdapter.STATE_ON){					//True if the adapter is now on
					if(sensorsPendingConnection.size()>0){					//True if there are sensors waiting to be connected before a track may begin
						Iterator<SensorBase> iterator = sensorsPendingConnection.iterator();
						while(iterator.hasNext()){							//Begin connecting to each sensor
							iterator.next().connect(thisManager);
						}
					}
					else if(!bluetoothAdapter.isDiscovering()){				//True if the adapter is not currently doing a scan. This is just here for safety
						bluetoothAdapter.startDiscovery();					//Start the bluetooth scan. Any new devices found will be received by this receiver 
					}
				}				
			}
			else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){	//True if discovery has just started
			}
			else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){	//True if discovery has just finished
				if(sensorPendingPairing==null){									//Turn off Bluetooth if there are no sensors pending pairing
					standardBluetoothScanComplete();
				}
			}
		*/}
	};
	
	/**
	 * An interface that classes may implement when requesting a sensor scan. The SensorsManager will notify this interface when
	 * events occur during the scan. There are three events that may occur: Scan started, scan finished, and new sensor found. Scan started and scan
	 * finished events are signalled using {@link SensorScanEvents#sensorScanStarted} and {@link SensorScanEvents#sensorScanFinished}, 
	 * When a new sensor is discovered, {@link SensorScanEvents#newSensorFound(SensorBase, boolean)} is called.
	 * @author Tom
	 *
	 */
	public interface SensorScanEvents{
		/**
		 * Called when the a sensor scan has started.
		 */
		public void sensorScanStarted();
		
		/**
		 * Called when the a sensor scan has finished.
		 */
		public void sensorScanFinished();
		
		/**
		 * Called when a new sensor has been found.
		 * @param sensor			An object from any class that extends the {@link SensorBase} class
		 * @param requiresPairing	Whether or not this sensor needs to be paired to the device in order to be added to the system. For internal sensors this will always be false
		 */
		public void newSensorFound(SensorBase sensor, boolean requiresPairing);
	}
	
	/**
	 * An interface that classes may implement when recording data. The SensorsManager will notify this interface when events occur during the recording session.
	 * @author Tom
	 *
	 */
	public interface RecordDataEvents{
		
		/**
		 * Called when a sensor has been connected to successfu.lly.
		 * @param sensor		The sensor that was connected to.
		 */
		public void sensorConnectionSucceeded(SensorBase sensor);
		
		/**
		 * Called when a sensor failed to connect.
		 * @param sensor		The sensor that failed to connect.
		 */
		public void sensorConnectionFailed(SensorBase sensor);
		
		/**
		 * Called when a sensor has been disconnected from successfully.
		 * @param sensor		The sensor that was disconnected from.
		 */
		public void sensorDisconnected(SensorBase sensor);
		
		/**
		 * Called when all sensors have been connected to (more specifically, all sensors have either succeeded or failed to connect, and the track is now ready to begin recording).
		 */
		public void connectionComplete();
		
		/**
		 * Called when all sensors have been disconnected from.
		 */
		public void disconnectionComplete();
		
		/**
		 * Called when a track has started recording.
		 */
		public void recordingStarted();
		
		/**
		 * Called when a track has stopped recording.
		 * @param summaryData	A {@link DataStore} object containing any summary data that was generated by the sensors that were used during the track.
		 */
		public void recordingStopped(DataStore summaryData);
		
		/**
		 * Called when a new lap has been detected and any lap-specific data has been generated.
		 * @param lapData		A {@link DataStore} object containing any lap-specific data that was generated.
		 * 						See {@link SensorsManager#newLap()} for more details.
		 * @param lapNumber		The new lap's number. Numbering starts from 1.
		 */
		public void newLapDetected(DataStore lapData, int lapNumber);
		
		/**
		 * Called when the SensorsManager has new sensor data.
		 * @param newData		A {@link DataStore} object, containing all available sensor data.
		 */
		public void newSensorData(DataStore newData);
		
		/**
		 * Called when the SensorsManager has new sensor status data. This data will typically include the battery level for external sensors and other similar information.
		 * @param sensorStatusData		An {@link HashMap} containing one {@link SensorStatus} object per connected sensor, indexed by sensor type code.
		 */
		public void newSensorStatusData(HashMap<Integer, SensorStatus> sensorStatusData);
		
		/**
		 * Called when the SensorsManager has detected an error during recording. Currently does nothing.
		 * @param errorCode
		 */
		public void recordingError(int errorCode);
	}
	
	public interface SensorDataListener{
		
		public void newSensorData(DataStore newData);
		
		public ArrayList<String> getRequestedDataTypes();
	}

	protected void standardBluetoothScanComplete() {
		bluetoothScanComplete();
	}
	
	protected boolean shouldAcceptDataType(String dataType, int originatingSensorType) {
		SensorDataType type = getDataType(dataType);
		if(type==null){									//This type is not listed in the set of available types, so we don't want it
			return false;
		}
		else{
			if(type.getParentSensor().getType()==originatingSensorType){		//True if the sensor that created this data type is the same as the originating sensor, so we want it
				return true;
			}
			else{																//The sensor that created this data type is NOT the same as the originating sensor, so we don't want it 
				return false;
			}
		}
	}

	/**
	 * A special sensor that is only used as the parent to the data types DURATION and TIME. It does nothing useful (like a Frenchman).
	 */
	private class SensorManagerSensor extends SensorBase{
		private static final long serialVersionUID = 1L;
		public SensorManagerSensor(Context context){
			super();
			TAG = "SensorManagerSensor";

			//Fill in this sensor's information
			setSensorType(SensorsManager.SENSOR_TYPE_SENSOR_MANAGER);
			setSensorName("Sensor manager");
			setSensorDescription("Provides some common datatypes, such as duration.");
			setIsVisible(false);			//We don't want to see this sensor in any config dialogs
		}
	}
	
	//The two common SensorDataTypes that this manager uses
	
	public static class TypeCurrentTime extends SensorDataType{
		private static final long serialVersionUID = 1L;
		public TypeCurrentTime() {
			super(DATA_TYPE_CURRENT_TIME, "Timestamp", "ms", 0, SensorDataType.VARIABLE_TYPE_LONG, SensorDataType.VARIABLE_CLASS_REALTIME);
		}
	}
	
	public static class TypeDuration extends SensorDataType{
		private static final long serialVersionUID = 1L;
		public TypeDuration() {
			super(DATA_TYPE_DURATION, "Duration", "ms", 0, SensorDataType.VARIABLE_TYPE_LONG, SensorDataType.VARIABLE_CLASS_SUMMARY);
		}
	}
}