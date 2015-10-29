package com.lpkhoza.ilocate;

import com.lpkhoza.ilocate.DrawSurfaceView;
import com.lpkhoza.ilocate.LocationUtils;
import com.lpkhoza.ilocate.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;

public class MainActivity extends Activity{
	
	protected PowerManager.WakeLock mWakelock;

	private static final String TAG = "Compass";
	private static boolean DEBUG = false;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private DrawSurfaceView mDrawView;
	LocationManager locMgr;
	
	private final SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (DEBUG)
				Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
			if (mDrawView != null) {
				mDrawView.setOffset(event.values[0]);
				mDrawView.invalidate();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		setContentView(R.layout.activity_main);
		
		//this keeps the screen on until activity is destroyed
				final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				this.mWakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
				this.mWakelock.acquire();
				
				mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);
				
				locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE); // <2>
				LocationProvider high = locMgr.getProvider(locMgr.getBestProvider(
						LocationUtils.createFineCriteria(), true));

				// using high accuracy provider... to listen for updates
				locMgr.requestLocationUpdates(high.getName(), 0, 0f,
						new LocationListener() {
							public void onLocationChanged(Location location) {
								// do something here to save this new location
								Log.d(TAG, "Location Changed");
								mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
								mDrawView.invalidate();
							}

							public void onStatusChanged(String s, int i, Bundle bundle) {

							}

							public void onProviderEnabled(String s) {
								// try switching to a different provider
							}

							public void onProviderDisabled(String s) {
								// try switching to a different provider
							}
						});

			}

			@Override
			protected void onResume() {
				if (DEBUG)
					Log.d(TAG, "onResume");
				super.onResume();

				mSensorManager.registerListener(mListener, mSensor,
						SensorManager.SENSOR_DELAY_GAME);
			}

			@Override
			protected void onStop() {
				if (DEBUG)
					Log.d(TAG, "onStop");
				mSensorManager.unregisterListener(mListener);
				super.onStop();
			}
			
			@Override
			public void onDestroy(){
				this.mWakelock.release();
				super.onDestroy();
			}
		}

