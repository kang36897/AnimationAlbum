package com.gulu.album;

import java.util.UUID;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class IdentifyDeviceActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identify_device);

		TextView androidIdView = (TextView) findViewById(R.id.android_id);
		String androdiId = getAndroidId(getApplicationContext());
		androidIdView.setText(androdiId);

		TextView serialNumberView = (TextView) findViewById(R.id.serial_number);
		String serialNumber = getSerialNumber();
		serialNumberView.setText(serialNumber);

		TextView deviceIdView = (TextView) findViewById(R.id.device_id);
		String deviceId = getDeviceId(getApplicationContext());
		deviceIdView.setText(deviceId);

		UUID uuid = new UUID(androdiId.hashCode(), (deviceId.hashCode() << 32 | serialNumber.hashCode()));
		TextView uuidView = (TextView) findViewById(R.id.generate_uuid);
		uuidView.setText(uuid.toString());
	}

	public static String getAndroidId(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static String getSerialNumber() {
		return Build.SERIAL;
	}

	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		return deviceId == null ? telephonyManager.getSubscriberId() : deviceId;
	}

}
