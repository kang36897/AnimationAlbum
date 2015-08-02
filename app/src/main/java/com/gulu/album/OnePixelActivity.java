package com.gulu.album;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;

public class OnePixelActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, getResources().getDisplayMetrics());
		int height = width;
		// View view = new View(this);
		// view.setBackgroundColor(Color.GREEN);
		// view.
		// view.setLayoutParams(new LayoutParams(width, height));
		// setContentView(view);
		getWindow().setLayout(width, height);
		// getWindow().setBackgroundDrawable(new ColorDrawable(Color.GREEN));
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
		// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	};
}
