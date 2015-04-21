package com.mashhype.fallingblocks;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Parts of this java class file can be attributed to:
 * Mobile App Development for Android Platform Module 14a & 14b
 * offered through JHU EP authored by Scott Stanchfield
 */
public class MainActivity extends Activity {
	private GameScreen gameScreen;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private Display display;
	private Vibrator vibrate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		gameScreen = new GameScreen(this);
		gameScreen = (GameScreen) findViewById(R.id.main);
		//gameScreen.setBackgroundColor(Color.WHITE);
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
	}
	
	SensorEventListener accelListener = new SensorEventListener() {
		@Override public void onSensorChanged(final SensorEvent event) {
			runOnUiThread(new Runnable() {
				@Override public void run() {
					float x = 0;
					float y = 0;
					switch (display.getRotation()) {
						case Surface.ROTATION_0:
							x = event.values[0];
							y = event.values[1];
							break;
						case Surface.ROTATION_90:
							x = -event.values[1];
							y = event.values[0];
							break;
						case Surface.ROTATION_180:
							x = -event.values[0];
							y = -event.values[1];
							break;
						case Surface.ROTATION_270:
							x = event.values[1];
							y = -event.values[0];
							break;
					}
					gameScreen.changeAccel(-x, y);
				}});
		}
		@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("ACCURACY CHANGE", sensor.getName() + ": " + accuracy);
		}
	};
	
	SensorEventListener magListener = new SensorEventListener() {
		@Override public void onSensorChanged(final SensorEvent event) {
			runOnUiThread(new Runnable() {
				@Override public void run() {
					float x = 0;
					float y = 0;
					switch (display.getRotation()) {
						case Surface.ROTATION_0:
							x = event.values[0];
							y = event.values[1];
							break;
						case Surface.ROTATION_90:
							x = -event.values[1];
							y = event.values[0];
							break;
						case Surface.ROTATION_180:
							x = -event.values[0];
							y = -event.values[1];
							break;
						case Surface.ROTATION_270:
							x = event.values[1];
							y = -event.values[0];
							break;
					}
					gameScreen.changeMag(-x, y);
				}});
		}
		@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("ACCURACY CHANGE", sensor.getName() + ": " + accuracy);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(accelListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(magListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		gameScreen.start();
	}
	
    @Override
    protected void onPause() {
    	gameScreen.stop();
    	sensorManager.unregisterListener(accelListener);
    	sensorManager.unregisterListener(magListener);
    	super.onPause();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
