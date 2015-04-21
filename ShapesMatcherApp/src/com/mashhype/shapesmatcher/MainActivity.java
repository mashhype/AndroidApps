package com.mashhype.shapesmatcher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private DrawingView drawingView;
	private TextView timer;
	private TextView score;
	private Player player = new Player();
	boolean drag;
	Thread thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//this block of code below is what gets the top row 
		//of shape buttons to show up in the app
		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);
		drawingView.setBackgroundColor(Color.WHITE);
		//timer = (TextView) findViewById(R.id.timer);
		//score = (TextView) findViewById(R.id.score);

		/*new CountDownTimer(30000, 1000) {
			public void onTick(long millisUntilFinished) {
				timer.setText("Timer: "+String.format("%d min, %d sec", 
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - 
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
			}
			
			public void onFinish() {
				timer.setText("Time Up!");
				finish();
			}
		}.start();*/
		
		//score = (TextView) findViewById(R.id.score);
	    //score.setText("Score: " + player.getScore());

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
	
	public void onPause(View view) {
		super.onPause();
		drawingView.setVisibility(View.INVISIBLE);
		
	}
	
	public void onResume(View view) {
		super.onResume();
		drawingView.setVisibility(View.VISIBLE);
	}
	
	
	
}
