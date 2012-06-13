package ch.ethz.inf.vs.android.restserver;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class main extends Activity {

	private Intent svc;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        svc = new Intent(this, Server.class);
        
        Button btn = (Button) findViewById(R.id.btnService);
        
        if (Server.isRunning) {
        	btn.setText(R.string.RunningButtonText);
		} else {
			btn.setText(R.string.StoppedButtonText);
		}
        
        getInterfaces();
    }
    
    public void onMinimize(View view) {
    	finish();
    }
    
    private void getInterfaces() {
    	TextView text = (TextView)findViewById(R.id.text);
    	text.setText("");

        try {
        	Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();


 			text.append("==[ Addresses ]=============\n");
 			while(e.hasMoreElements()) {
	 			NetworkInterface ni = (NetworkInterface) e.nextElement();
				text.append("Net interface: "+ni.getName()+"\n");

    			Enumeration<InetAddress> e2 = ni.getInetAddresses();

				while (e2.hasMoreElements()){
					InetAddress ip = (InetAddress) e2.nextElement();
					text.append("IP address: "+ ip.toString()+"\n");
				}
 			}
 			
 			text.append("==[ Sensors ]=============\n");
 			
 			SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
 	        for (Sensor s : sensorMgr.getSensorList(Sensor.TYPE_ALL))
 	        	text.append(s.getName()+"\n");
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
    }
    
    public void onClick(View view) {
        
	    if (!Server.isRunning) {
	    	Toast.makeText(main.this, "Starting server", Toast.LENGTH_SHORT).show();
			startService(svc);
			((Button)view).setText(R.string.RunningButtonText);
			finish();
		} else {
			Toast.makeText(main.this, "Stopping server", Toast.LENGTH_SHORT).show();
			stopService(svc);
			((Button)view).setText(R.string.StoppedButtonText);
		}
    
    }
    
    public void onDisplay(View view) {
    	Intent myIntent = new Intent(main.this, Display.class);
    	myIntent.putExtra("color", Color.BLACK);
    	myIntent.putExtra("brightness", 100);
    	main.this.startActivity(myIntent);
    }
    
    public void onSettings(View view) {
    	Intent myIntent = new Intent(main.this, Settings.class);
    	main.this.startActivity(myIntent);
    }
}
