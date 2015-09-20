package com.gulu.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lulala on 20/9/15.
 */
public class RadialGradientView extends View {

    public final static int NORMAL_RADIAL_GRADIENT = 0;
    public final static int POSITION_RADIAL_GRADIENT = 1;
    private int mCurrentMode = NORMAL_RADIAL_GRADIENT;


    private RadialGradient mRadialGradient;
    private Paint mPaint;

    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    private float Max_Radius;

    private float mShaderRadius;
    private int mCenterColor;
    private int mEdgeColor;


    private DashPathEffect mDashEffect;


    public RadialGradientView(Context context) {
        super(context);
        initialization(context);
    }

    public RadialGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public RadialGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }


    private void initialization(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mDashEffect = new DashPathEffect(new float[]{10, 5, 4}, 2);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        mCenterX = getMeasuredWidth() * 0.5f;
        mCenterY = getMeasuredHeight() * 0.5f;
        Max_Radius = mRadius = Math.min(w, h) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mRadialGradient);

        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

        mPaint.setShader(null);
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(mDashEffect);

        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(mCenterX, mCenterY, mShaderRadius, mPaint);

        mPaint.setColor(Color.YELLOW);

        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }

    private void updateShader(float shaderRatio, float radiusRatio, int centerColor, int edgeColor) {


        mShaderRadius = Max_Radius * shaderRatio;
        mCenterColor = centerColor;
        mEdgeColor = edgeColor;


        mRadialGradient = new RadialGradient(mCenterX, mCenterY, mShaderRadius, mCenterColor, mEdgeColor, Shader.TileMode.CLAMP);


        mRadius = Max_Radius * radiusRatio;
        invalidate();
    }

    public void updateShader(float shaderRatio, float radiusRatio, int[] colors, float[] stops) {
        if(mCurrentMode == NORMAL_RADIAL_GRADIENT)
        {
            updateShader(shaderRatio, radiusRatio,colors[0], colors[1]);
            return;
        }


        mShaderRadius = Max_Radius * shaderRatio;
        mRadius = Max_Radius * radiusRatio;

        mRadialGradient = new RadialGradient(mCenterX, mCenterY, mShaderRadius, colors, stops, Shader.TileMode.CLAMP);

        invalidate();
    }

    public void switchMode(int mode) {
        if (mCurrentMode == mode) {
            return;
        }
        mCurrentMode = mode;


    }

    public int getMode()
    {
        return mCurrentMode;
    }

}
