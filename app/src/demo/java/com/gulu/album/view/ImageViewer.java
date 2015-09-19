package com.gulu.album.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.gulu.album.R;

/**
 * Created by Administrator on 2015/9/17.
 */
public class ImageViewer extends View {

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private GestureListener mGestureListener ;

    private final static float SCALE_LIMIT = 2.0f;

    private String mDataSource;

    private Bitmap mSourceBitmap;
    private Rect mContentBounds = new Rect();

    private float mScale = 1.0f;
    private Rect mSrc = new Rect();
    private Rect mDst = new Rect();
    private Rect mSrcOrignal = new Rect();
    private Rect mDstOrignal = new Rect();

    public ImageViewer(Context context) {
        super(context);
        initialization(context);
    }

    public ImageViewer(Context context, AttributeSet attrs) {

        super(context, attrs);
        initialization(context);
    }

    public ImageViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }



    private void initialization(Context context)
    {
        mGestureListener = new GestureListener();
        mScaleGestureDetector = new ScaleGestureDetector(context, mGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);

        mSourceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.football_girl);

    }

    public void setPreviewImage(Bitmap bitmap){
        mSourceBitmap = bitmap;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mContentBounds.set(0, 0, w, h);

        int dw = mSourceBitmap.getWidth();
        int dh = mSourceBitmap.getHeight();
        mSrc.set(0, 0, dw, dh);
        mSrcOrignal.set(mSrc);

        int nw = 0;
        int nh = 0;
        float scale = 1;
        if(dw * h > w * dh)
        {
            nw = w;
            scale = (float) w / (float)dw;
            nh = (int)(dh * scale);

        }else
        {
            nw =(int) (dw / scale);
            nh = (int) (dh /scale);

            while( nw > w || nh > h)
            {
                scale *= 2;

                nw =(int) (nw / scale);
                nh = (int) (nh /scale);
            }

        }

        Gravity.apply(Gravity.CENTER,nw, nh, mContentBounds, mDst);
        mDstOrignal.set(mDst);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);



       canvas.drawBitmap(mSourceBitmap, mSrc, mDst, null);



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {







            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
