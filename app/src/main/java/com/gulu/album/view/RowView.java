package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.gulu.album.R;

/**
 * TODO: document your custom view class.
 */
public class RowView extends ViewGroup {


 /*   private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;*/



    public RowView(Context context) {
        super(context);
        init(null, 0);
    }

    public RowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(null,0,0,opts);
        int oWidth = opts.outWidth;
        int oHeight = opts.outHeight;

        float imageRatio = oWidth / oHeight;
        float frameRatio = width / height;

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = calculateSampleSizeFromFrame(oWidth, oHeight, width, height);

        Bitmap source =  BitmapFactory.decodeByteArray(null, 0, 0, opts);
        boolean wFlag = width > source.getWidth();
        boolean hFlag = height > source.getHeight();
        if( wFlag || hFlag ){

            int desiredWidth = width;
            int desiredHeight = height;

            if(imageRatio > 1.0f){
                if(frameRatio > 1.0f){
                    if(frameRatio > imageRatio){
                        desiredWidth = width;
                        desiredHeight = (int)(width / imageRatio);
                    }else if(frameRatio < imageRatio){
                         desiredHeight = height;
                         desiredWidth = (int)(desiredHeight * imageRatio);

                    }else{
                        // just use the orignal frame size to scale the image
                    }

                }else{
                     desiredHeight = height;
                     desiredWidth = (int)(desiredHeight * imageRatio);

                }
            }else{
              if(frameRatio >= 1.0f){
                  desiredWidth = width;
                  desiredHeight = (int)(width / imageRatio);
              }else{
                  if(frameRatio > imageRatio){
                      desiredWidth = width;
                      desiredHeight = (int)(width / imageRatio);
                  }else if(frameRatio < imageRatio){
                      desiredHeight = height;
                      desiredWidth = (int)(desiredHeight * imageRatio);
                  }else{
                      // just use the orignal frame size to scale the image
                  }

              }
            }

            source = Bitmap.createScaledBitmap(source, desiredWidth, desiredHeight, true);
        }
        Bitmap sliceBitmap = Bitmap.createBitmap(source, (source.getWidth() - width)/2, (source.getHeight() - height)/2, width, height);




    }

    private int calculateSampleSizeFromFrame(int orignalWidth, int orignalHeight, int frameWidth, int frameHeight) {
        int sampleSize = 1;

        while (true) {
            int tempWidth = orignalWidth / sampleSize;
            int tempHeight = orignalHeight / sampleSize;

            if (tempWidth < frameWidth || tempHeight < frameHeight) {
                break;
            }

            sampleSize++;

        }

        return Math.max(sampleSize - 1, 1);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RowView, defStyle, 0);



        a.recycle();

        // Set up a default TextPaint object
       /* mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);*/

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
      /*  mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*// TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }


}
