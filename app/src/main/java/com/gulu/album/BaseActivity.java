package com.gulu.album;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.gulu.album.logging.util.Logable;

public class BaseActivity extends Activity implements Logable {

	@Override
	public boolean isDebuging() {
		return false;
	}

	@Override
	public String getTag() {
		return null;
	}

	@Override
	public void debug(String msg) {
		if (isDebuging() == false)
			return;

		Log.d(getTag(), msg);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ViewGroup container = (ViewGroup) findViewById(android.R.id.content);
		if(container != null){
			container.removeAllViews();
			container = null;
		}
	}
}
