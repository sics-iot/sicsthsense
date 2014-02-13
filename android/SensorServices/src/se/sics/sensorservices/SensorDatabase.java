package se.sics.sensorservices;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class handles the storage of sensors in the system. Methods are provided for adding, removing, enabling and disabling sensors.
 */
public class SensorDatabase extends SQLiteOpenHelper {
	private final static String TAG =  "SensorDatabase";
	
	//Database Version
    private static final int DATABASE_VERSION = 1;
 
    //Database Name
    private static final String DATABASE_NAME = "SensorDatabase";
 
    //Table names
    private final String TABLE_SENSOR_CONFIGURATION = "SensorConfiguration";
    
    //Fields for the sensor configuration table
    private final String KEY_SENSOR_TYPE = "sensorType";
    private final String KEY_SENSOR_ADDRESS = "sensorAddress";
    private final String KEY_ENABLED = "enabled";
    
    private Context context;
    
    String createTableSensorConfiguration = "CREATE TABLE IF NOT EXISTS "+TABLE_SENSOR_CONFIGURATION+" ("+KEY_SENSOR_TYPE+" INTEGER PRIMARY KEY, "+KEY_SENSOR_ADDRESS+" TEXT, "+KEY_ENABLED+" BOOLEAN)";

    /**
     * Constructor for the Database. Upgrading between database versions is handled by the system.
     * @param newContext		The context of the activity or service that instantiated the Database.
     */
    public SensorDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

	/**
	 * Called when the database is created. Handles creation of the various tables.
	 */
    @Override
    public void onCreate(SQLiteDatabase database) {
    	//Create the various tables we will use
       database.execSQL(createTableSensorConfiguration);
    }
 
    /**
     * Called when the database is upgraded. Handles any changes we define.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //Changes are handled in a for loop, with each change represented as an interation
    	String updateOperation = null;
    	for(int i=oldVersion+1; i<=newVersion; i++){
    		switch(i){
    		case 1:		//Version 1 adds the configuration table
    			updateOperation = createTableSensorConfiguration;
    			break;
    		default:
    			updateOperation = null;
    			break;
    		}
    		if(updateOperation!=null){
    			database.execSQL(updateOperation);
    		}
    	}
    	Log.d(TAG, "Database upgraded to version "+newVersion);
    	onCreate(database);
    }
    
    /**
     * Adds a sensor to the persistent configuration.
     * @param sensor		A sensor sub-class that implements the {@link SensorBase} class.
     */
    public void addSensor(SensorBase sensor){    	
    	SQLiteDatabase database = this.getWritableDatabase();	//Connect to the database
    	
    	Integer sensorType = sensor.getType();
    	
    	//We will store this contact in a row in the database, which is represented by a set of values contained in a ContentValues object
    	ContentValues row = new ContentValues();
        row.put(KEY_SENSOR_TYPE, sensorType); 				//Type of the sensor
        row.put(KEY_SENSOR_ADDRESS, sensor.getAddress()); 	//Address of the sensor. For internal sensors, this will be null
        row.put(KEY_ENABLED, true); 						//Whether or not the sensor is enabled. Initially true
  
        //Insert the row
        if(database.insert(TABLE_SENSOR_CONFIGURATION, null, row)!=-1){
        	Log.d(TAG, "Sensor added, type: "+sensorType);
        }
        else{
        	Log.e(TAG, "Failed to add sensor, type: "+sensorType);
        }
        database.close(); 									//Close the database connection
        Intent sensorAddedIntent = new Intent(SensorsManager.INTENT_SIGNATURE_SENSOR_ADDED);		//Send a broadcast to signal a sensor was just added
        context.sendBroadcast(sensorAddedIntent);
    }
    
