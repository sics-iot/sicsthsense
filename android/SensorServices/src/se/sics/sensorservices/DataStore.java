package se.sics.sensorservices;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;

/**
 * This class is used for storing collections of variables. Each variable is stored as an {@link Object}, and is referred to using the variable's shortCode (see {@link SensorDataType#getShortCode()}).
 * Objects from this class may be stored in {@link Handler} messages or {@link Bundle}s.
 */
public class DataStore extends HashMap<String, Object>{
	//private final String TAG = "DataStore";
	private static final long serialVersionUID = 1L;
	
	private SensorsManager sensorsManager;
	
	private Long startTime = null;
	
	public DataStore(SensorsManager sensorsManager){
		super();
		this.sensorsManager = sensorsManager;
	}
	
	/**
	 * Sets the start time of this {@link DataStore}.
	 * @param time	The start time, in milliseconds since Jan 1st 1970.
	 */
	public void setStartTime(Long time){
		startTime = time;
	}
	
	/**
	 * Returns the duration of this {@link DataStore}. This is the difference between the start time and the current time.
	 * @return		The duration, in milliseconds since Jan 1st 1970, or null if not set.
	 */
	public Long getDuration(){
		if(containsKey(SensorsManager.DATA_TYPE_CURRENT_TIME) && startTime!=null){		//True if we have valid current and start timestamps
			return (Long) get(SensorsManager.DATA_TYPE_CURRENT_TIME) - startTime;
		}
		else return null;
	}

	/**
	 * Returns an {@link Iterator} of the set of keys in this {@link DataStore}.
	 * @return		The {@link Iterator}.
	 */
	public Iterator<String> iterator() {
		return keySet().iterator();
	}
	
	/**
	 * Returns the contents of this {@link DataStore} in a {@link JSONObject}. Each variable will be stored in the object under its shortCode (see {@link SensorDataType#getShortCode()}).
	 * @return					A {@link JSONObject} containing the data.
	 * @throws JSONException	If there was a problem adding this {@link DataStore}'s values to the {@link JSONObject}.
	 */
	public JSONObject getJSONObject() throws JSONException{
		JSONObject values = new JSONObject();
		Iterator<String> iterator = iterator();
		SensorDataType tempType = null;
		String dataType = null;
		Object dataValue = null;
		while(iterator.hasNext()){
			dataType = iterator.next();
			tempType = sensorsManager.getDataType(dataType);
			if(tempType==null || tempType.isRecordingEnabled()){		//Only add the value to the JSON Object if recoring is enabled for this data type
				dataValue = get(dataType);
				if(dataValue!=null){
					values.put(dataType, dataValue);
				}
			}
		}
		return values;
	}
}