package com.lpkhoza.ilocate;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	@SuppressWarnings("unused")
	private static final String TAG = "CameraSurfaceView";
	private SurfaceHolder holder;
	private Camera camera;

	// private DrawSurfaceView draw;

	public CameraSurfaceView(Context context) {
		super(context);

		// Initiate the Surface Holder properly
		this.holder = this.getHolder();
		this.holder.addCallback(this);
		// this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@SuppressWarnings("deprecation")
	public CameraSurfaceView(Context context, AttributeSet set) {
		super(context, set);

		// Initiate the Surface Holder properly
		this.holder = this.getHolder();
		this.holder.addCallback(this);
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// Open the Camera in preview mode
			this.camera = Camera.open();
			Configuration c = getResources().getConfiguration();
			 
			if(c.orientation == Configuration.ORIENTATION_PORTRAIT ) {
				this.camera.setDisplayOrientation(90);	
			}

			this.camera.setPreviewDisplay(this.holder);
			// this.camera.setPreviewCallback(mPreviewCallback);

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.d("Test", "SurfaceChanged");

		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> csc = parameters.getSupportedPreviewSizes();
		Camera.Size cs = null;

		for (Camera.Size s : csc) {
			if (s.width == 640 && s.height == 480) {
				cs = s;
				break;
			}
		}
		if (cs == null)
			cs = csc.get(csc.size() / 2);

		parameters.setPictureSize(cs.width, cs.height);

		camera.setParameters(parameters);
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when replaced with a new screen
		// Always make sure to release the Camera instance
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public Camera getCamera() {
		return this.camera;
	}

	public void setScreenshot(PictureCallback rawPictureCallback,
			PictureCallback jpegPictureCallback) {
		camera.takePicture(null, rawPictureCallback, jpegPictureCallback);
	}

	Point screenSize = null;

	
}
