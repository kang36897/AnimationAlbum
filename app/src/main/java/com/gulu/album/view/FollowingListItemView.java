package com.gulu.album.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015/7/13.
 */
public class FollowingListItemView extends ViewGroup {

    public FollowingListItemView(Context context){
        super(context);
    }



    public FollowingListItemView(Context context, AttributeSet attrs){
        this(context,attrs, 0);
    }

    public FollowingListItemView(Context context, AttributeSet attrs, int defStyle) {
      super(context,attrs,defStyle);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
