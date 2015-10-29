package com.lpkhoza.ilocate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

public class OverlayView extends View implements SensorEventListener, LocationListener {
	
	public static final String DEBUG_TAG = "OverlayView Log";
	
	private final Context context;
	private final Handler handler;
	
	String accelData = "Accelerometer Data";
	String compassData = "Compass Data";
	String gyroData = "Gyro Data";
	
	private LocationManager locationManager = null;
	private SensorManager sensors = null;
	
	private float verticalFOV;
	private float horizontalFOV;
	
	private Location lastLocation;
	private float[] lastAccelerometer;
	private float[] lastCompass;
	
	private boolean isAccelAvailable;
	private boolean isCompassAvailable;
	private boolean isGyroAvailable;
	private Sensor accelSensor;
	private Sensor compassSensor;
	private Sensor gyroSensor;
	
	private TextPaint contentPaint;
	
	public OverlayView(Context context){
		super(context);
		this.context = context;
		this.handler = new Handler();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		sensors = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gyroSensor = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		startSensors();
		startGPS();
		
		Camera camera = Camera.open();
		Camera.Parameters params = camera.getParameters();
		verticalFOV = params.getVerticalViewAngle();
		horizontalFOV = params.getHorizontalViewAngle();
		camera.release();
		
		contentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		contentPaint.setTextAlign(Align.LEFT);
		contentPaint.setTextSize(20);
		contentPaint.setColor(Color.RED);
		
	}
	
	private void startSensors(){
		isAccelAvailable = sensors.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
		isCompassAvailable = sensors.registerListener(this, compassSensor,SensorManager.SENSOR_DELAY_NORMAL);
		isGyroAvailable = sensors.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void startGPS(){
		Criteria criteria = new Criteria();
		
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		
		String best = locationManager.getBestProvider(criteria, true);
		
		Log.v(DEBUG_TAG, "Best provider: "+ best);
		
		locationManager.requestLocationUpdates(best, 10, 0, this);
	}
	
	@Override
	protected void  onDraw(Canvas canvas){
		
		super.onDraw(canvas);
		
		StringBuilder text = new StringBuilder(accelData).append("\n");
		text.append(compassData).append("\n");
		text.append(gyroData).append("\n");
	}
	
	public void onAccuracyChanged(Sensor arg0, int arg1){
		Log.d(DEBUG_TAG, "onAccuracyChanged");
	}
	
	public void onSensorChanged(SensorEvent event){
		
		StringBuilder msg = new StringBuilder(event.sensor.getName()).append(" ");
		for(float value : event.values){
			msg.append("[").append(String.format("%.3f", value)).append("]");
		}
		
		switch (event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
		lastAccelerometer = event.values.clone();
		accelData = msg.toString();
		break;
		case Sensor.TYPE_GYROSCOPE:
			gyroData = msg.toString();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			lastCompass = event.values.clone();
			compassData = msg.toString();
			break;
		}
		this.invalidate();
	}
	public void onResume() {
		startSensors();
		startGPS();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
