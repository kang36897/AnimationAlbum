package com.gulu.album.fragment;

import android.app.ListFragment;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.general.ListViewAdapter;

public class PermissionListFragment extends ListFragment {
	
	private PermissionInfo[] mData;
	private ListViewAdapter<PermissionInfo> mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle params = getArguments();
		if (params != null) {
			mData = (PermissionInfo[]) params
					.getParcelableArray(ConstantsCave.PARAMS_PERMISSION_INFO_LIST);
		}
		
		mAdapter = new ListViewAdapter<PermissionInfo>() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				PermissionItemViewHolder holder;
				
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							android.R.layout.simple_list_item_1, parent, false);
					
					holder = new PermissionItemViewHolder();
					holder.nameView = (TextView) convertView
							.findViewById(android.R.id.text1);
					convertView.setTag(holder);
					
				} else {
					holder = (PermissionItemViewHolder) convertView.getTag();
				}
				
				PermissionInfo item = (PermissionInfo) getItem(position);
				holder.nameView.setText(item.name);
				
				return convertView;
			}
		};
		mAdapter.changeDataSource(mData);
	}
	
	static class PermissionItemViewHolder {
		
		public TextView nameView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
	}
	
}
