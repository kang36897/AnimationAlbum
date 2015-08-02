package com.gulu.album;

import android.os.Bundle;

import com.gulu.album.view.MirrorView;

public class MirrorActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new MirrorView(this));

	}
}
