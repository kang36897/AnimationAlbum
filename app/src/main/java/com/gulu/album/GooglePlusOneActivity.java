package com.gulu.album;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class GooglePlusOneActivity extends Activity {
	
	public static final int ID_LOAD_IMAGE_SUCCESS = 0;
	
	private static final AccelerateInterpolator mAccelerate = new AccelerateInterpolator();
	
	private static final DecelerateInterpolator mDecelerate = new DecelerateInterpolator();
	
	private static final AccelerateDecelerateInterpolator mAccelerateBeforeDecelerate = new AccelerateDecelerateInterpolator();
	
	private View mPager;
	
	private View mApplaudView;
	
	private ImageView mImageView;
	
	private Handler localHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_plus_one);
		initHandler();
		
		setActivityView();
		
	}
	
	private void initHandler() {
		localHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ID_LOAD_IMAGE_SUCCESS:
					mImageView.setImageBitmap((Bitmap) msg.obj);
					break;
				
				}
			}
		};
		
	}
	
	private void setActivityView() {
		mPager = findViewById(R.id.pager);
		
		mApplaudView = findViewById(R.id.applaud);
		mApplaudView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView tempView = ((TextView) v);
				tempView.setTextColor(Color.WHITE);
				tempView.setBackgroundResource(R.drawable.red_rounded_red_bg);
				
				Animator throwUpAndDownAnimator = AnimatorInflater
						.loadAnimator(getBaseContext(),
								R.animator.thrown_up_and_down);
				
				throwUpAndDownAnimator.setTarget(v);
				throwUpAndDownAnimator
						.addListener(new AnimatorListenerAdapter() {
							
							@Override
							public void onAnimationEnd(Animator animation) {
								
								// AnimatorSet shakingAnimator = (AnimatorSet)
								// AnimatorInflater
								// .loadAnimator(getBaseContext(),
								// R.animator.shaking);
								// shakingAnimator.setTarget(mPager);
								// shakingAnimator.start();
								// from the normal point to the lowest point
								
								AnimatorSet firstTerm = new AnimatorSet();
								ObjectAnimator downAnimator = ObjectAnimator
										.ofFloat(mPager, "rotationX", 0, -2)
										.setDuration(50);
								downAnimator.setInterpolator(mDecelerate);
								
								AnimatorSet.Builder builder = firstTerm
										.play(downAnimator);
								
								/*
								 * downAnimator = ObjectAnimator
								 * .ofFloat(mPager, "rotationY", 0, -4)
								 * .setDuration(50);
								 * downAnimator.setInterpolator(mDecelerate);
								 * builder.with(downAnimator);
								 */
								
								// from the lowest point to the highest point
								AnimatorSet secondTerm = new AnimatorSet();
								ObjectAnimator upAnimator = ObjectAnimator
										.ofFloat(mPager, "rotationX", -2, 1)
										.setDuration(150);
								upAnimator
										.setInterpolator(mAccelerateBeforeDecelerate);
								
								builder = secondTerm.play(upAnimator);
								
								/*
								 * upAnimator = ObjectAnimator .ofFloat(mPager,
								 * "rotationY", -4, 2) .setDuration(150);
								 * upAnimator
								 * .setInterpolator(mAccelerateBeforeDecelerate
								 * ); builder.with(upAnimator);
								 */
								
								builder.after(firstTerm);
								
								// from the highest point to the normal point
								AnimatorSet thirdTerm = new AnimatorSet();
								downAnimator = ObjectAnimator.ofFloat(mPager,
										"rotationX", 1, 0).setDuration(50);
								downAnimator.setInterpolator(mDecelerate);
								
								builder = thirdTerm.play(downAnimator);
								
								/*
								 * downAnimator = ObjectAnimator.ofFloat(mPager,
								 * "rotationY", 2, 0).setDuration(50);
								 * downAnimator.setInterpolator(mDecelerate);
								 * builder.with(downAnimator);
								 */
								
								builder.after(secondTerm);
								
								mPager.setPivotX(mPager.getLeft()
										+ mPager.getWidth());
								mPager.setPivotY(mPager.getTop());
								Log.d("mPager",
										"mPager.getPivotX() = "
												+ mPager.getPivotX()
												+ ",mPager.getPivotY() = "
												+ mPager.getPivotY());
								thirdTerm.start();
							}
						});
				
				throwUpAndDownAnimator.start();
			}
		});
		
		mImageView = (ImageView) findViewById(R.id.temp_img);
		new ScaledImageCreator(localHandler, getResources(),
				R.drawable.androidicons).start();
		// loadBitmapForImageView(mImageView, R.drawable.androidicons);
	}
	
	static class ScaledImageCreator extends Thread {
		
		private WeakReference<Handler> hanlderReference;
		
		private Resources res;
		private int resId;
		
		public ScaledImageCreator(Handler handler, Resources resources,
				int resId) {
			super("ScaledImageCreator");
			this.res = resources;
			this.resId = resId;
			hanlderReference = new WeakReference<Handler>(handler);
		}
		
		@Override
		public void run() {
			Bitmap scaledBitmap = createScaledBitmapForImage(res, resId);
			
			Handler localHandler = hanlderReference.get();
			if (localHandler == null)
				return;
			
			localHandler.obtainMessage(ID_LOAD_IMAGE_SUCCESS, scaledBitmap)
					.sendToTarget();
		}
	}
	
	void loadBitmapForImageView(ImageView target, int resId) {
		
		final BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeResource(getResources(), resId, options);
		
		options.inSampleSize = computeSampleSize(options, 400, 300);
		
		options.inJustDecodeBounds = false;
		
		target.setImageBitmap(BitmapFactory.decodeResource(getResources(),
				resId, options));
	}
	
	static Bitmap createScaledBitmapForImage(Resources res, int resId) {
		final BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeResource(res, resId, options);
		
		options.inSampleSize = computeSampleSize(options, 400, 300);
		
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static int computeSampleSize(Options opts, int reqWidth,
			int reqHeight) {
		final int width = opts.outWidth;
		final int height = opts.outHeight;
		
		int sampleSize = 1;
		
		if (reqWidth < width || reqHeight < height) {
			
			final int halfWidth = width / 2;
			final int halfHeight = height / 2;
			
			while (halfWidth / sampleSize > reqWidth
					&& halfHeight / sampleSize > reqWidth) {
				sampleSize *= 2;
			}
			
		}
		
		return sampleSize;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_plus_one, menu);
		return true;
	}
	
}
