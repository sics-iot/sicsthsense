package se.sics.phonesense;

public class MeanBuffer extends BaseBuffer{
	//private final String TAG = "MeanBuffer";
	
	public MeanBuffer(int size){
		super(size);
	}
	
	@Override
	public Float getFilteredValue(){
		if(debug){
			startDebugClock();
		}
		Float mean = null;
		Float total = null;
		int samples = 0;
		for(int i=0; i<size; i++){
			if(total==null){
				total = values[i];
			}
			else{
				total+= values[i];
			}
			samples++;
		}
		if(total!=null && samples>0){
			mean = total / samples;
		}
		
		if(debug){
			stopDebugClock();
		}
		return mean;
	}
}