package se.sics.sensorservices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import se.sics.sensorservices.SensorsManager.RecordDataEvents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

/**
 * <p>A base class that can be extended in order to create a new sensor. This class includes all methods required for communication with a {@link SensorsManager}.
 * To create a new sensor, you should extend this class:</p>
 * <pre>public class MyCustomSensor extends SensorBase{};</pre>
 * <p>In your sensor's constructor, you must call the superclass's constructor:</p>
 * <pre>public MyCustomSensor(Params params){
 * 	super();
 *}</pre>
 *<p>Your custom sensor class will be sent several events by its parent {@link SensorsManager} during its lifetime. These are:</p>
 *<ul>
 *	<li>{@link SensorBase#connect(SensorsManager)}</li>
 *	<li>{@link SensorBase#startRecording()}</li>
 *	<li>{@link SensorBase#stopRecording()}</li>
 *	<li>{@link SensorBase#disconnect()}</li>
 *</ul>
 *
 * <p>The basic principle is that you place code in {@link SensorBase#connect(SensorsManager)} that handles connecting to the sensor,
 * and code in {@link SensorBase#disconnect()} that handles disconnecting from the sensor.</p>
 * <p>You may also place code in {@link SensorBase#startRecording()} and {@link SensorBase#stopRecording()} that handles starting and stopping of data recording respectively.
 * For example, if your sensor generates some kind of summary report at the end of a recording session, this should be done in {@link SensorBase#stopRecording()}.</p>
 * <p>Finally, your sensor may send and receive "new lap" and "summary" events. For example, your sensor can call {@link SensorsManager#newLap()} to indicate a new lap
 * has occurred, and the event {@link SensorBase#newLapDetected()} may be sent to your sensor at any time. See the documentation of these methods for details on how to use them.</p>
 * 
 * <p>This class, and any subclass derived from it, is {@link Serializable}, so that sensor objects may be stored in {@link Handler} messages or {@link Bundle}s.</p>
 */
public class SensorBase implements Serializable {
	protected String TAG = "SensorBase";
	static final long serialVersionUID = 1L;
	
	//Variables shared by sub-classes

	protected boolean sensorEnabled = true;
	protected boolean sensorVisible = true;
	
	protected boolean connected = false;
	protected boolean recordingEnabled = false;
	
	protected Handler messageHandler = null;

	private HashMap<String, SensorDataType> providedTypes;
	
	protected ArrayList<String> requestedOutputFiles;
	protected ArrayList<File> outputFiles;
	protected HashMap<String, FileWriter> outputFileWriters;
	
	//Information about the sensor. The variables are declared here, and set in the constructors of any class that extends this class
	protected int sensorType = -1;
	protected String sensorName = null;
	protected String sensorDescription = null;
	protected int iconResId;
	
	//These two parameters are onyl set when the sensor is an external (Bluetooth) one
	protected String address = null;
	protected String bluetoothNamePrefix = null;
	
	protected long systemBooted;
	
	protected File loggingDirectory = null;
	
	protected SensorsManager sensorsManager = null;
	
	protected SparseArray<Object> sensorStatusData;

	/**
	 * The default sampling rate is 50HZ. This can be changed to whatever you like, but it must be expressed in microseconds between samples.
	 */
	protected int samplingRate = SensorsManager.FREQUENCY_50HZ_US;
	
	/**
	 * Constructor. Your sensor's constructor may take any set of parameters, but you must call super().
	 */
	public SensorBase() {
		//Get the time at which the system was booted. This is needed as some sensor timestamps are relative to the system boot time
		systemBooted = System.currentTimeMillis() - SystemClock.uptimeMillis();
		providedTypes = new HashMap<String, SensorDataType>();
		requestedOutputFiles = new ArrayList<String>();
		outputFiles = new ArrayList<File>();
		outputFileWriters = new HashMap<String, FileWriter>();
		sensorStatusData = new SparseArray<Object>();
		iconResId = se.sics.sensorsmanager.R.drawable.device_access_bluetooth_searching;		//Default icon
	}
	
	//Public methods that are NOT overridden
	
