package ch.ethz.inf.vs.android.restserver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.view.WindowManager;

public class Display extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        
        Bundle extras = getIntent().getExtras();
        int color = extras.getInt("color");
        int brightness = extras.getInt("brightness");
        String text = extras.getString("text");

        TextView display = (TextView) findViewById(R.id.display);
        display.setGravity(0x10 | 0x01);
        display.setTextColor(Color.WHITE);
    	display.setBackgroundColor(color);
    	setBrightness(brightness);
    	display.setText(text);
    	display.setTextSize(72f);

		//Toast.makeText(Display.this, ""+brightness, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onNewIntent(Intent i) {
        Bundle extras = i.getExtras();
        int color = extras.getInt("color");
        int brightness = extras.getInt("brightness");
        String text = extras.getString("text");

    	TextView display = (TextView) findViewById(R.id.display);
    	display.setGravity(0x10 | 0x01);
    	display.setTextColor(Color.WHITE);
    	display.setBackgroundColor(color);
    	setBrightness(brightness);
     	display.setText(text);
    	display.setTextSize(72f);
    }
    
    private  void setBrightness(int value) {
    	if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }
    	WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (float)value / 100f;       
        lp.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(lp);
    }
}
