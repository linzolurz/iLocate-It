package com.lpkhoza.ilocate;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


public class DrawSurfaceView extends View {

	static Context context = null;
    Point me = new Point(-25.7534628, 28.2303968, "Me");
	Paint mPaint = new Paint();
	Paint mPaintLine = new Paint();
	private double OFFSET = 0d;
	private double screenWidth, screenHeight = 0d;
	private Bitmap[] mSpots, mBlips;
	private Bitmap mRadar;
	
	
	public static List<Point> props = new ArrayList<Point>();
	static {
		//props.add(new Point(-25.753897, 28.230718, "Zoology"));
		//props.add(new Point(-25.754474, 28.230934, "Campus Kiosk"));
		//props.add(new Point(-25.754483, 28.231443, "Student Centre"));
		//props.add(new Point(-25.7534827, 28.230425, "Ndamu's Desk"));
		//props.add(new Point(-25.754486, 28.231469, "Piazza"));
		//props.add(new Point(-25.753177, 28.230997, "Thuto"));
		//props.add(new Point(-25.753307, 28.228444, "Engineering 1"));
		//props.add(new Point(-25.753424, 28.230415, "Postgrad lab"));
		//props.add(new Point(-25.75382, 28.229913, "Lamp 1"));
		//props.add(new Point(-25.753955, 28.229815, "Lamp 2"));
		//props.add(new Point(-25.754047, 28.22999, "Fountain Statue"));
		
	}
	

	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}

	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		
		//If we don't have data in our Array then read from the CSV file
		if(props.size() == 0){
			InputStream inputStream = context.getResources().openRawResource(R.raw.mappoints);
			CSVFile csvFile = new CSVFile(inputStream);
			props = csvFile.read();
		}
		
		mPaintLine.setColor(Color.WHITE);
		mPaintLine.setTextSize(10);
		
		mPaint.setColor(Color.GREEN);
		
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
		mPaint.setAntiAlias(true);
		
		mRadar = BitmapFactory.decodeResource(context.getResources(), R.drawable.radar);
		
		mSpots = new Bitmap[props.size()];
		for (int i = 0; i < mSpots.length; i++) 
			mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);

		mBlips = new Bitmap[props.size()];
		for (int i = 0; i < mBlips.length; i++)
			mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blip);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (double) w;
		screenHeight = (double) h;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawBitmap(mRadar, 0, 0, mPaint);
		
		float descPointY = 0;
		float descVerticalSpace = 45; // This is the vertical space between each point description
		float descStartPoint = 1; // How far down should we start drawing the names
		
		int radarCentreX = mRadar.getWidth() / 2;
		int radarCentreY = mRadar.getHeight() / 2;

	
		for (int i = 0; i < mBlips.length; i++) {
			Bitmap blip = mBlips[i];
			Bitmap spot = mSpots[i];
			Point u = props.get(i);
			double dist = Math.round(distInMetres(me, u) * 10000)/10000;
			String distance = String.valueOf(dist);
			
			if(dist<70){
			
			if (blip == null || spot == null)
				continue;

			if(dist > 70)
				dist = 70; //we have set points very far away for demonstration

			double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
			double xPos, yPos;
			String latitude=String.valueOf(me.latitude);
			String longitude=String.valueOf(me.longitude);
			
			if(angle < 0)
				angle = (angle+360)%360;

			xPos = Math.sin(Math.toRadians(angle)) * dist;
			yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));

			if (angle > 90 && angle < 270)
				yPos *= -1;

			double posInPx = angle * (screenWidth / 90d);

			int blipCentreX = blip.getWidth() / 2;
			int blipCentreY = blip.getHeight() / 2;

			xPos = xPos - blipCentreX;
			yPos = yPos + blipCentreY;
			canvas.drawBitmap(blip, (radarCentreX + (int) xPos), (radarCentreY - (int) yPos), mPaint); //radar blip
			

			//reuse xPos
			int spotCentreX = spot.getWidth() / 2;
			int spotCentreY = spot.getHeight() / 2;
			xPos = posInPx - spotCentreX;

			if (angle <= 45)
				u.x = (float) ((screenWidth / 2) + xPos);

			else if (angle >= 315)
				u.x = (float) ((screenWidth / 2) - ((screenWidth*4) - xPos));

			else
				u.x = (float) (float)(screenWidth*9); //somewhere off the screen

			u.y = (float)screenHeight/2 + spotCentreY;
			
			
			if(dist<50){mPaint.setTextSize(50);
						mPaint.setColor(Color.RED);}
			else if(dist>50){mPaint.setTextSize(30);
							 mPaint.setColor(Color.GREEN);}
			
			
			descPointY = u.y/4 + descStartPoint +(descVerticalSpace * i);			
		
			// lets draw a line from the point where the marker is to the point where the text starts 
			// The " - 5" is just to have the line end and start at the middle of the point or text
			
			
			canvas.drawText(u.description +"  "+ distance + "m", u.x, descPointY, mPaint); //text
			}
		}
	}

	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	public void setMyLocation(double latitude, double longitude) {
		me.latitude = latitude;
		me.longitude = longitude;
	}

	protected double distInMetres(Point me, Point u) {

		double lat1 = me.latitude;
		double lng1 = me.longitude;

		double lat2 = u.latitude;
		double lng2 = u.longitude;

		double earthRadius = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist * 1000;
	}

	protected static double bearing(double lat1, double lon1, double lat2, double lon2) {
		double longDiff = Math.toRadians(lon2 - lon1);
		double la1 = Math.toRadians(lat1);
		double la2 = Math.toRadians(lat2);
		double y = Math.sin(longDiff) * Math.cos(la2);
		double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1) * Math.cos(la2) * Math.cos(longDiff);

		double result = Math.toDegrees(Math.atan2(y, x));
		return (result+360.0d)%360.0d;
	}
}