	/**
	 * Enable or disable this sensor. A disabled sensor will not be connected to by its parent {@link SensorsManager}, and its provided data types will not be available.
	 * @param enabled		True to enable this sensor, false to disable. By default, a sensor is enabled.
	 */
	public void setEnabled(boolean enabled){
		sensorEnabled = enabled;
	}
	
	/**
	 * Sets the visibility status of this sensor. You may retrieve the visibility status using {@link SensorBase#isVisible()}. This status is useful if you have a sensor
	 * that you want to hide from the user, for example a sensor that is always enabled that monitors the status of the phone's battery level.
	 * The visibility status does not affect the operation of the sensor in any way.
	 * @param visible
	 */
	public void setIsVisible(boolean visible){
		sensorVisible = visible;
	}
	
	/**
	 * Sets the sampling rate of this sensor.
	 * @param samplingRate		The sampling rate, expressed in microseconds between samples.
	 * @see SensorsManager
	 */
	public void setSamplingRate(int samplingRate){
		this.samplingRate = samplingRate;
	}
	
	/**
	 * Get the current enabled state of this sensor.
	 * @return				True if this sensor is currently enabled, false otherwise. By default, a sensor is enabled.
	 */
	public boolean isEnabled(){
		return sensorEnabled;
	}
	
	/**
	 * Gets the visibility of the sensor.
	 * @return				True if this sensor is visible, false otherwise.
	 */
	public boolean isVisible(){
		return sensorVisible;
	}

	/**
	 * Returns the type of the sensor. Types are defined in your custom {@link SensorsManager}.
	 * @return		The type of the sensor
	 */
	public int getType() {
		return sensorType;
	}
	
	/**
	 * Returns whether or not this sensor is an external (Bluetooth) one.
	 * @return		True if this is an external sensor.
	 */
	public boolean isExternal(){
		return sensorType>=200;		//If the type is less than 200, this is not an external sensor, so return false. Otherwise, return true
	}
	
	/**
	 * Returns whether or not this sensor is an virtual one.
	 * @return		True if this is an virtual sensor.
	 */
	public boolean isVirtual(){
		return sensorType>=100 && sensorType<200;		//If the type is 100-199, this is a virtual sensor
	}

	/**
	 * Returns the MAC address of this sensor.
	 * @return		The MAC address, in the formt AA:BB:CC:DD:EE:FF, or null if this is not an external sensor.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Returns the set of real-time data types that this sensor provides. These are data types that are available as streaming (constantly updated) variables, such as heart rate, speed, or latitude.
	 * @return		A {@link HashMap}, where the indexes are the data type short codes, and the values are {@link SensorDataType} objects.
	 */
	public HashMap<String, SensorDataType> getProvidedTypes() {
		return providedTypes;
	}

	/**
	 * Returns the name of this sensor.
	 * @return		The name, or null if the name is not set.
	 */
	public String getName() {
		return sensorName;
	}

	/**
	 * Returns this sensor's description.
	 * @return		The description, or null if the name is not set.
	 */
	public String getDescription() {
		return sensorDescription;
	}
	
	/**
	 * Returns a {@link Bitmap} of this sensor's icon. To set the icon, call {@link SensorBase#setSensorIconResourceId(int)}.
	 * @param context		The {@link Context} in which to retrieve the icon.
	 * @return				The icon's {@link Bitmap}, or null if the icon could not be generated.
	 */
	public Bitmap getIcon(Context context){		
		return BitmapFactory.decodeResource(context.getResources(), iconResId);
	}

	/**
	 * Sets the message {@link Handler} that this sensor wil use to send data to its parent {@link SensorsManager}. A Handler is used for communication because the sensor may be collecting data in a separate thread.
	 * @param newHandler		A reference to the {@link Handler}.
	 */
	public void setHandler(Handler newHandler) {
		messageHandler = newHandler;
	}
	
	/**
	 * Sets the directory in which this sensor may create log (output) files.
	 * @param loggingDirectory		A {@link File} object describing the directory this sensor should use for output.
	 */
	public void setLoggingDirectory(File loggingDirectory) {
		this.loggingDirectory = loggingDirectory;		
	}
	
