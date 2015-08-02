package com.gulu.album;

import java.util.List;

import android.app.ListActivity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BuiltInSensorsActivity extends ListActivity {
	
	private SensorManager mSensorManager;
	private List<Sensor> mData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		SensorDetailAdapter adapter = new SensorDetailAdapter();
		setListAdapter(adapter);
		
		mData = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		adapter.notifyDataSetChanged();
	}
	
	class SensorDetailAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			if (mData == null)
				return 0;
			
			return mData.size();
		}
		
		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						android.R.layout.simple_list_item_1, null);
			}
			
			TextView detailView = (TextView) convertView;
			
			Sensor sensor = mData.get(position);
			detailView.setText(sensor.toString());
			
			return detailView;
		}
		
	}
}
