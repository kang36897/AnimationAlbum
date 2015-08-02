package com.gulu.album;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gulu.album.R;
import com.gulu.album.fragment.FeatureInformationFragment;

public class DeviceManagerActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_device_manager);
		
		FragmentTransaction fTransaction = getSupportFragmentManager()
				.beginTransaction();
		fTransaction.add(R.id.content_container,
				new FeatureInformationFragment());
		fTransaction.commit();
	}
	
}
