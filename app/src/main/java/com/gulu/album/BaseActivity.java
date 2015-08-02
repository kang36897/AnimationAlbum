package com.gulu.album;

import android.app.Activity;
import android.util.Log;

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

}
