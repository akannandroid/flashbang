package com.example.flashbang;

import java.util.HashMap;

import android.support.v7.app.ActionBarActivity; 
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	private final float NOISE = (float) 10.0;
	public final int flashsound = R.raw.flash;
	private static SoundPool soundpool;
	int mysound;
	boolean loaded = false;
	Camera mCamera;
	Parameters params;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		setContentView(R.layout.main_lay);
		soundpool = new SoundPool(2,AudioManager.STREAM_MUSIC, 0);
		AudioManager aud = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		mCamera = Camera.open();
		params = mCamera.getParameters();
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mysound = soundpool.load(this,R.raw.flash,1);
		
		soundpool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
		       loaded = true;
		    }
		});
		
		
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		mCamera.release();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mysound = soundpool.load(this,R.raw.flash,1);
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		//ImageView iv = (ImageView)findViewById(R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if (!mInitialized) {
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				tvX.setText("0.0");
				tvY.setText("0.0");
				tvZ.setText("0.0");
				mInitialized = true;
			} 
		else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));
			//iv.setVisibility(View.VISIBLE);
		
			if (deltaX > deltaY) {
				Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show();
				LinearLayout rl = (LinearLayout)this.findViewById(R.id.main_lay);
				rl.setBackgroundColor(Color.argb(50, 22, 99, 76));
					if (loaded){
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
						mCamera.setParameters(params);
						mCamera.startPreview();
					soundpool.play(1, 1, 1, 1, 1, 1f);
						mCamera.stopPreview();
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(params);
					}
				
				} 
			else if (deltaY > deltaX) {
				Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show();
				LinearLayout rl = (LinearLayout)this.findViewById(R.id.main_lay);
				rl.setBackgroundColor(Color.argb(150, 200, 37, 55));
				;
					if(loaded){
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
						mCamera.setParameters(params);
						mCamera.startPreview();
					soundpool.play(1, 1, 1, 1, 1, 1f);
						mCamera.stopPreview();
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(params);
					}
				} 
			else {
				//iv.setVisibility(View.INVISIBLE);
				}
			}
		
		}
		
	

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	


}
