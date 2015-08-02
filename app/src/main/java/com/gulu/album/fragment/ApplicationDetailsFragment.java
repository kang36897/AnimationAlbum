package com.gulu.album.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gulu.album.R;
import com.gulu.album.constants.ConstantsCave;
import com.gulu.album.utils.DateFormatUtils;

public class ApplicationDetailsFragment extends Fragment {

	private PackageInfo mPi;

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.activity_number_value:
				showIncludedActivityes();
				break;

			case R.id.services_number_value:
				showIncludedServices();
				break;

			case R.id.receivers_number_value:
				showIncludedReceivers();
				break;

			case R.id.declared_permission_number_value:
				showIncludedDeclaredPermission();
				break;

			case R.id.requested_permission_number_value:
				break;

			case R.id.providers_number_value:
				showIncludedProviders();
				break;
			default:
				break;

			}

		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	protected void showIncludedDeclaredPermission() {
		if (mPi.permissions == null) {
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		PermissionListFragment rf = new PermissionListFragment();

		Bundle params = new Bundle();
		params.putParcelableArray(ConstantsCave.PARAMS_PERMISSION_INFO_LIST, mPi.permissions);
		rf.setArguments(params);

		ft.replace(android.R.id.content, rf);
		ft.addToBackStack(ConstantsCave.FT_REQUEST_PERMISSION_LIST);
		ft.commit();

	}

	protected void showIncludedProviders() {
		if (mPi.providers == null) {
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ProviderListFragment rf = new ProviderListFragment();

		Bundle params = new Bundle();
		params.putParcelableArray(ConstantsCave.PARAMS_PROVIDER_INFO_LIST, mPi.providers);
		rf.setArguments(params);

		ft.replace(android.R.id.content, rf);
		ft.addToBackStack(ConstantsCave.FT_REQUEST_PROVIDER_LIST);
		ft.commit();
	}

	protected void showIncludedReceivers() {
		if (mPi.receivers == null) {
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ReceiverListFragment rf = new ReceiverListFragment();

		Bundle params = new Bundle();
		params.putParcelableArray(ConstantsCave.PARAMS_RECEIVER_INFO_LIST, mPi.receivers);
		rf.setArguments(params);

		ft.replace(android.R.id.content, rf);
		ft.addToBackStack(ConstantsCave.FT_REQUEST_RECEIVER_LIST);
		ft.commit();

	}

	protected void showIncludedServices() {
		if (mPi.services == null) {
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ServiceListFragment sf = new ServiceListFragment();

		Bundle params = new Bundle();
		params.putParcelableArray(ConstantsCave.PARAMS_SERVICE_INFO_LIST, mPi.services);
		sf.setArguments(params);

		ft.replace(android.R.id.content, sf);
		ft.addToBackStack(ConstantsCave.FT_REQUEST_SERVICE_LIST);
		ft.commit();
	}

	protected void showIncludedActivityes() {
		if (mPi.activities == null) {
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ActivityListFragment af = new ActivityListFragment();

		Bundle params = new Bundle();
		params.putParcelableArray(ConstantsCave.PARAMS_ACTIVITY_INFO_LIST, mPi.activities);
		af.setArguments(params);

		ft.replace(android.R.id.content, af);
		ft.addToBackStack(ConstantsCave.FT_REQUEST_ACTIVITY_LIST);
		ft.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle params = getArguments();
		if (params != null) {
			mPi = params.<PackageInfo> getParcelable(ConstantsCave.PARAMS_APPLICATION_PACKAGE_INFORMATION);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View pContentView = inflater.inflate(R.layout.fragment_application_details, null);

		TextView pnView = (TextView) pContentView.findViewById(R.id.pn_value);
		pnView.setText(mPi.packageName);

		TextView vnView = (TextView) pContentView.findViewById(R.id.vn_value);
		vnView.setText(mPi.versionName);

		TextView fiView = (TextView) pContentView.findViewById(R.id.fi_value);
		fiView.setText(DateFormatUtils.formatTimeToString(mPi.firstInstallTime));

		TextView udView = (TextView) pContentView.findViewById(R.id.lut_value);
		udView.setText(DateFormatUtils.formatTimeToString(mPi.lastUpdateTime));

		TextView anView = (TextView) pContentView.findViewById(R.id.activity_number_value);
		anView.setText(Integer.toString(mPi.activities == null ? 0 : mPi.activities.length));
		anView.setOnClickListener(mOnClickListener);

		TextView snView = (TextView) pContentView.findViewById(R.id.services_number_value);
		snView.setText(Integer.toString(mPi.services == null ? 0 : mPi.services.length));
		snView.setOnClickListener(mOnClickListener);

		TextView rnView = (TextView) pContentView.findViewById(R.id.receivers_number_value);
		rnView.setText(Integer.toString(mPi.receivers == null ? 0 : mPi.receivers.length));
		rnView.setOnClickListener(mOnClickListener);

		TextView dpView = (TextView) pContentView.findViewById(R.id.declared_permission_number_value);
		dpView.setText(Integer.toString(mPi.permissions == null ? 0 : mPi.permissions.length));
		dpView.setOnClickListener(mOnClickListener);

		TextView rpView = (TextView) pContentView.findViewById(R.id.requested_permission_number_value);
		rpView.setText(Integer.toString(mPi.requestedPermissions == null ? 0 : mPi.requestedPermissions.length));
		rpView.setOnClickListener(mOnClickListener);

		TextView prView = (TextView) pContentView.findViewById(R.id.providers_number_value);
		prView.setText(Integer.toString(mPi.providers == null ? 0 : mPi.providers.length));
		prView.setOnClickListener(mOnClickListener);
		return pContentView;
	}
}