	/**
	 * Returns the list of log (output) files that this sensor has created.
	 * @return		An {@link ArrayList} of {@link File} objects, where each object represents a file that this sensor has created. Any of these files
	 * 				may be empty or may not exist in the file system, so you should check them before attempting to read from them.
	 */
	public ArrayList<File> getLogFiles(){
		return outputFiles;
	}
	
	/**
	 * Connects to this sensor, and also receives a reference to the SensorsManager that is managing this sensor, which this sensor may use to obtain information from other sensors.
	 * You MUST create a method in your sensor class that overrides this method, and place any code you need to run to connect to the sensor in your method.
	 * Your new connect() method must ALSO call super.connect() BEFORE running any other code.
	 */
	public void connect(SensorsManager sensorsManager){
		this.sensorsManager = sensorsManager;
	}
	
	/**
	 * Disconnects from this sensor. You MUST create a method in your sensor class that overrides this method, and place any code you need to run to disconnect from the sensor in your method.
	 * Your new disconnect() method must ALSO call super.disconnect()
	 */
	public void disconnect(){
		connected = false;
	}
	
	/**
	 * Starts recording data from this sensor. This will always be called after connect() has completed succsssfully.
	 * You do not have to override this method, but if you do, you MUST call super.startRecording()
	 */
	public void startRecording(File loggingDirectory){
		this.loggingDirectory = loggingDirectory;
		recordingEnabled = true;
		if(loggingDirectory!=null){		//True if we have a valid directory where we can log data
			//Create a filewriter for each output file that has been requested. Requests for output files should be made in your sensor's constructor, using the method declareOutputFile(fileName)
			FileWriter tempFileWriter = null;
			String fileName = null;
			File logFile = null;
			Iterator<String> iterator = requestedOutputFiles.iterator();
			while(iterator.hasNext()){
				fileName = iterator.next();
				logFile = new File(loggingDirectory, fileName);
				try {
					tempFileWriter = new FileWriter(logFile, false);
					outputFileWriters.put(fileName, tempFileWriter);
					outputFiles.add(logFile);
				}
				catch (IOException e) {
					Log.e(TAG, "Error logging data to file: "+e.toString());
				}
			}
		}
	}
	
	/**
	 * <p>Called by the parent {@link SensorsManager} when a new lap has been detected by one of the sensors.</p>
	 * <p>If you want your sensor to collect data lap-by-lap(for example the average heart rate during the last lap), then you should override this method
	 * and do your calculations in you new method.</p>
	 * </p>If you want to make the results of your calculations available for other sensors to use, you can publish it using {@link SensorManager#storeLapValue(String, Object)}.</p>
	 * <p>You should <b>not</b> check for new lap data manually inside this method, as it is not guaranteed that all sensors will have published their data at this time. Instead,
	 * once all sensors have had the chance to publish data, another method, {@link SensorBase#newLapDataAvailable(HashMap)}, will be called in each sensor.
	 * This will provide the sensor with a {@link HashMap} containing any data published by any sensor during the last lap update.</p>
	 * @param currentLap 		The new lap's number
	 * @param lapDuration 		The lap's duration, in milliseconds. This is provided so that sensors may use it in their processing. It is recommended that you use this provided
	 * 							duration rather calculating it yourself, to guarantee that all sensors have the exact same lap duration.
	 */ 
	public void newLapDetected(int lapNumber, long lapDuration){
	}
	
	/**
	 * Called once all lap data has been collected. If you override this method, you MUST call super.newLapDataAvailable(long lapTimestamp, int lapNumber, DataStore newData)
	 * before doing anything else.
	 * @param lapTimestamp 		The timestamp representing when this new lap was created, expressed in milliseconds since Jan 1st 1970.
	 * @param lapNumber			The new lap's number. Numbering starts from 1.
	 * @param newData			A {@link DataStore} containing any lap data that was collected, where the indexes are the short codes of the variables,
	 * 							and the values are Objects that may be any type. This DataStore may be empty.
	 */
	public void newLapDataAvailable(long lapTimestamp, int lapNumber, DataStore lapData){
	}

