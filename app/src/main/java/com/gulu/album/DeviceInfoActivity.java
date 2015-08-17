package com.gulu.album;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceInfoActivity extends BaseActivity {

    private ListView mListView;
    private ArrayList<String> mData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);


        mListView = (ListView) findViewById(R.id.listView);

        StringBuilder sb = new StringBuilder();
        int densityDpi = getResources().getDisplayMetrics().densityDpi;
        float density = getResources().getDisplayMetrics().density;
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        sb.append("Display-desity: ").append(densityDpi);
        mData.add(sb.toString());
        sb.delete(0, sb.length());
        sb.append("Display-in-pixels-width*height: ")
                .append(width)
                .append("x")
                .append(height);
        mData.add(sb.toString());
        sb.delete(0, sb.length());
        sb.append("Display-in-dip-width*height: ")
                .append(width / density)
                .append("x")
                .append(height / density);
        mData.add(sb.toString());
        sb.delete(0, sb.length());

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        sb = new StringBuilder();
        sb.append("Application-limit-heap-size:").append(activityManager.getMemoryClass()).append("m");
        mData.add(sb.toString());
        sb.delete(0, sb.length());

        mListView.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,mData));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(mListView != null){
            mListView.setAdapter(null);
            mListView = null;
        }

        if(mData != null)
        {
            mData = null;
        }

    }
}
