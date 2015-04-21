package com.mashhype.fallingblocks;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class GameOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
