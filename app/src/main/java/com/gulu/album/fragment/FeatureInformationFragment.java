package com.gulu.album.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gulu.album.R;
import com.gulu.album.general.ListViewAdapter;

public class FeatureInformationFragment extends Fragment {
	
	private Context mContext;
	private FeatureInfo[] mData;
	private ListViewAdapter<FeatureInfo> mAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		PackageManager pm = mContext.getPackageManager();
		mData = pm.getSystemAvailableFeatures();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mAdapter = new ListViewAdapter<FeatureInfo>() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				FeatureItemViewHolder holder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.list_item_feature_item, null);
					holder = new FeatureItemViewHolder();
					holder.fName = (TextView) convertView
							.findViewById(R.id.feature_name);
					convertView.setTag(holder);
				} else {
					holder = (FeatureItemViewHolder) convertView.getTag();
				}
				
				FeatureInfo item = (FeatureInfo) getItem(position);
				holder.fName.setText(item.name);
				
				return convertView;
			}
		};
		mAdapter.changeDataSource(mData);
	}
	
	static class FeatureItemViewHolder {
		
		TextView fName;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView view = (ListView) inflater.inflate(
				R.layout.fragment_feature_information, container, false);
		view.setAdapter(mAdapter);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		ListView listView = (ListView) getActivity()
				.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FeatureInfo item = (FeatureInfo) parent
						.getItemAtPosition(position);
				Toast.makeText(mContext, item.name + ":" + item.flags,
						Toast.LENGTH_SHORT).show();
			}
			
		});
		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mData = null;
		mAdapter = null;
		mContext = null;
	}
	
}