	/**
	 * Stops recording data from this sensor. You do not have to override this method, but if you do, you MUST call super.stopRecording()
	 * @param trackDuration		The total duration of the track, in milliseconds. This is provided so that sensors may use it in their processing. It is recommended that you use this provided
	 * 							duration rather calculating it yourself, to guarantee that all sensors have the exact same track duration.
	 */	
	public void stopRecording(long trackDuration){
		if(loggingDirectory!=null){		//True if we have a valid directory where we can log data
			ArrayList<FileWriter> fileWriters = new ArrayList<FileWriter>(outputFileWriters.values());
			Iterator<FileWriter> iterator = fileWriters.iterator();
			while(iterator.hasNext()){
				try {
					iterator.next().close();
				}
				catch (IOException e) {
					Log.e(TAG, "Error closing output file: "+e.toString());
				}
			}
		}
		recordingEnabled = false;
	}
	
	/**
	 * Called once all summary data has been collected. If you override this method, you MUST call
	 * super.summaryDataAvailable(long lapTimestamp, DataStore summaryData) before doing anything else.
	 * @param 	summaryData		A {@link DataStore} containing any summary data that was collected, where the indexes are the short codes of the variables,
	 * 							and the values are Objects that may be any type. This DataStore may be empty.
	 * @param 	timestamp	 	The timestamp representing when this data was collected (i.e. the end of the track)
	 */
	public void summaryDataAvailable(long timestamp, DataStore summaryData){
	}
	
	/**
	 * Returns all available status data pertaining to this sensor.
	 * @return		A {@link SensorStatus} object containing any status data. This object will always contain entries for type, name and description,
	 * but it may not always contain any status values.
	 */
	public SensorStatus getStatusData(){
		return new SensorStatus(sensorType, sensorName, sensorDescription, sensorStatusData);
	}
	
	//Protected methods. Only called from sub-classes that extend this class
	
