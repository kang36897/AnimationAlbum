package com.gulu.album;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SendShortCutRequestActivity extends Activity {
	
	final static int RC_GET_SHORTCUT = 0x11;
	
	private TextView mTipView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_shortcut_request);
		
		mTipView = ((TextView) findViewById(R.id.send_action));
		mTipView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
				startActivityForResult(i, RC_GET_SHORTCUT);
				
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == RC_GET_SHORTCUT) {
			
			if (resultCode == RESULT_OK) {
				ImageView v = (ImageView) findViewById(R.id.shortcut);
				v.setVisibility(View.VISIBLE);
				
				String shortcutName = data
						.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
				final Intent shortcutIntent = data
						.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
				Bitmap icon = data
						.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
				ShortcutIconResource iconResource = data
						.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
				
				Editable editable = (Editable) mTipView.getText();
				editable.append(";get:").append(shortcutName);
				mTipView.setText(editable);
				
				if (icon == null) {
					PackageManager pm = getPackageManager();
					try {
						Resources rs = pm
								.getResourcesForApplication(iconResource.packageName);
						Drawable drawable = rs.getDrawable(rs.getIdentifier(
								iconResource.resourceName, null, null));
						v.setImageDrawable(drawable);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					
				} else {
					v.setImageBitmap(icon);
					v.setScaleType(ScaleType.FIT_CENTER);
				}
				
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						startActivity(shortcutIntent);
						
					}
				});
				
			}
			
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}
}
