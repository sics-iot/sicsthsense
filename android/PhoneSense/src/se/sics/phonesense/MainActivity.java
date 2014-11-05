package se.sics.phonesense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import se.sics.sensorservices.DataStore;
import se.sics.sensorservices.SensorBase;
import se.sics.sensorservices.SensorBase.SensorStatus;
import se.sics.sensorservices.SensorsManager.RecordDataEvents;
import se.sics.sensorservices.SensorsManager.SensorScanEvents;
import android.app.Activity;
//import android.R;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements SensorScanEvents, RecordDataEvents {
	PhoneSensorsManager manager;
	TextView TextViewOutput;
	EditText urlbox;
	ToggleButton button;
	String posturl = "";
	boolean posting = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ArrayList<Integer> sensors = new ArrayList<Integer>(); 
        sensors.add(PhoneSensorsManager.SENSOR_TYPE_INTERNAL_ACCELEROMETER); 
        sensors.add(PhoneSensorsManager.SENSOR_STATUS_BATTERY_LEVEL);
        
        manager = PhoneSensorsManager.getSensorsManager(this);
        manager.addSensorScanListener(this);
        manager.setDataMessageInterval(3000);
        manager.scanForNewSensors(sensors, true);
        manager.addRecordDataListener(this);
        
        setupUI();
    }
    @Override
    public void onStop() {
    	super.onStop(); 
    	manager.stopRecordingFromSensors();
    }

    public void setupUI() {
        //findviewbyid casted
        TextViewOutput = (TextView)findViewById(R.id.status);
        urlbox = (EditText)findViewById(R.id.urlbox);
        
    	button = (ToggleButton)findViewById(R.id.toggleButton1);
        button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (button.isChecked()) {posting=true;
				} else {posting=false;}
			}
        });
    }
    

 
	@Override
	public void newSensorData(DataStore newData) {
		// TODO Auto-generated method stub
		String json = "";
		if (posting) {
			try {
				json = newData.getJSONObject().toString();
			} catch (Exception e){
				Log.e("MainActivity","Json problem!");
			}
			Log.d("MainActivity", json);
			String url = urlbox.getText().toString();
			if (validUrl(url)) {
				postData(url,json);
			} else {
				setStatus("Posting URL seems invalid: "+url);
			}
		}
	}
	public boolean validUrl(String url) {
		if ("".equals(url)) {
			return false;
		}
		return true;
	}

    public void postData(String url, String data) {
    	if (!url.startsWith("http")) {
    		url = "http://"+url;
    	}

        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            
            httppost.setEntity(new StringEntity(data));
            httppost.setHeader("Content-type", "application/json");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
			setStatus("Posted:\n"+data);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	Log.e("Main","Client Protocol Exception"+e);
        	setStatus("Client Protocol Exception!: \n"+e);

        } catch (IOException e) {
            // TODO Auto-generated catch block
        	Log.e("Main","IOException: "+e);
        	setStatus("IO Exception!: \n"+e);

        } catch (Exception e) {
        	Log.e("Main","Posting Exception!: "+e);
        	setStatus("Posting Exception!: \n"+e);
        }
    } 
    
	public void setStatus(final String status) {
		//TextViewOutput.setText(new Runnable(){ public void run(){ v.bringToFront(); } });
		runOnUiThread(new Runnable() { 
			public void run() { 
				TextViewOutput.setText(status); 
			} 
		} );
	}
	
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.quit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	

	@Override
	public void sensorScanStarted() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sensorScanFinished() {
		// TODO Auto-generated method stub
		manager.connectToAvailableSensors();
	}


	@Override
	public void newSensorFound(SensorBase sensor, boolean requiresPairing) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sensorConnectionSucceeded(SensorBase sensor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sensorConnectionFailed(SensorBase sensor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sensorDisconnected(SensorBase sensor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connectionComplete() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disconnectionComplete() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void recordingStarted() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void recordingStopped(DataStore summaryData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newLapDetected(DataStore lapData, int lapNumber) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newSensorStatusData(
			HashMap<Integer, SensorStatus> sensorStatusData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void recordingError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

    
}