	/**
	 * Set the sensor's type. Types are defined in your custom {@link SensorsManager}. This should be called in your sensor's constructor.
	 * @param sensorType		The type
	 */
	protected void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}
	
	/**
	 * Set the sensor's name. This should be called in your sensor's constructor.
	 * @param sensorName		The name.
	 */
	protected void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	
	/**
	 * Set the sensor's description. This should be called in your sensor's constructor.
	 * @param sensorName		The description.
	 */
	protected void setSensorDescription(String sensorDescription) {
		this.sensorDescription = sensorDescription;
	}
	
	/**
	 * Set the sensor's icon resource ID. This should be called in your sensor's constructor.
	 * @param sensorName		The resource ID. This should be one of the IDs defined in the generated file R.java.
	 */
	protected void setSensorIconResourceId(int resourceId) {
		this.iconResId = resourceId;
	}
	
	/**
	 * Declares an output type. This should be called in your sensor's constructor. This method should be called once for every data type your sensor provides.
	 * @param dataType			The data type to declare.
	 */
	protected void declareProvidedType(SensorDataType dataType){
		dataType.setParentSensor(this);		//Set the parent sensor of this data type
		providedTypes.put(dataType.getShortCode(), dataType);
	}
	
	/**
	 * Declare an output file. This should be called in your sensor's constructor. The sensor's parent {@link SensorsManager} will create any declared files.
	 * Data may then be written to a file using {@link SensorBase#writeDataToOutputFile(String, String)}. The parent {@link SensorsManager} determines where output files are placed. 
	 * @param sensorName		The name of the file to create. This name is also used to refer to an output file when writing data ({@link SensorBase#writeDataToOutputFile(String, String)).
	 */
	protected void declareOutputFile(String fileName){
		requestedOutputFiles.add(fileName);
	}
	
	/**
	 * Writes a data string to an output file previously declared using {@link SensorBase#declareOutputFile(String)}.
	 * @param fileName		The filename identifier that you wish to write to. This should match one of the filenames provided by a call to {@link SensorBase#declareOutputFile(String)}.
	 * @param data			The data to write to the file. This will be appended to the any existing contents of the file.
	 */
	protected void writeDataToOutputFile(String fileName, String data){
		FileWriter tempFileWriter = outputFileWriters.get(fileName);
		if(recordingEnabled && tempFileWriter!=null){
			try {
				tempFileWriter.append(data);
				tempFileWriter.flush();
			}
			catch (IOException e) {
				Log.e(TAG, "Error writing data to output file: "+e.toString());
			}
		}
	}
	
	protected boolean storeLapValue(String dataType, Object dataValue){
		return sensorsManager.storeLapValue(this, dataType, dataValue);
	}
	
	protected boolean storeSummaryValue(String dataType, Object dataValue){
		return sensorsManager.storeSummaryValue(this, dataType, dataValue);
	}
	
	/**
	 * Stores a piece of sensor status data (such as battery level). Sensor status data is sent by the {@link SensorsManager} to any declared {@link RecordDataEvents} interfaces.
	 * Use {@link SensorBase#getStatusData()} to manually retrieve all status data from this sensor.
	 * @param dataType			A type code that identifies this type of status data. Types should be defined in your custom SensorsManager class.
	 * @param dataValue			The value, may be a string, int, float, double etc etc.
	 */
	protected void storeStatusData(int dataType, Object dataValue){
		sensorStatusData.put(dataType, dataValue);
	}
	
	/**
	 * Signals to the parent {@link SensorsManager} that this sensor was connected to successfully. At some point during your {@link SensorBase#connect(SensorsManager)} method,
	 * you must call either this method, or {@link SensorBase#connectionFailed()}, so that the parent {@link SensorsManager} knows whether or not connection succeeded.
	 */
	protected void connectionSucceeded(){
		connected = true;
		if(messageHandler!=null){
			Bundle dataBundle = new Bundle();
			dataBundle.putSerializable("sensor", this);
			sendMessage(SensorsManager.MESSAGE_TYPE_SENSOR_CONNECTION_SUCCEEDED, dataBundle);
		}		
	}
	
	/**
	 * Signals to the parent {@link SensorsManager} that connection failed to this sensor. At some point during your {@link SensorBase#connect(SensorsManager)} method,
	 * you must call either this method, or {@link SensorBase#connectionSucceeded()}, so that the parent {@link SensorsManager} knows whether or not connection succeeded.
	 */
	protected void connectionFailed(){
		connected = false;
		if(messageHandler!=null){
			Bundle dataBundle = new Bundle();
			dataBundle.putSerializable("sensor", this);
			sendMessage(SensorsManager.MESSAGE_TYPE_SENSOR_CONNECTION_FAILED, dataBundle);
		}
	}
	
	/**
	 * Signals to the parent {@link SensorsManager} that this sensor is now disconnected. At some point during your {@link SensorBase#disconnect()} method you must call this method,
	 * so that the parent {@link SensorsManager} knows the sensor is disconnected. Even if there a problem occurs during disconnection, you should still call this method. 
	 */
