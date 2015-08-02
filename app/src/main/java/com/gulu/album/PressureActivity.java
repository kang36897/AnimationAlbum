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

public class PressureActivity extends Activity implements SensorEventListener {
	
	final static int AIR_PRESSURE_CHANGED = 0x1001;
	
	private SensorManager mSensorManager;
	private Sensor mPressureSensor;
	
	private TextView mPressureView;
	private float maxRange;
	private float mPressureValue;
	
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		maxRange = mPressureSensor.getMaximumRange();
		
		setContentView(R.layout.activity_pressure);
		TextView maxView = (TextView) findViewById(R.id.maxRange);
		maxView.setText(String.format(
				getString(R.string.max_range_pressure_value_format_template),
				maxRange));
		
		mPressureView = (TextView) findViewById(R.id.pressure);
		
		mHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case AIR_PRESSURE_CHANGED:
					mPressureView.setText(String.format(
							getString(R.string.pressure_value_format_template),
							mPressureValue));
					break;
				
				default:
					break;
				}
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(this, mPressureSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		mPressureValue = event.values[0];
		
		mHandler.sendEmptyMessage(AIR_PRESSURE_CHANGED);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
