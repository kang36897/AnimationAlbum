package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.gulu.album.R;

/**
 * Created by lulala on 12/9/15.
 */
public class PagerIndicator extends View {

    private final static int NORMAL_COLOR = Color.parseColor("#efffffff");
    private final static int SELECTED_COLOR = Color.parseColor("#ff00ff00");

    private RadialGradient mNomralShader;
    private Paint mPaint;
    private int mPagerCount;
    private int mCurrentPage;
    private int mSpacing;
    private int mRadius;
    private int mOffset;

    public PagerIndicator(Context context) {
        super(context);
        initialization();
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        extractAttribute(context, attrs);

        initialization();
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttribute(context, attrs);
        initialization();
    }

    public void setCurrentPage(int position){
        if(mCurrentPage == position)
        {
            return;
        }

        mCurrentPage = position;
        invalidate();

    }


    public void setCurrentPageByAnimation(int position)
    {
        if(mCurrentPage == position)
        {
            return;
        }

        mOffset =  mRadius * 2 * mCurrentPage + mSpacing * mCurrentPage;
        if(position < mCurrentPage)
        {
            mOffset  = mOffset * -1;
        }

    }

    private void extractAttribute(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pagerIndicator);
        mPagerCount = ta.getInt(R.styleable.pagerIndicator_pi_pagerCount, 1);
        mCurrentPage = ta.getInt(R.styleable.pagerIndicator_pi_currentPage, 0);
        mSpacing = (int) ta.getDimension(R.styleable.pagerIndicator_pi_space, 0);
        mRadius = (int) ta.getDimension(R.styleable.pagerIndicator_pi_radius, context.getResources().getDisplayMetrics().density * 8);
        ta.recycle();
    }

    private void initialization() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mNomralShader = new RadialGradient(mRadius, mRadius, mRadius, Color.TRANSPARENT, NORMAL_COLOR, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int needWidth = mRadius * 2 * mPagerCount + mSpacing * (mPagerCount - 1) + getPaddingLeft() + getPaddingRight();
        int needHeight = mRadius * 2 + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(needWidth, widthMeasureSpec), resolveSize(needHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int innerWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int innerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        //mPaint.setColor(NORMAL_COLOR);

        mPaint.setShader(mNomralShader);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        for (int i = 0; i < mPagerCount; i++) {
            canvas.drawCircle(mRadius + i * 2 * mRadius + mSpacing * i, mRadius, mRadius, mPaint);
        }

        canvas.restore();

        mPaint.setShader(null);
        mPaint.setColor(SELECTED_COLOR);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawCircle(mRadius + mRadius * 2 * mCurrentPage + mSpacing * mCurrentPage, mRadius, mRadius, mPaint);

        canvas.restore();
    }
}
