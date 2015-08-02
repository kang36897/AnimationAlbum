package com.gulu.album.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.general.ListViewAdapter;

public class ServiceListFragment extends ListFragment {
	
	private ServiceInfo[] mData;
	private ListViewAdapter<ServiceInfo> mAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle params = getArguments();
		if (params != null) {
			mData = (ServiceInfo[]) params
					.getParcelableArray(ConstantsCave.PARAMS_SERVICE_INFO_LIST);
		}
		
		mAdapter = new ListViewAdapter<ServiceInfo>() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO change the item layout
				ServiceItemViewHolder holder;
				
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(
							android.R.layout.simple_list_item_1, parent, false);
					holder = new ServiceItemViewHolder();
					holder.nameView = (TextView) convertView
							.findViewById(android.R.id.text1);
					
				} else {
					holder = (ServiceItemViewHolder) convertView.getTag();
				}
				
				ServiceInfo item = (ServiceInfo) getItem(position);
				holder.nameView.setText(item.name);
				
				return convertView;
			}
		};
		
		mAdapter.changeDataSource(mData);
	}
	
	static class ServiceItemViewHolder {
		
		public TextView nameView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}
}
