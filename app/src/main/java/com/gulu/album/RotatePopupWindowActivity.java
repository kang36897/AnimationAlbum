package com.gulu.album;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.gulu.album.view.OpopupWindow;

public class RotatePopupWindowActivity extends BaseActivity {
	private OpopupWindow mPopupWindow;
	private View mTargetView;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rotate_popup);

		mTargetView = getLayoutInflater().inflate(R.layout.board_dialog, null);
		final View optsView = findViewById(R.id.btn_opts);

		optsView.post(new Runnable() {

			@Override
			public void run() {
				mPopupWindow = new OpopupWindow(RotatePopupWindowActivity.this);
				mPopupWindow.setContentView(mTargetView);
				mPopupWindow.show(optsView.getRootView());
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mPopupWindow == null) {
							return;
						}

						mPopupWindow.dismiss();

					}
				}, 3000);
			}
		});

		optsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPopupWindow.dismiss();
	}

}
