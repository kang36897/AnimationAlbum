package com.gulu.album.fragment;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gulu.album.R;
import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.general.ListViewAdapter;

public class ApplicationListFragment extends ListFragment {

	private Context mContext;
	private PackageManager mPM;
	private List<ApplicationInfo> mData;
	private ListViewAdapter<ApplicationInfo> mAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPM = mContext.getPackageManager();

		mData = mPM.getInstalledApplications(0);

		mAdapter = new ListViewAdapter<ApplicationInfo>() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ApplicationItemViewHolder holder;
				if (convertView == null) {

					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_application_item, null);
					holder = new ApplicationItemViewHolder();

					holder.iConView = (ImageView) convertView.findViewById(R.id.application_icon);
					holder.nameView = (TextView) convertView.findViewById(R.id.application_lable);

					convertView.setTag(holder);

				} else {
					holder = (ApplicationItemViewHolder) convertView.getTag();
				}

				ApplicationInfo item = (ApplicationInfo) getItem(position);
				holder.iConView.setImageDrawable(item.loadIcon(mPM));
				holder.nameView.setText(item.loadLabel(mPM));

				return convertView;
			}
		};
		mAdapter.changeDataSource(mData);

	}

	static class ApplicationItemViewHolder {

		public ImageView iConView;
		public TextView nameView;
		public TextView descriptionView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ApplicationInfo item = (ApplicationInfo) l.getItemAtPosition(position);
		// mPM.setApplicationEnabledSetting(item.packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);
		//
		// try {
		// PackageInfo pi = mPM.getPackageInfo(item.packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
		// | PackageManager.GET_RECEIVERS | PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS);
		// ProviderInfo[] data = pi.providers;
		// if (data == null) {
		// Toast.makeText(getActivity(), "data is null", Toast.LENGTH_SHORT).show();
		// return;
		// }
		//
		// for (ProviderInfo provider : data) {
		// ComponentName cn = new ComponentName(provider.packageName, provider.name);
		// mPM.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
		// }
		//
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }

		try {
			PackageInfo pi = mPM.getPackageInfo(item.packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
					| PackageManager.GET_RECEIVERS | PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS);

			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ApplicationDetailsFragment detailsFragment = new ApplicationDetailsFragment();
			Bundle params = new Bundle();
			params.putParcelable(ConstantsCave.PARAMS_APPLICATION_PACKAGE_INFORMATION, pi);
			detailsFragment.setArguments(params);
			ft.replace(android.R.id.content, detailsFragment);

			ft.addToBackStack(ConstantsCave.FT_REQUEST_APPLICATION_DETAILS);
			ft.commit();

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
