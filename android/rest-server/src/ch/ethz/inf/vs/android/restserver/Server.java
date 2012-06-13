package ch.ethz.inf.vs.android.restserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class Server extends Service {
	
	private static final String DEFAULT_BASE_URI = "http://sense.sics.se/streams/";
	
	private static final String RES_LED = "/led";
	private static final String RES_VIBRATE = "/vibrate";
	private static final String RES_SOUND = "/sound";
	private static final String RES_DISPLAY = "/display";
	private static final String RES_COMPASS = "/compass";
	private static final String RES_ORIENTATION = "/orientation";
	private static final String RES_LIGHT = "/light";
	private static final String RES_PROXIMITY = "/proximity";
	
	private static final String INDEX_HTML=
		"<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>"+
	"<html xmlns='http://www.w3.org/1999/xhtml'>"+
	""+
	"<head>"+
	"<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"+
	"<title>Mobile Phone</title>"+
	"<style type='text/css'>"+
	"<!--"+
	"body, html {"+
	"	font-family: Arial, Helvetica, sans-serif;"+
	"	background-color: #EEEEDD;"+
	"	width: 100%;"+
	"	height: 100%;"+
	"	margin: 0px;"+
	"	display: table;"+
	"}"+
	""+
	"a:link { "+
	"    color:#0000BB; "+
	"    text-decoration:none; "+
	"}"+
	""+
	"a:visited { "+
	"    color:#0000BB; "+
	"    text-decoration:none; "+
	"}"+
	""+
	"a:hover { "+
	"    color:#0000BB; "+
	"    text-decoration:underline; "+
	"}"+
	""+
	"a:link.navlink { "+
	"    color:#000000; "+
	"    text-decoration:none; "+
	"}"+
	""+
	"a:visited.navlink { "+
	"    color:#000000; "+
	"    text-decoration:none; "+
	"}"+
	""+
	"a:hover.navlink { "+
	"    color:#0000cc; "+
	"    text-decoration:none; "+
	"}"+
	""+
	"#cell {"+
	"	display: table-cell;"+
	"    vertical-align: middle;"+
	"}"+
	""+
	"#container {"+
	"	width: 600px;"+
	"	height: 270px;"+
	"	margin: auto;"+
	"	padding: 20px;"+
	"	background-color: white;"+
	"	border: 1px;"+
	"	-moz-border-radius: 10px;"+
	"	-webkit-border-radius: 10px;"+
	"}"+
	""+
	"#icon {"+
	"	position: absolute;"+
	"	width: 160px;"+
	"	height: 280px;"+
	"	overflow: hidden;"+
	"	text-align: center;"+
	"	background-color: white;"+
	"}"+
	""+
	"#currentstate {"+
	"	position: absolute;"+
	"	margin-top: 170px;"+
	"	width: 160px;"+
	"	height: 60px;"+
	"	line-height: 60px;"+
	"	overflow: hidden;"+
	"	text-align: center;"+
	"	vertical-align: middle;"+
	"	background-color: #DDDDDD;"+
	"	padding: 0px;"+
	"	font-size: 60px;"+
	"}"+
	""+
	"#title {"+
	"	position: absolute;"+
	"	width: 420px;"+
	"	margin-left: 180px;"+
	"	font-size: 40px;"+
	"	font-weight: bold;"+
	"	border-bottom: 2px dotted #CCCCCC;"+
	"}"+
	""+
	"#subtitle {"+
	"	position: absolute;"+
	"	width: 580px;"+
	"	margin-left: 180px;"+
	"	margin-top: 56px;"+
	"	font-size: 12px;"+
	"	color: #aaaaaa;"+
	"}"+
	""+
	"#info {"+
	"	position: absolute;"+
	"	width: 430px;"+
	"	margin-left: 180px;"+
	"	margin-top: 60px;"+
	"	font-size: 15px;"+
	"	color: #000000;"+
	"}"+
	""+
	".info {"+
	"	position: absolute;"+
	"	margin-top: 220px;"+
	"	width: 290px;"+
	"	height: 240px;"+
	"	text-align: center;"+
	"	background-color: white;"+
	"	border: 1px dashed #CCCCCC;"+
	"	-moz-border-radius: 10px;"+
	"	-webkit-border-radius: 10px;"+
	"}"+
	""+
	".decimals {"+
	"	font-size: 50px;"+
	"	font-weight: bold;"+
	"}"+
	""+
	"-->"+
	"</style>"+
	"</head>"+
	""+
	"<body>"+
	"<div id=\"cell\">"+
	""+
	"<div id=\"container\">"+
	"	<div id=\"icon\"><img id=\"iconimg\" src=\"http://laptop/~prasentator/wifinode/resources/desire.jpg\" /></div>"+
	"	<div id=\"title\">Mobile Phone</div>"+
	"	<div id=\"info\">"+
	"	   <span style=\"color: #000000;\">"+
	"	    Available Resources:"+
	"    	<ul style=\"list-style-type:circle; margin: 0px; padding: 0px;\">"+
	"    	   <li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_LED+"\">"+RES_LED+"</a>: The current state of the led light.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_VIBRATE+"\">"+RES_VIBRATE+"</a>: The current state of the vibrating alert.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_SOUND+"\">"+RES_SOUND+"</a>: The current state of the sound playback.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_DISPLAY+"/text\">"+RES_DISPLAY+"/text</a>: The current text on the display.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_DISPLAY+"/color\">"+RES_DISPLAY+"/color</a>: The current background color of the display.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_DISPLAY+"/brightness\">"+RES_DISPLAY+"/brightness</a>: The current brightness of the display.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_COMPASS+"\">compass</a>: The current reading of the compass sensor.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_ORIENTATION+"/x\">orientation/x</a>: The current orientation in x axis.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_ORIENTATION+"/y\">orientation/y</a>: The current orientation in y axis.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_PROXIMITY+"\">"+RES_PROXIMITY+"</a>: The current reading of the proximity sensor.</li>"+
	"			<li style=\"margin-left: 20px; padding-left: 0px;\"><a href=\""+RES_LIGHT+"\">"+RES_LIGHT+"</a>: The current reading of the proximity sensor.</li>"+
	"        </ul>"+
	"        </span>"+
	"    </div>"+
	""+
	"</div>"+
	"</div>"+
	""+
	"</body>"+
	"</html>";

	
	static public boolean isRunning = false;
	
	private final int PORT = 8080;
	private final int MAX_CONN = 0;
	
	private ServerSocket listener;
	private int conCount = 0;
	
    private NotificationManager mNM;
    
	// Workaround to display a toast from a non-GUI thread
	final Handler printToast = new Handler( new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
			Toast.makeText(Server.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			return true;
        }
    });
    
	private SensorManager sensorMgr = null;
	
	private int configColor = Color.BLACK;
	private int configBrightness = 100;
	private String configText = "Hello, world!";
	
	private String sensorOrientationX;
	private String sensorOrientationY;
	private String sensorCompass;
	private String sensorLight;
	private String sensorProximity;

    /* Instance of the vibrator service */
	private Vibrator vib;
	private boolean vibrating = false;
	/* Flashlight */
	private Camera cam;
	private boolean openCam = false;
	
	private class CamCB implements Camera.ErrorCallback {
		public void onError(int nr, Camera errCam) {
			Toast.makeText(Server.this, "Camera error: " + nr, Toast.LENGTH_LONG).show();
		}
	}
	
	/* Sound */
	private MediaPlayer mp;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	Server getService() {
            return Server.this;
        }
    }
    
    private class ClientHandler implements Runnable {
    	private Socket client;
        private String request,line,input,payload;
        private boolean put = false;

        ClientHandler(Socket client) {
          this.client=client;
        }

        public void run () {
        	try {
        		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        		PrintStream out = new PrintStream(client.getOutputStream());
        		
        		request = in.readLine();
        		
        		// request type
        		if (request.startsWith("GET")) {
        			//get = true;
        		} else if (request.startsWith("POST") || request.startsWith("PUT")) {
        			put = true;
        		} else {
        			
        		}
        		
        		input = "";
        		while ((line = in.readLine()) != null) {
        			input += line +"\r\n";
        			if (line.length()==0) break;
        		}
        		
        		// payload
        		payload = "";
        		if (put) {
        			int c;
        			while (in.ready()) {
        				c = in.read();
        				payload += (char)c;
        			}
        		}
        		
        		out.print("HTTP/1.1 200 OK\r\n");
        		
        		if (request.contains(RES_LED)) {
        			if (put) {
        				if ((payload.toLowerCase().equals("true") && !openCam) ||
        					(payload.toLowerCase().equals("false") && openCam)) {
        					clickFlashlight(null);
        				}
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
    				out.print(openCam ? "true" : "false");
        		} else if (request.contains(RES_VIBRATE)) {
        			if (put) {
        				if ((payload.toLowerCase().equals("true") && !vibrating) ||
        					(payload.toLowerCase().equals("false") && vibrating)) {
        					clickVibrate(null);
        				}
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
    				out.print(vibrating ? "true" : "false");
        		} else if (request.contains(RES_SOUND)) {
        			if (put) {
        				if ((payload.toLowerCase().equals("true") && !mp.isPlaying()) ||
        					(payload.toLowerCase().equals("false") && mp.isPlaying())) {
        					clickSound(null);
        				}
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
    				out.print(mp.isPlaying() ? "true" : "false");
        		} else if (request.contains(RES_DISPLAY+"/color")) {
        			if (put) {
        				try {
        					configColor = Color.parseColor(payload);
        				} catch (Exception e) {
        					out.println("Failed to convert '"+payload+"'");
        					Log.w("Server", e.getMessage(), e);
        				} finally {
            				Intent myIntent = new Intent(Server.this, Display.class);
            				myIntent.putExtra("color", configColor);
            				myIntent.putExtra("brightness", configBrightness);
            				myIntent.putExtra("text", configText);
            				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            				Server.this.startActivity(myIntent);
        				}
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.printf("#%02X%02X%02X", Color.red(configColor),
        					Color.green(configColor),
        					Color.blue(configColor));
    				
        		} else if (request.contains(RES_DISPLAY+"/brightness")) {
        			if (put) {
        				try {
        					configBrightness = Integer.parseInt(payload);
        				} catch (Exception e) {
        					out.println("Failed to convert '"+payload+"'");
        					Log.w("Server", e.getMessage(), e);
        				} finally {
            				Intent myIntent = new Intent(Server.this, Display.class);
            				myIntent.putExtra("color", configColor);
            				myIntent.putExtra("brightness", configBrightness);
            				myIntent.putExtra("text", configText);
            				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            				Server.this.startActivity(myIntent);
        				}
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(configBrightness);
    				
        		} else if (request.contains(RES_DISPLAY+"/text")) {
        			if (put) {
    					configText = payload;
    				
        				Intent myIntent = new Intent(Server.this, Display.class);
        				myIntent.putExtra("color", configColor);
        				myIntent.putExtra("brightness", configBrightness);
        				myIntent.putExtra("text", configText);
        				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        				Server.this.startActivity(myIntent);
        			}
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(configText);
    				
        		} else if (request.contains(RES_COMPASS)) {
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(sensorCompass);
        		} else if (request.contains(RES_ORIENTATION+"/x")) {
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(sensorOrientationX);
        		} else if (request.contains(RES_ORIENTATION+"/y")) {
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(sensorOrientationY);
        		} else if (request.contains(RES_ORIENTATION)) {
            		out.print("Content-Type: application/json\r\n");
            		out.print("\r\n");
        			out.print("{\"x\":"+sensorOrientationX+",\"y\":"+sensorOrientationY+"}");
        		} else if (request.contains(RES_LIGHT)) {
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(sensorLight);
        		} else if (request.contains(RES_PROXIMITY)) {
            		out.print("Content-Type: text/plain\r\n");
            		out.print("\r\n");
        			out.print(sensorProximity);
        		} else {
        			// show main page 
            		out.print("Content-Type: text/html\r\n");
            		out.print("\r\n");
            		
            		out.print(INDEX_HTML);
        		}
        		
        		client.close();
		  } catch (IOException e) {
			  Log.e("ClientHandler", e.getMessage(), e);
		  }
        }
	};
	
	private Runnable serverLoop = new Runnable() {
		public void run () {
	        try {
	        	listener = new ServerSocket(PORT);
	        	
		        while( (MAX_CONN == 0) || (conCount++ < MAX_CONN) ){
		        	
		        	Socket client = listener.accept();
		        	ClientHandler conn_c = new ClientHandler(client);
		        	
		        	Thread t = new Thread(conn_c);
		        	t.start();
		        }
	        	
	        } catch (Exception e) {
	        	Log.i("ServerSocket", e.getMessage());
	        }
		}
	};
	
	private class PostTask implements Runnable {
		private String stream;
		private String payload;

		public PostTask(String stream, String payload) {
			this.stream = stream;
			this.payload = payload;
		}

		// The HttpClient has no timeout by itself, so we abort manually
		private class RequestTimeoutTask extends TimerTask {
			
			HttpPost request = null;
			
			public RequestTimeoutTask(HttpPost request) {
				this.request = request;
			}

			@Override
			public void run() {
		        if(request != null) {
		        	request.abort();
		        }
			}
		}
		
		@Override
		public void run() {
			// new Thread required to not run blocking request in the UI thread
			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {
					
					while (stream.startsWith("/")) {
						stream = stream.substring(1);
					}
			        
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost request = new HttpPost(sharedPrefs.getString("post_base_uri", DEFAULT_BASE_URI) + stream );
					if (payload.startsWith("{")) {
						request.addHeader("Content-Type", "application/json");
					} else {
						request.addHeader("Content-Type", "text/plain");
					}
					try {
						request.setEntity(new StringEntity(payload));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					ResponseHandler<String> handler = new BasicResponseHandler();
					
					// Time out before the POST interval
					Timer timer = new Timer();
					timer.schedule(new RequestTimeoutTask(request), sharedPrefs.getInt("post_interval", 5000)-500);

					Looper.prepare();
					try {
						String response = httpclient.execute(request, handler); // blocking
						printToast.sendMessage(printToast.obtainMessage(0, "Response: "+response));
					} catch (SocketException e) {
						printToast.sendMessage(printToast.obtainMessage(0, "Error: " + e.getMessage()));
						
					} catch (ClientProtocolException e) {
						printToast.sendMessage(printToast.obtainMessage(0, "Server error: " + e.getMessage()));
						
					} catch (IOException e) {
						printToast.sendMessage(printToast.obtainMessage(0, "IO error: " + e.getMessage()));
						e.printStackTrace();
					}
					timer.cancel();
					httpclient.getConnectionManager().shutdown();
				}
			});

	        task.start();
		}
	}

	private boolean checkInterval(long last) {
		return last+sharedPrefs.getInt("post_interval", 5000)<System.currentTimeMillis();
	}
	
	public SensorEventListener sensorListener = new SensorEventListener() {

		private long lastPostCompass = 0;
		private long lastPostLight = 0;
		private long lastPostOrientation = 0;

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				// Use pre-processed by orientation
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				break;
			case Sensor.TYPE_ORIENTATION:
				sensorCompass = "" + event.values[0];
				sensorOrientationX = "" + event.values[1];
				sensorOrientationY = "" + event.values[2];
				if (sharedPrefs.getBoolean("post_compass", false) && checkInterval(lastPostCompass)) {
					lastPostCompass = System.currentTimeMillis();
					postHandler.post(new PostTask(sharedPrefs.getString("post_compass_path", "sensors"+RES_COMPASS), sensorCompass));
				}
				if (sharedPrefs.getBoolean("post_orientation", false) && checkInterval(lastPostOrientation)) {
					lastPostOrientation = System.currentTimeMillis();
					postHandler.post(new PostTask(sharedPrefs.getString("post_orientation_path", "sensors"+RES_ORIENTATION), "{\"x\":"+sensorOrientationX+",\"y\":"+sensorOrientationY+"}"));
				}
				break;
			case Sensor.TYPE_PROXIMITY:
				String dummy = "" + event.values[0];
				sensorProximity = (dummy.compareTo("0.0") == 0) ? "true" : "false";
				if (sharedPrefs.getBoolean("post_proximity", false)) {
					postHandler.post(new PostTask(sharedPrefs.getString("post_proximity_path", "sensors"+RES_PROXIMITY), sensorProximity));
				}
				break;
			case Sensor.TYPE_LIGHT:
				sensorLight = "" + event.values[0];
				if (sharedPrefs.getBoolean("post_light", false) && checkInterval(lastPostLight)) {
					lastPostLight = System.currentTimeMillis();
					postHandler.post(new PostTask(sharedPrefs.getString("post_light_path", "sensors"+RES_LIGHT), sensorLight));
				}
				break;
			default:
				break;
			}
		}
	};


	SharedPreferences sharedPrefs = null;


	private Handler postHandler;
    
    
    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        
        // sanity checks
        String test = sharedPrefs.getString("post_base_uri", DEFAULT_BASE_URI);
        if (!test.endsWith("/")) {
        	sharedPrefs.edit().putString("post_base_uri", test+"/").apply();
        }
		
		this.postHandler = new Handler();

        // Change static variable
        isRunning = true;

        // Get SensorManager
        sensorMgr  = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Initialize (custom) SensorListener for all sensors
    	sensorMgr.registerListener(sensorListener, sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    	sensorMgr.registerListener(sensorListener, sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    	sensorMgr.registerListener(sensorListener, sensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);

    	/* Actuators */
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        
        mp = MediaPlayer.create(Server.this, R.raw.remove); // Put audio file in folder res/raw/ (here remove.wav)
        mp.setVolume(10.0f, 10.0f);
        mp.setLooping(true);
    	
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
   

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        
        Thread loop = new Thread(serverLoop);
        loop.start();
        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	
    	// close the socket to kill the thread
    	try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mp.release();

    	//sensorMgr.unregisterListener(this);
    	// Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);
        
        // Change static variable
        isRunning = false;

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
        
        // Show lights
        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        //
        // Make sure the notification stays and is not removed when the user highlights it
        //
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, main.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label), text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.local_service_started, notification);
    }
    

    public synchronized void clickVibrate(View view) {
    	if (!vibrating) {
    		long[] pattern = { 0, 100, 100, 200, 100, 100 };
    		vib.vibrate(pattern, 0);
    		vibrating = true;
    	} else {
    		vib.cancel();
    		vibrating = false;
    	}
    }
    
    public synchronized void clickFlashlight(View view) {
    	
    	if (!openCam) {
	    	cam = Camera.open();
	    	openCam = true;
	    	
	    	cam.setErrorCallback(new CamCB());
	    	
	    	// get supported modes
	    	Camera.Parameters parameters = cam.getParameters();
	    	List<String> list = parameters.getSupportedFlashModes();
	    	if (list == null) return;
	    	
	    	// since 2.2 TORCH appears in list: Tada!
	        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
	        cam.setParameters(parameters);
    	} else {
    		cam.release();
    		openCam = false;
    	}
    }
    
    public void clickSound(View view) {
    	
    	if (!mp.isPlaying()) {
    		mp.start();
    	} else {
    		mp.stop();
    		try {
				mp.prepareAsync();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
    	}
    }
    
}
