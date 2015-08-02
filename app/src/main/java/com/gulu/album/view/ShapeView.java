package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.gulu.album.R;

public class ShapeView extends View {

	private Paint mPaint;

	private float leftDegree = 90 + 8;

	private int mShapeFillColor;
	private String text;
	private float textSize;
	private int textColor;

	private Path mPath;

	public ShapeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShapeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.shapeView);

		mShapeFillColor = a.getColor(R.styleable.shapeView_filledColor, Color.parseColor("#ffff00"));
		text = a.getString(R.styleable.shapeView_text);
		textSize = a.getDimension(R.styleable.shapeView_textSize, getScaledSize(17.0f));
		textColor = a.getColor(R.styleable.shapeView_textColor, Color.BLACK);
		a.recycle();

		LayoutParams params = new LayoutParams(getContext(), attrs);
		setLayoutParams(params);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPath = new Path();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		LayoutParams params = getLayoutParams();

		setMeasuredDimension(MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY));

	}

	protected float getDevicedSize(float size) {
		return size * getResources().getDisplayMetrics().density + 0.5f;
	}

	protected float getScaledSize(float size) {
		return size * getResources().getDisplayMetrics().scaledDensity + 0.5f;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);

		mPaint.setStyle(Style.FILL);
		mPaint.setColor(mShapeFillColor);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		float xCenter = width / 2;
		float yCenter = height * 0.3f;
		float radius = yCenter * 0.65f;

		float xOffset = (float) Math.abs(radius * Math.cos(leftDegree));
		float yOffset = (float) Math.abs(radius * Math.sin(leftDegree));

		// start point is (x0,y0)
		float x0 = xCenter - xOffset;
		float y0 = yCenter + yOffset;

		mPath.reset();
		mPath.moveTo(x0, y0);

		// control point is (x1,y1)
		float x1 = xCenter - radius * 0.25f;
		float y1 = yCenter + radius * 1.2f;

		// end point is (x2,y2)
		float x2 = xCenter;
		float y2 = yCenter + radius * 1.92f;

		mPath.quadTo(x1, y1, x2, y2);

		// start point is (x2,y2)

		// control point is (x3,y3)
		float x3 = xCenter + radius * 0.25f;
		float y3 = yCenter + radius * 1.2f;

		// end point is (x4,y4);

		float x4 = xCenter + xOffset;
		float y4 = yCenter + yOffset;
		mPath.quadTo(x3, y3, x4, y4);

		canvas.drawPath(mPath, mPaint);
		canvas.drawCircle(xCenter, yCenter, radius, mPaint);

		// draw the number
		Rect bounds = new Rect();
		mPaint.setColor(textColor);
		mPaint.setTextSize(textSize);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.getTextBounds(text, 0, text.length(), bounds);

		// text's origin
		float dx = xCenter - bounds.centerX();
		float dy = yCenter - bounds.centerY();
		canvas.save();
		canvas.translate(dx, dy);
		canvas.drawText(text, 0, 0, mPaint);
		canvas.restore();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mPaint = null;
		mPath = null;
	}

}