    /**
     * Enables or disables a sensor.
     * @param sensor		The sensor you wish to enable or disable.
     * @param enabled		The new status.
     */
    public void setSensorEnabled(SensorBase sensor, boolean enabled){
    	SQLiteDatabase database = this.getWritableDatabase();	//Connect to the database
    	
    	String selector = KEY_SENSOR_TYPE+"="+sensor.getType();
    	
    	ContentValues row = new ContentValues();
        row.put(KEY_ENABLED, enabled); 						//The new enabled status of this sensor
        
        //Update the row
        database.update(TABLE_SENSOR_CONFIGURATION, row, selector, null);
        database.close(); 										//Close the database connection
    }
    
    /**
     * Removes a sensor from the persistent configuration.
     * @param sensor	A sensor sub-class that implements the SensorBase class (e.g. SensorBioHarness).
     */
    public void removeSensor(SensorBase sensor){
    	SQLiteDatabase database = this.getReadableDatabase();		//Connect to the database
    	database.execSQL("DELETE FROM "+TABLE_SENSOR_CONFIGURATION+" WHERE "+KEY_SENSOR_TYPE+"="+sensor.getType());	//Delete the matching sensor from the table
        database.close();
        Intent sensorRemovedIntent = new Intent(SensorsManager.INTENT_SIGNATURE_SENSOR_REMOVED);				//Send a broadcast to signal a sensor was just removed
        context.sendBroadcast(sensorRemovedIntent);
    }

    /**
     * Retrieves all sensors currently in the persistent configuration
     * @return		An ArrayList containing the types and address of each sensor in the persistent configuration, where each sensor is represented using a Pair object, in the form Pair<sensorType, sensorAddress>
     */
    public ArrayList<SensorInfo> getSensorsInConfiguration(){
    	ArrayList<SensorInfo> sensorList = new ArrayList<SensorInfo>();
    	String[] columnsToRetrieve = {KEY_SENSOR_TYPE, KEY_SENSOR_ADDRESS, KEY_ENABLED};
      	
    	SQLiteDatabase database = this.getReadableDatabase();	//Connect to the database
    	
    	//Simply get all sensors in the configuration table
        Cursor cursor = database.query(TABLE_SENSOR_CONFIGURATION, columnsToRetrieve, null, null,  null, null, null, null);
        if (cursor.moveToFirst()){		//True if there is at least one result to process
        	do {						//Loop through        		
        		Integer sensorType = cursor.getInt(0);
        		String sensorAddress = cursor.getString(1);
        		Boolean sensorEnabled = cursor.getInt(2)>0;
        		sensorList.add(new SensorInfo(sensorType, sensorAddress, sensorEnabled));
            } while (cursor.moveToNext());
        }
        database.close(); 										//Close the database connection
        //Return the list of sensor pairs, or null if none were found    	
    	return sensorList;
    }
    
    /**
     * An inner class that is used to store information about a sensor stored in the database.
     * @author Tom
     */
    public class SensorInfo{
    	private int type;
		private String address = null;
    	private boolean enabled;
    	
    	/**
    	 * Constructor
    	 * @param type			The sensor's type. Types should be defined in your own implementation of {@link SensorsManager}.
    	 * @param address		The sensor's address. For internal sensors this will be null.
    	 * @param enabled		Whether or not this sensor is enabled. Disabled sensors are still stored in the databse, but cannot be connected to.
    	 */
    	public SensorInfo(int type, String address, boolean enabled){
    		this.type = type;
    		this.address = address;
    		this.enabled = enabled;
    	}    	

    	/**
    	 * Returns the sensor's type. Types should be defined in your own implementation of {@link SensorsManager}.
    	 * @return		The type.
    	 */
    	public int getType() {
			return type;
		}

    	/**
    	 * Returns the sensor's address. For internal sensors this will be null.
    	 * @return		The sensor's address.
    	 */
		public String getAddress() {
			return address;
		}

		/**
		 * Returns the sensor's enabled state.
		 * @return		True if this sensor is enabled, false if not.
		 */
		public boolean isEnabled() {
			return enabled;
		}
    }
}