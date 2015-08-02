package com.gulu.album;

import android.app.Activity;
import android.os.Bundle;

import com.gulu.album.view.CustomerMapView;

public class ShowCustomerMapActivity extends Activity {
	
	private CustomerMapView mMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_customer_map);
		
		mMapView = (CustomerMapView) findViewById(R.id.custom_map);
		mMapView.updateMap();
	}
}
