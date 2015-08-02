package com.gulu.album.fragment;

import android.app.ListFragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.general.ListViewAdapter;

public class ReceiverListFragment extends ListFragment {
	
	private ActivityInfo[] mData;
	private ListViewAdapter<ActivityInfo> mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle params = getArguments();
		if (params != null) {
			mData = (ActivityInfo[]) params
					.getParcelableArray(ConstantsCave.PARAMS_RECEIVER_INFO_LIST);
		}
		
		mAdapter = new ListViewAdapter<ActivityInfo>() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ReceiverItemViewHolder holder;
				
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							android.R.layout.simple_list_item_1, parent, false);
					
					holder = new ReceiverItemViewHolder();
					holder.nameView = (TextView) convertView
							.findViewById(android.R.id.text1);
					convertView.setTag(holder);
					
				} else {
					holder = (ReceiverItemViewHolder) convertView.getTag();
				}
				
				ActivityInfo item = (ActivityInfo) getItem(position);
				holder.nameView.setText(item.name);
				
				return convertView;
			}
		};
		mAdapter.changeDataSource(mData);
	}
	
	static class ReceiverItemViewHolder {
		
		public TextView nameView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
	}
}
