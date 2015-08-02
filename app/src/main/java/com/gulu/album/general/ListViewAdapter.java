package com.gulu.album.general;

import java.util.Arrays;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class ListViewAdapter<T> extends BaseAdapter {
	
	private List<T> mData;
	
	public ListViewAdapter() {
		
	}
	
	public ListViewAdapter(List<T> input) {
		mData = input;
	}
	
	public void changeDataSource(List<T> input) {
		mData = input;
	}
	
	public void changeDataSource(T[] input) {
		mData = Arrays.asList(input);
	}
	
	@Override
	public int getCount() {
		if (mData == null)
			return 0;
		
		return mData.size();
	}
	
	@Override
	public Object getItem(int position) {
		if (position < 0 || position > mData.size())
			return null;
		
		return mData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// you can override this function for special use
		return 0;
	}
	
}
