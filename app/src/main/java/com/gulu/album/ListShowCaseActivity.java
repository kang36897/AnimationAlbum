package com.gulu.album;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListShowCaseActivity extends Activity {
	
	private ListView mListView;
	private ActivityInfo[] mAcivityInfoArray;
	private String[] mItemData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListView = new ListView(this);
		setContentView(mListView);
		
		PackageManager pm = getPackageManager();
		try {
			String packageName = getApplicationInfo().packageName;
			PackageInfo packageInfo = pm.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			
			ActivityInfo[] tempAcivityInfoArray = packageInfo.activities;
			if (tempAcivityInfoArray == null) {
				finish();
				return;
			}
			
			int size = tempAcivityInfoArray.length - 1;
			int index = 0;
			mItemData = new String[size];
			mAcivityInfoArray = new ActivityInfo[size];
			for (int i = tempAcivityInfoArray.length - 1; i >= 0 ; i--) {
				if (tempAcivityInfoArray[i].name.contains(getLocalClassName())) {
					continue;
				}
				
				mAcivityInfoArray[index] = tempAcivityInfoArray[i];
				int resId = tempAcivityInfoArray[i].labelRes;
				
				if (resId == 0) {
					mItemData[index] = tempAcivityInfoArray[i].name;
					index++;
					continue;
				}
				
				mItemData[index] = getResources().getString(resId);
				index++;
			}


			
			mListView.setAdapter(new ArrayAdapter<>(this,
					android.R.layout.simple_list_item_1, mItemData));
			mListView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ActivityInfo activityInfo = mAcivityInfoArray[position];
					Intent intent = new Intent();
					intent.setClassName(activityInfo.packageName,
							activityInfo.name);
					
					startActivity(intent);
				}
			});
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();


		if(mListView != null){
			mListView.setAdapter(null);
			mListView = null;
		}
	}
}
