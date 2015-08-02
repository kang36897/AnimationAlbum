package com.gulu.album;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SpinnerProgressActivity extends BaseActivity {

	private StateView mTargetView;
	private Drawable mSuccessDrawable;
	private Drawable alnertiveDrawable;
	private float strokeWidth = 5.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_different_spinner);

		strokeWidth = strokeWidth * getResources().getDisplayMetrics().density + 0.5f;
		mTargetView = (StateView) findViewById(R.id.stateView);

		RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radioButton1) {
					mTargetView.setDone(false);
				} else if (checkedId == R.id.radioButton3) {
					if (alnertiveDrawable != null)
						mTargetView.setResultDrawable(alnertiveDrawable);
					mTargetView.setDone(true);
				} else if (checkedId == R.id.radioButton2) {

					if (mSuccessDrawable != mTargetView.getResultDrawable()) {
						alnertiveDrawable = mTargetView.getResultDrawable();
					}

					if (mSuccessDrawable != null) {
						mTargetView.setDone(true);
						mTargetView.setResultDrawable(mSuccessDrawable);
						return;
					}
					mTargetView.setDone(false);
					createSuccessDrawable();
				}
			}
		});
	}

	private void createSuccessDrawable() {
		new Thread() {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

				int preferWidth = mTargetView.getMeasuredWidth() - mTargetView.getPaddingLeft() - mTargetView.getPaddingRight();
				int preferHight = mTargetView.getMeasuredHeight() - mTargetView.getPaddingTop() - mTargetView.getPaddingBottom();

				Bitmap bitmap = Bitmap.createBitmap(preferWidth, preferHight, Config.ARGB_8888);

				Canvas canvas = new Canvas(bitmap);

				float dx = preferWidth / 2;
				float dy = preferHight / 2;
				float radius = Math.min(dx, dy);

				Paint paint = new Paint();
				paint.setStyle(Style.FILL);
				paint.setAntiAlias(true);
				paint.setColor(Color.parseColor("#99cc00"));

				canvas.drawCircle(dx, dy, radius, paint);
				// canvas.save();
				// canvas.restore();

				paint.setColor(Color.WHITE);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(strokeWidth);

				radius = radius * 3 / 5;
				canvas.drawCircle(dx, dy, radius, paint);
				mSuccessDrawable = new BitmapDrawable(bitmap);
				mTargetView.getHandler().post(new Runnable() {

					@Override
					public void run() {
						mTargetView.setResultDrawable(mSuccessDrawable);
						mTargetView.setDone(true);
					}
				});

			}
		}.start();
	}

	public static class StateView extends View {
		final static int MAX_ROTATE_DEGREE = 360;
		private Animation mRotateAnimation;

		private Transformation mTransformation;

		private Drawable mSpinnerDrawable;
		private Drawable mSpinnerBackgroundDrawable;
		private Drawable mDrawable;

		private boolean isDone = false;

		public StateView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public boolean isDone() {
			return isDone;
		}

		public void setDone(boolean done) {
			if (this.isDone == done) {
				return;
			}

			if (done == true) {
				mRotateAnimation.cancel();
			} else {
				mRotateAnimation.startNow();
				postInvalidate();
			}

			this.isDone = done;
		}

		public StateView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);

			mRotateAnimation = new RotateAnimation(0, MAX_ROTATE_DEGREE, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF,
					0.5f);
			mRotateAnimation.setInterpolator(new LinearInterpolator());
			mRotateAnimation.setRepeatCount(Animation.INFINITE);
			mRotateAnimation.setDuration(800);
			mRotateAnimation.startNow();

			mTransformation = new Transformation();

			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.stateView);
			for (int i = 0; i < a.getIndexCount(); i++) {
				int attr = a.getIndex(i);

				switch (attr) {
				case R.styleable.stateView_spinner_drawable:
					mSpinnerDrawable = a.getDrawable(attr);
					break;

				case R.styleable.stateView_spinner_background_drawable:
					mSpinnerBackgroundDrawable = a.getDrawable(attr);
					break;

				case R.styleable.stateView_result_drawable:
					mDrawable = a.getDrawable(attr);
					break;
				}

			}

			a.recycle();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			int paddingLeft = getPaddingLeft();
			int paddingTop = getPaddingTop();
			Rect bounds = new Rect(0, 0, getMeasuredWidth() - paddingLeft - getPaddingRight(), getMeasuredHeight() - paddingTop - getPaddingBottom());
			if (isDone) {

				if (mDrawable == null)
					return;
				canvas.save();
				canvas.translate(paddingLeft, paddingTop);
				mDrawable.setBounds(bounds);
				mDrawable.draw(canvas);

				canvas.restore();
				return;
			}

			mRotateAnimation.getTransformation(AnimationUtils.currentAnimationTimeMillis(), mTransformation);

			// draw spinner background
			if (mSpinnerBackgroundDrawable != null) {

				canvas.save();
				canvas.translate(paddingLeft, paddingTop);
				mSpinnerBackgroundDrawable.setBounds(bounds);
				mSpinnerBackgroundDrawable.draw(canvas);

				canvas.restore();
			}

			// draw spinner itself
			if (mSpinnerDrawable == null) {
				return;
			}

			canvas.save();

			canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
			canvas.concat(mTransformation.getMatrix());
			canvas.translate(-getMeasuredWidth() / 2, -getMeasuredHeight() / 2);
			canvas.translate(paddingLeft, paddingTop);

			mSpinnerDrawable.setBounds(bounds);
			mSpinnerDrawable.draw(canvas);

			canvas.restore();

			invalidate();
		}

		public Drawable getResultDrawable() {
			return mDrawable;
		}

		public void setResultDrawable(Drawable mDrawable) {
			if (this.mDrawable == mDrawable) {
				return;
			}

			unscheduleDrawable(mDrawable);
			this.mDrawable = mDrawable;
			invalidate();
		}

	}
}
