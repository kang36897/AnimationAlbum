package com.gulu.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2015/9/10.
 */
public class DotView extends View {
    private final static int NORMAL_COLOR = Color.parseColor("#efffffff");
    private final static int SELECTED_COLOR = Color.parseColor("#fff");
    private boolean mIsChecked = false;
    private Paint mPaint;


    public DotView(Context context) {
        super(context);
        initialization();
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization();
    }

    public void setChecked(boolean checked)
    {
        if(mIsChecked == checked)
        {
            return;
        }

        mIsChecked = checked;
        invalidate();

    }


    private void initialization()
    {


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        int innerWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int innerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        float cx =  innerWidth * 0.5f;
        float cy = innerHeight * 0.5f;
        float radius = Math.min(cx,cy);


        if(mIsChecked)
        {
            mPaint.setColor(SELECTED_COLOR);
        }else
        {
            mPaint.setColor(NORMAL_COLOR);

        }

        canvas.drawCircle(cx, cy, radius, mPaint);



    }
}
