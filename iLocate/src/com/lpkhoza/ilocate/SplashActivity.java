package com.lpkhoza.ilocate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	/*Splash interval*/
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		/*Handler to start the main activity after loading the splash*/
		new  Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
		
	}

}
