package com.gulu.album;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class ProximityActivity extends Activity implements SensorEventListener {
	
	final static int PROXIMITY_DISTANCE_CHANGED = 0x01;
	
	private SensorManager mSensorManager;
	private Sensor mProximitySensor;
	private float mDistance;
	
	private Handler mHandler;
	private TextView mDistanceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mProximitySensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		
		initHandler();
		
		setContentView(R.layout.activity_proximity);
		mDistanceView = (TextView) findViewById(R.id.distance);
	}
	
	public void initHandler() {
		mHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case PROXIMITY_DISTANCE_CHANGED:
					mDistanceView.setText(mDistance + "cm");
					break;
				
				default:
					break;
				}
			}
		};
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		mDistance = event.values[0];
		mHandler.sendEmptyMessage(PROXIMITY_DISTANCE_CHANGED);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mProximitySensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
}
