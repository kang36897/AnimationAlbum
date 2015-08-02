package com.gulu.album.fragment;

import android.app.ListFragment;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.general.ListViewAdapter;

public class ProviderListFragment extends ListFragment {

	private ProviderInfo[] mData;
	private ListViewAdapter<ProviderInfo> mAdapter;
	private PackageManager mPM;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPM = getActivity().getPackageManager();

		Bundle params = getArguments();
		if (params != null) {
			mData = (ProviderInfo[]) params.getParcelableArray(ConstantsCave.PARAMS_PROVIDER_INFO_LIST);
		}

		mAdapter = new ListViewAdapter<ProviderInfo>() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ReceiverItemViewHolder holder;

				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);

					holder = new ReceiverItemViewHolder();
					holder.nameView = (TextView) convertView.findViewById(android.R.id.text1);
					convertView.setTag(holder);

				} else {
					holder = (ReceiverItemViewHolder) convertView.getTag();
				}

				ProviderInfo item = (ProviderInfo) getItem(position);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ProviderInfo item = (ProviderInfo) mAdapter.getItem(position);
		ComponentName cn = new ComponentName(item.packageName, item.name);
		mPM.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
	}
}
