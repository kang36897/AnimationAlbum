package com.gulu.album;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class RotateActivity extends BaseActivity {
	final static int ID_SENSOR_CHANGED = 0X11;
	private SensorManager mSensorManager;

	private Sensor mAccelerometer;
	private Sensor mMagneticSensor;

	private boolean readyA = false;
	private boolean readyB = false;
	private float[] gravity;
	private float[] geomagnetic;
	private float[] mOrientation;
	private SensorEventListener mSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor == mAccelerometer) {
				gravity = event.values;
				readyA = true;
			} else if (event.sensor == mMagneticSensor) {
				geomagnetic = event.values;
				readyB = true;
			}

			if (readyA && readyB) {
				float[] mRotateMatrix = new float[9];
				if (mSensorManager.getRotationMatrix(mRotateMatrix, null, gravity, geomagnetic)) {
					float[] values = new float[3];
					mOrientation = mSensorManager.getOrientation(mRotateMatrix, values);
					Message msg = mHandler.obtainMessage(ID_SENSOR_CHANGED);
					msg.obj = values;
					msg.sendToTarget();
				}

			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	private TextView azimuthView;
	private TextView pitchView;
	private TextView rollView;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rotate_sensor);

		azimuthView = (TextView) findViewById(R.id.azimuth);
		pitchView = (TextView) findViewById(R.id.pitch);
		rollView = (TextView) findViewById(R.id.roll);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ID_SENSOR_CHANGED:
					float[] orientation = (float[]) msg.obj;
					azimuthView.setText(String.valueOf(Math.toDegrees(orientation[0])));
					pitchView.setText(String.valueOf(Math.toDegrees(orientation[1])));
					rollView.setText(String.valueOf(Math.toDegrees(orientation[2])));
					break;

				default:
					super.handleMessage(msg);
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAccelerometer != null && mMagneticSensor != null) {
			mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
			mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAccelerometer != null && mMagneticSensor != null) {
			mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);
			mSensorManager.unregisterListener(mSensorEventListener, mMagneticSensor);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
