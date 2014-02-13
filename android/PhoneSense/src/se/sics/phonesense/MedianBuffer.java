package se.sics.phonesense;

import java.util.Arrays;

public class MedianBuffer extends BaseBuffer{
	//private final String TAG = "MedianBuffer";
	
	public MedianBuffer(int size){
		super(size);
	}
	
	@Override
	public Float getFilteredValue(){
		if(debug){
			startDebugClock();
		}
		//Copy the current buffer contents to a new array, as we need sorted data but we dont want to change the order of the elements in the values array
		Float[] sortedArray = new Float[size];
		
		for(int i=0; i<size; i++){
			sortedArray[i] = values[i];
		}
		
		//Now sort this new array
		Arrays.sort(sortedArray);
		Float median = null;
		int middle = size / 2;
		if(size%2==0){
			median = (sortedArray[middle] + sortedArray[middle - 1]) / 2;
		}
		else median = sortedArray[middle];
		
		if(debug){
			stopDebugClock();
		}
		return median;
	}
}