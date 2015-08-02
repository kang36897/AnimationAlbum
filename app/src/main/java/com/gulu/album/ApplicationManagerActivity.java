package com.gulu.album;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.gulu.album.fragment.ApplicationListFragment;

public class ApplicationManagerActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			FragmentManager fm = getFragmentManager();

			FragmentTransaction ft = fm.beginTransaction();
			ft.add(android.R.id.content, new ApplicationListFragment());
			ft.commit();
		}

	}

}
