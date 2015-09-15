package com.gulu.album.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
    private RadialGradient mSelectedShader;
    private Paint mPaint;
    private int mPagerCount;
    private int mCurrentPage;
    private int mSpacing;
    private int mRadius;

    private ValueAnimator mAnimator;
    private int mPreviousValue;
    private int mCurrentValue;
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

    public void setCurrentPage(int position) {
        if (mCurrentPage == position) {
            return;
        }

        mCurrentPage = position;
        mCurrentValue = mRadius + mCurrentPage * mOffset;
        mPreviousValue = mCurrentValue;
        invalidate();

    }


    public void setCurrentPageByAnimation(final int position) {
        if (mCurrentPage == position) {
            return;
        }

        mOffset = Math.abs(mOffset);
        if (position < mCurrentPage) {
            mOffset = mOffset * -1;
        }

        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
            mAnimator = null;
        }

        mAnimator = ValueAnimator.ofInt(0, mOffset).setDuration(200);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (Integer) animation.getAnimatedValue();
                mCurrentValue = mPreviousValue + offset;
                invalidate();
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPreviousValue = mCurrentValue = mPreviousValue + mOffset;
                mCurrentPage = position;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator.start();
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
        mNomralShader = new RadialGradient(mRadius, mRadius, mRadius,Color.TRANSPARENT, NORMAL_COLOR,  Shader.TileMode.CLAMP);
        mSelectedShader = new RadialGradient(mRadius, mRadius, mRadius, new int[]{SELECTED_COLOR, Color.TRANSPARENT, SELECTED_COLOR}, new float[]{0.6f, 0.8f,1.0f}, Shader.TileMode.CLAMP);
        mOffset = mRadius * 2 + mSpacing;
        mCurrentValue = mPreviousValue = mRadius + mOffset * mCurrentPage;

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

//        int innerWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
//        int innerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        //mPaint.setColor(NORMAL_COLOR);
        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop());

        mPaint.setShader(mNomralShader);
        for (int i = 0; i < mPagerCount; i++) {
            canvas.save();
            canvas.translate((2 * mRadius + mSpacing) * i, 0);
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
            canvas.restore();
        }

        // draw the selected one
         mPaint.setShader(mSelectedShader);
        canvas.save();
        canvas.translate(mCurrentValue - mRadius, 0);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        canvas.restore();

        canvas.restore();
    }
}
