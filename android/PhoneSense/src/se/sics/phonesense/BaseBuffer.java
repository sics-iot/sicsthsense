package se.sics.phonesense;

public class BaseBuffer {
	//private final String TAG = "BaseBuffer";
	
	protected int size;
	protected int pointer;
	protected long storeOperations;
	
	protected boolean debug;
	private long startTimestamp, endTimestamp, executionTime;
	
	protected float[] values;
	
	public BaseBuffer(int size){
		this.size = size;
		values = new float[size];
		pointer = 0;
		storeOperations = 0;
		debug = false;
		executionTime = 0;
	}
	
	public void setDebugEnabled(boolean debugEnabled){
		debug = debugEnabled;
	}

	protected void startDebugClock() {
		startTimestamp = System.nanoTime();
	}
	
	protected void stopDebugClock(){
		endTimestamp = System.nanoTime();
		executionTime+= endTimestamp - startTimestamp;
	}
	
	public long getExecutionTime(){
		if(executionTime>0){
			return executionTime / 1000000;
		}
		else{
			return 0;
		}
	}
	
	public void storeValue(float value){
		values[pointer] = value;
		pointer++;
		if(pointer>=size){
			pointer = 0;
		}
		storeOperations++;
	}
	
	public int getPointer(){
		return pointer;
	}
	
	public int getSize(){
		return size;
	}
	
	public boolean isBufferFull(){
		return storeOperations>=size;
	}
	
	public Float getFilteredValue(){
		return null;
	}
	
	public float[] getValues(){
		float[] outputValues = new float[size];
		int valuePointer = pointer;
		for(int i=0; i<size; i++){
			outputValues[i] = values[valuePointer];
			valuePointer++;
			if(valuePointer>=size){
				valuePointer = 0;
			}
		}
		return outputValues;
	}
	
	public String getValuesString(){
		String valuesString = "";
		float[] values = getValues();
		for(int i=0; i<size; i++){
			if(i>0){
				valuesString+= ',';
			}
			valuesString+= values[i];
		}
		return valuesString;
	}
}