/*
	protected void disconnected(){
		if(messageHandler!=null){
			Bundle dataBundle = new Bundle();
			dataBundle.putSerializable("sensor", this);
			sendMessage(SensorsManager.MESSAGE_TYPE_SENSOR_DISCONNECTED, dataBundle);
		}
	}
*/
	/**
	 * Sends a message to the sensor's parent {@link SensorsManager}. This message may be a status message, or it may contain sensor data.
	 * @param messageType	The message's type code. This should be one of the MESSAGE_TYPE_XX defined in {@link SensorsManager}.
	 * @param messageData	An optional set of data to attach to the message. This may be null.
	 */
	protected void sendMessage(Integer messageType, Bundle messageData){
		if(messageHandler!=null){
			Message handlerMessage;
			handlerMessage = messageHandler.obtainMessage(messageType);	//Create a message for the data we will send
			handlerMessage.arg1 = sensorType;							//The unique type code of this sensor is sent with all messages
			if(messageData!=null){
				handlerMessage.setData(messageData);					//If a Bundle of data was provided, place it in the message		
			}
			messageHandler.sendMessage(handlerMessage);
		}
	}
	
	/**
	 * Call this method to request that this sensor's parent {@link SensorsManager} start recording a new track. This will cause the SensorsManager
	 * to call {@link SensorBase#startRecording()} in each available sensor.
	 */
	protected void sendStartRecordingSignal(File dataLoggingDirectory){
		if(!sensorsManager.isCurrentlyRecording()){
			sensorsManager.startRecordingFromSensors(dataLoggingDirectory);
		}
	}
	
	/**
	 * Call this method to request that this sensor's parent {@link SensorsManager} stop recording the current track. This will cause the SensorsManager
	 * to call {@link SensorBase#stopRecording()} in each available sensor.
	 */
	protected void sendStopRecordingSignal(){
		if(sensorsManager.isCurrentlyRecording()){
			sensorsManager.stopRecordingFromSensors();
		}
	}
	
	/**
	 * Call this method to inform this sensor's parent {@link SensorsManager} that a new lap has begun. This will cause {@link SensorBase#newLapDetected(int)} and
	 * {@link SensorBase#newLapDataAvailable(long, int, DataStore)} to be called in each connected sensor. After this, {@link RecordDataEvents#newLapDetected(DataStore, int)}
	 * will be called in any class that implements the {@link RecordDataEvents} interface.
	 */
	protected void sendNewLapSignal(){
		sensorsManager.newLap(true);
	}
	
	/**
	 * A class that is used for storing and sending status information about a sensor. This class is {@link Serializable} to allow
	 * instances to be passed using {@link Handler}s and {@link Bundle}s.
	 *
	 */
	public static class SensorStatus implements Serializable{
		private static final long serialVersionUID = 1L;

		private int type;
		private String name;
		private String description;
		private SparseArray<Object> statusData;
		
		/**
		 * Constructor. Private so that the only object that may create a {@link SensorStatus} object is the sensor itself.
		 * @param statusData	A set of status data. May be null.
		 */
		private SensorStatus(int type, String name, String description, SparseArray<Object> statusData){
			this.type = type;
			this.name = name;
			this.description = description;
			this.statusData = statusData;
		}
		
		/**
		 * Returns the type code of this sensor.
		 * @return		The type code.
		 */
		public int getType() {
			return type;
		}

		/**
		 * Returns the name of this sensor.
		 * @return		The name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the description of this sensor.
		 * @return		The description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Returns the requested status data type, casting it to an {@link Integer} if neccessary. If this casting fails,
		 * or the requested entry does not exist, null is returned.
		 * @param typeCode		The requested status data type. Types should be defined in your custom SensorsManager class.
		 * @return				An Integer representation of the requested type, or null.
		 */
		public Integer getIntValue(int typeCode){
			Object value = statusData.get(typeCode);
			if(value!=null){
				try{
					Integer output = (Integer) value;
					return output;
				}
				catch(ClassCastException e){
					return null;
				}
			}
			else{
				return null;
			}
		}
		
		/**
		 * Returns the requested status data type, casting it to an {@link Float} if neccessary. If this casting fails,
		 * or the requested entry does not exist, null is returned.
		 * @param typeCode		The requested status data type. Types should be defined in your custom SensorsManager class.
		 * @return				An Float representation of the requested type, or null.
		 */
		public Float getFloatValue(int typeCode){
			Object value = statusData.get(typeCode);
			if(value!=null){
				try{
					Float output = (Float) value;
					return output;
				}
				catch(ClassCastException e){
					return null;
				}
			}
			else{
				return null;
			}
		}
		
		/**
		 * Returns the requested status data type, casting it to an {@link String} if neccessary. If this casting fails,
		 * or the requested entry does not exist, null is returned.
		 * @param typeCode		The requested status data type. Types should be defined in your custom SensorsManager class.
		 * @return				An String representation of the requested type, or null.
		 */
		public String getStringValue(int typeCode){
			Object value = statusData.get(typeCode);
			if(value!=null){
				try{
					String output = (String) value;
					return output;
				}
				catch(ClassCastException e){
					return null;
				}
			}
			else{
				return null;
			}
		}
	}
}