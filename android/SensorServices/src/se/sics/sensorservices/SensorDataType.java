package se.sics.sensorservices;

import java.io.Serializable;

/**
 * A class that describes a variable (data type) provided by a sensor. Each data type comprises a short three-letter code, a name,
 * a unit, a specification of how many decimal places it requires, and a type.
 */
public class SensorDataType implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * Flag representing an {@link Integer} type
	 */
	public static final int VARIABLE_TYPE_INTEGER = 1;
	/**
	 * Flag representing an {@link Long} type
	 */
	public static final int VARIABLE_TYPE_LONG = 2;
	/**
	 * Flag representing an {@link Float} type
	 */
	public static final int VARIABLE_TYPE_FLOAT= 3;
	/**
	 * Flag representing an {@link Double} type
	 */
	public static final int VARIABLE_TYPE_DOUBLE = 4;
	/**
	 * Flag representing an {@link String} type
	 */
	public static final int VARIABLE_TYPE_STRING = 10;
	/**
	 * Flag representing an boolean type
	 */
	public static final int VARIABLE_TYPE_BOOLEAN = 11;
	
	/**
	 * Flag representing a data type that is sampled in real-time.
	 */
	public static final int VARIABLE_CLASS_REALTIME = 1;
	/**
	 * Flag representing a data type that is sampled not in real-time, but once per lap and / or track.
	 */
	public static final int VARIABLE_CLASS_SUMMARY = 2;
	
	public static final int ICON_NOT_SET = 0;
	
	public static final int PRIORITY_DEFAULT = 50;
	
	private SensorBase parentSensor;
	private String shortCode;
	private String fullName;
	private String unit;
	private Integer decimalPlaces;
	private Integer variableType;
	private Integer variableClass;
	private int priority;
	private int iconResId;
	
	private boolean recordData;		//Flag that is used to tell the SensorsManager whether or not the data type
	private boolean enabled;		//Flag that is used to tell the SensorsManager whether or not this data type is available for recording

	/**
	 * Constuctor.
	 * @param newShortCode			A three-letter code that is used internally by the {@link SensorsManager} and other sensors.
	 * @param newFullName			The name of the variable.
	 * @param newUnit				The unit of this variable, for example, BPM for heart rate. May be null.
	 * @param newDecimalPlaces		How many decimal places this variable should be rounded to. This value is not used by the {@link SensorsManager}, but may be used in your code.
	 * @param variableType			The type of this variable (e.g. {@link Float}). Must be one of of the VARIABLE_TYPE_XX constants defined in this class.
	 * @param variableClass			The class of this variable (real-time or summary). Must be one of the VARIABLE_CLASS_XX constants defined in this class.
	 */
	public SensorDataType(String newShortCode, String newFullName, String newUnit, Integer newDecimalPlaces, Integer variableType, Integer variableClass){
		shortCode = newShortCode;
		fullName = newFullName;
		unit = newUnit;
		if(unit==null){		//Check for null values and replace with an empty string
			unit = "";
		}
		decimalPlaces = newDecimalPlaces;
		this.variableType = variableType;
		this.variableClass = variableClass;  
		recordData = true;
		priority = PRIORITY_DEFAULT;
		iconResId = ICON_NOT_SET;
	}
	
	public SensorDataType setParentSensor(SensorBase parentSensor){
		this.parentSensor = parentSensor;
		return this;
	}
	
	public SensorDataType setIconResId(int iconResId){
		this.iconResId = iconResId;
		return this;
	}
	
	/**
	 * Call this method to prevent the {@link SensorsManager} from recording or transmitting this data type. This is useful for data types
	 * that need to be sent to other classes in the system, but not logged or sent to a server (e.g. battery status of a sensor).
	 * @return		A copy of this data type, to allow methods to be chained together.
	 */
	public SensorDataType disableRecording(){
		recordData = false;
		return this;
	}

	public SensorDataType setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public SensorDataType setPriority(int priority){
		this.priority = priority;
		return this;
	}
	
	public SensorBase getParentSensor(){
		return parentSensor;
	}

	/**
	 * Returns the three-letter code of this type.
	 * @return The type code.
	 */
	public String getShortCode() {
		return shortCode;
	}

	/**
	 * Returns the name of the variable.
	 * @return The name.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Returns the variable's unit.
	 * @return The unit.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Returns the variable's decimal places.
	 * @return The decimal places.
	 */
	public int getDecimalPlaces() {
		return decimalPlaces;
	}
	
	/**
	 * Returns the variable's class.
	 * @return The class.
	 */
	public int getVariableClass(){
		return variableClass;
	}
	
	/**
	 * Returns the variable's type.
	 * @return The type.
	 */
	public int getVariableType(){
		return variableType;
	}
	
	public int getIconResId(){
		return iconResId;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Returns whether or not this data type should be logged or transmitted to a server.
	 * @see SensorDataType#disableRecording()
	 * @return		True if this data type should be logged or transmitted, false otherwise.
	 */
	public boolean isRecordingEnabled(){
		return recordData;
	}
	
	public int getPriority(){
		return priority;
	}
}