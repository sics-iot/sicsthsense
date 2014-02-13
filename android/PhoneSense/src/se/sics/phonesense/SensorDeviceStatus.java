package se.sics.phonesense;

import java.io.File;

import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorDataType;
import se.sics.sensorservices.SensorsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SensorDeviceStatus extends SensorBase{
	private static final long serialVersionUID = 1L;

	public static final String FILE_GPS_TRACK = "gps_track.csv";
	
	private final int HEADSET_STATE_UNPLUGGED = 0;
	private final int HEADSET_STATE_PLUGGED = 1;

	private Context context;

	private SharedPreferences preferences;

	private int batteryLevel;
	
	private int headsetState = HEADSET_STATE_UNPLUGGED;
	
	public SensorDataType battery = new SensorDataType("bat", "Battery", "per", 2, SensorDataType.VARIABLE_TYPE_INTEGER, SensorDataType.VARIABLE_CLASS_REALTIME);
	public SensorDataType temperature = new SensorDataType("temperature", "Temperature", "c", 2, SensorDataType.VARIABLE_TYPE_FLOAT, SensorDataType.VARIABLE_CLASS_REALTIME);


	public SensorDeviceStatus(Context context){
		super();
		TAG = "SensorDeviceStatus";

		this.context = context;

		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		//Fill in this sensor's information
//		setSensorType(SkilabSensorsManager.SENSOR_TYPE_DEVICE_STATUS);
		setSensorName("Device status");
		setSensorDescription("Monitors the status of the device, e.g. battery level.");
		setIsVisible(false);			//We don't want to see this sensor in any config dialogs
		declareProvidedType(battery);
		//declareProvidedType(new SensorDataType(SkilabSensorsManager.DATA_TYPE_DEVICE_BATTERY_LEVEL, "Device battery level", "%", 0, SensorDataType.VARIABLE_TYPE_INTEGER).disableRecording());
	}

	@Override
	public void connect(SensorsManager sensorsManager){
		super.connect(sensorsManager);
	    connectionSucceeded();

		registerReceivers();
	}

	@Override
	public void startRecording(File loggingDirectory){
		super.startRecording(loggingDirectory);
		storeData();			//This sensor receives broadcasts from the OS containing battery information. If a broadcast arrives whilst another sensor is connecting,
								//the message will be ignored by the SensorsManager. To avoid this, we store the value for battery level in a global and send it 
								//to the handler here, when the handler is certain to be ready for the message
	}
	
	@Override
	public void stopRecording(long trackDuration){
		super.stopRecording(trackDuration);
		unRegisterReceivers();
	}

	@Override
	public void disconnect(){
		super.disconnect();
	}

	private void registerReceivers() {
	    IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    
	    context.registerReceiver(batteryReceiver, batteryLevelFilter);
	}

	private void unRegisterReceivers(){
		context.unregisterReceiver(batteryReceiver);
	    
	}

	protected void handleBatteryIntent(Intent intent){
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        //int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        if(level!=-1 && scale!=-1){
        	batteryLevel = Math.round(((float) level / (float) scale) * 100);
			Bundle dataBundle = new Bundle();
			//dataBundle.putFloat(DataTypes.getTypeFromCode(DataTypes.ELEVATION).getShortCode(), (float) Utils.round(currentElevation, 2));
			dataBundle.putInt("bat", batteryLevel);
			dataBundle.putFloat("temperature", temp);
			
			sendMessage(PhoneSensorsManager.MESSAGE_TYPE_DATA, dataBundle);
		}
	}

	private void storeData(){
    	storeStatusData(PhoneSensorsManager.SENSOR_STATUS_BATTERY_LEVEL, batteryLevel);
	}

	private boolean isHeadsetLapControlEnabled(){
		return false;
	}

	BroadcastReceiver batteryReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
        	handleBatteryIntent(intent);
        }
    };
    
   
}
