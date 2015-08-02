package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.gulu.album.R;
import com.gulu.album.utils.MDataHelper;

public class CustomerMapView extends View {
	
	static final float DEFAULT_MOVE_STRIDE = 15.0f;
	
	final static String DEBUG_TAG = "customer_map_view";
	
	final static int DEFAULT_SLOT = 20;
	final static int DEFAULT_VIEW_PORT_WIDTH = 400;
	
	private float mFlingStride;
	
	private float[] position;
	private String mTips = "KHM";




	private ViewConfiguration mVConfiguration;
	private Paint mPaint;
	
	private DisplayMetrics mDisplayMetrics;
	
	// the bitmap's viewport
	private Rect src;
	// the view's canvas
	private Rect dst;
	
	private BitmapFactory.Options mOptions;
	private boolean isMatrixed = false;
	private Matrix iMatrix;
	
	private Bitmap mSMap;
	// the original size of bitmap
	private int ow;
	private int oh;
	// after scaled to the canvas
	private int dw;
	private int dh;
	
	private int mViewWidth;
	private int mViewHeight;
	private float factor;
	
	private MDataHelper mDHelper;
	
	private int mZoomParam;
	private int mRequestWidth;
	private int mRequestHeight;
	private int mViewPortStride;
	
	private DecelerateInterpolator interpolator = new DecelerateInterpolator();
	private GestureDetector mNormalGestureDetector;
	private SimpleOnGestureListener mNGListener;
	private ScaleGestureDetector mScaleGestureDetector;
	private SimpleOnScaleGestureListener mSGListener;
	
	private HandlerThread mWorkerThread;
	private Handler mInternalHandler;
	
	public CustomerMapView(Context context) {
		super(context);
		initPaintStuff();
	}
	
	public CustomerMapView(Context context, AttributeSet attrSet) {
		this(context, attrSet, 0);
	}
	
	public CustomerMapView(Context context, AttributeSet attrSet,
			int defaultStyle) {
		super(context, attrSet, defaultStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrSet,
				R.styleable.CustomMapView);
		try {
			
			mZoomParam = a.getInt(R.styleable.CustomMapView_zoom, 3);
			mRequestWidth = a.getDimensionPixelSize(
					R.styleable.CustomMapView_bitmapWidth, 800);
			mRequestHeight = a.getDimensionPixelSize(
					R.styleable.CustomMapView_bitmapHeight, 800);
			mViewPortStride = a.getDimensionPixelSize(
					R.styleable.CustomMapView_viewPortStride,
					DEFAULT_VIEW_PORT_WIDTH);
		} finally {
			a.recycle();
		}
		
		initPaintStuff();
		
	}
	
	private void initPaintStuff() {

		setFocusable(true);
		
		mDHelper = new MDataHelper(getContext());
		position = new float[2];
		mVConfiguration = ViewConfiguration.get(getContext());
		mDisplayMetrics = getResources().getDisplayMetrics();
		
		mOptions = new Options();
		mOptions.inScaled = false;
		iMatrix = new Matrix();
		
		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#ff6347"));
		mPaint.setTextSize(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PX, 20, getResources()
						.getDisplayMetrics()));
		
		mFlingStride = 500 * mDisplayMetrics.density + 0.5f;
		
		initNormalGestureDetector();
		
		initScaledGestureDetector();
		
		mWorkerThread = new HandlerThread("dirty-worker-thread");
		mWorkerThread.start();
		
		mInternalHandler = new Handler(mWorkerThread.getLooper());
	}
	
	protected void initScaledGestureDetector() {
		mSGListener = new SimpleOnScaleGestureListener() {
			
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				// TODO Auto-generated method stub
				return super.onScaleBegin(detector);
			}
			
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				
				float scaleFactor = 0.0f;
				scaleFactor = detector.getPreviousSpan()
						/ detector.getCurrentSpan();
				
				int nw = 0;
				int nh = 0;
				
				if (factor < 1) {
					nh = (int) (src.height() * scaleFactor);
					
					if (nh > dh) {
						nh = dh;
					}
					
					nw = (int) (nh * factor);
					
				} else {
					
					nw = (int) (src.width() * scaleFactor);
					
					if (nw > dw) {
						nw = dw;
					}
					
					nh = (int) (nw / factor);
				}
				
				int x = src.centerX() - nw / 2;
				if (nw == dw) {
					x = dw / 2;
				}
				
				int y = src.centerY() - nh / 2;
				if (nh == dh) {
					y = dh / 2;
				}
				
				src.left = x;
				src.top = y;
				src.right = x + nw;
				src.bottom = y + nh;
				
				adjustPosition();
				invalidate();
				
				return true;
			}
			
		};
		mScaleGestureDetector = new ScaleGestureDetector(getContext(),
				mSGListener);
	}
	
	protected void initNormalGestureDetector() {
		mNGListener = new SimpleOnGestureListener() {
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				float xDistance = e2.getX() - e1.getX();
				float yDistance = e2.getY() - e1.getY();
				
				// Log.d(DEBUG_TAG, "onFling-->velocityX=" + velocityX
				// + ",velocityY=" + velocityY + ",xDistance ="
				// + xDistance + ",yDistance =" + yDistance);
				
				float tempVelocityX = Math.abs(velocityX);
				float tempVelocityY = Math.abs(velocityY);
				if (tempVelocityX < mVConfiguration
						.getScaledMinimumFlingVelocity()
						&& tempVelocityY < mVConfiguration
								.getScaledMinimumFlingVelocity()) {
					return false;
				}
				
				float absXDistance = Math.abs(xDistance);
				float absYDistance = Math.abs(yDistance);
				if (absXDistance > 450.0f || absYDistance > 450.0f) {
					return false;
				}
				
				final long start = SystemClock.uptimeMillis();
				final long end = start + 300;
				
				if (absXDistance > absYDistance) {
					if (absYDistance != 0) {
						absYDistance = mFlingStride
								* ((float) absYDistance / absXDistance);
					}
					absXDistance = mFlingStride;
				} else {
					if (absXDistance != 0) {
						absXDistance = mFlingStride
								* ((float) absXDistance / absYDistance);
						
					}
					absYDistance = mFlingStride;
				}
				
				final float xFlingDistance = absXDistance
						* (xDistance >= 0 ? 1 : -1);
				final float yFingDistance = absYDistance
						* (yDistance >= 0 ? 1 : -1);
				
				getHandler().post(new Runnable() {
					
					@Override
					public void run() {
						if (SystemClock.uptimeMillis() > end) {
							return;
						}
						
						float input = (float) (SystemClock.uptimeMillis() - start) / 300;
						
						float xOffset = xFlingDistance
								* interpolator.getInterpolation(input);
						float yOffset = yFingDistance
								* interpolator.getInterpolation(input);
						
						moveViewport(xOffset * -1, yOffset * -1);
						getHandler().postDelayed(this, 4);
					}
					
				});
				
				return true;
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				
				// Log.d(DEBUG_TAG, "onScroll-->distanceX =" + distanceX
				// + ",distanceY =" + distanceY);
				
				moveViewport(distanceX, distanceY);
				return true;
			}
		};
		mNormalGestureDetector = new GestureDetector(getContext(), mNGListener,
				getHandler(), true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mSMap == null) {
			return;
		}
		
		// create imatrix failed
		if (!isMatrixed) {
			// canvas.drawColor(Color.GRAY);
			return;
		}
		
		canvas.drawBitmap(mSMap, src, dst, null);
		
	}
	
	protected void afterLoadedBitMap() {
		if (mSMap == null) {
			return;
		}
		
		ow = mSMap.getWidth();
		oh = mSMap.getHeight();
		dw = mSMap.getScaledWidth(mDisplayMetrics.densityDpi);
		dh = mSMap.getScaledHeight(mDisplayMetrics.densityDpi);
		Log.d(DEBUG_TAG, "width:" + ow + ",height:" + oh + ",dw:" + dw + ",dh:"
				+ dh);
		RectF src = new RectF(0, 0, ow, oh);
		RectF dst = new RectF(0, 0, dw, dh);
		isMatrixed = iMatrix.setRectToRect(src, dst, ScaleToFit.FILL);
		Log.d(DEBUG_TAG, "isMatrixCreatedSuccessfully = " + isMatrixed);
		
	}
	
	private Runnable mUpdateMapTask = new Runnable() {
		
		@Override
		public void run() {
			
			try {
				mDHelper.clear();
				String requestUrl = mDHelper.buildRequestUrl(mZoomParam,
						121.470337, 31.224839, mRequestWidth, mRequestHeight);
				final Bitmap target = mDHelper.extractBitmap(requestUrl);
				// FIXME do some calculations
				Bitmap temp = mSMap;
				
				mSMap = target;
				if (temp != null && !temp.isRecycled()) {
					temp.recycle();
				}
				afterLoadedBitMap();
				src.offset((dw - src.width()) / 2, (dh - src.height()) / 2);
				postInvalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	};
	
	public void updateMap() {
		mInternalHandler.removeCallbacks(mUpdateMapTask);
		mInternalHandler.post(mUpdateMapTask);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		mInternalHandler.removeCallbacks(mUpdateMapTask);
		mInternalHandler.sendMessage(Message.obtain());

	}
	
	@Override
	public boolean isInEditMode() {
		return true;
	}
	

	
	public Bitmap buildBitmap(Bitmap source) {
		Bitmap temp = source;
		if (!temp.isMutable()) {
			temp = source.copy(Config.ARGB_8888, true);
		}
		
		Canvas canvas = new Canvas(temp);
		
		position[0] = canvas.getWidth() / 2;
		position[1] = canvas.getHeight() / 2;
		
		final int saveCount = canvas.save();
		
		float[] origin = new float[2];
		computeOriginOfText(position, origin, true);
		canvas.drawText(mTips, origin[0], origin[1], mPaint);
		
		canvas.restoreToCount(saveCount);
		return temp;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mViewWidth = w;
		mViewHeight = h;
		Log.d(DEBUG_TAG, "onSizeChanged()--> w = " + w + ",h=" + h + ",oldw ="
				+ oldw + ",oldh=" + oldh);
		factor = (float) mViewWidth / mViewHeight;
		Log.d(DEBUG_TAG, "onSizeChanged()--> scale = " + factor);
		src = new Rect(0, 0, mViewPortStride, (int) (mViewPortStride / factor));
		
		Log.d(DEBUG_TAG, "onSizeChanged()--> src = " + src);
		dst = new Rect(0, 0, mViewWidth, mViewHeight);
		
	}
	
	public void computeOriginOfText(float[] position, float[] origin,
			boolean isCenter) {
		
		Rect bounds = new Rect();
		mPaint.getTextBounds(mTips, 0, mTips.length(), bounds);
		
		Matrix matrix = new Matrix();
		if (isCenter) {
			matrix.setTranslate(position[0] - bounds.centerX(), position[1]
					- bounds.centerY());
		} else {
			int left = bounds.left;
			int top = bounds.top;
			matrix.setTranslate(position[0] - left, position[1] - top);
		}
		
		matrix.mapPoints(origin, new float[] {
				0, 0
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		boolean handled = false;
		
		handled = handled | mNormalGestureDetector.onTouchEvent(event)
				| mScaleGestureDetector.onTouchEvent(event);
		
		return handled;
		
	}
	
	protected void moveViewport(float xOffset, float yOffset) {
		if (xOffset > 0) {
			xOffset = Math.min(xOffset, DEFAULT_MOVE_STRIDE);
		} else {
			xOffset = Math.max(xOffset, -DEFAULT_MOVE_STRIDE);
		}
		
		if (yOffset > 0) {
			yOffset = Math.min(yOffset, DEFAULT_MOVE_STRIDE);
		} else {
			yOffset = Math.max(yOffset, -DEFAULT_MOVE_STRIDE);
		}
		
		if (src.width() == dw) {
			xOffset = 0;
		}
		
		if (src.height() == dh) {
			yOffset = 0;
		}
		
		// mMoveMatrix.setTranslate(xOffset, yOffset);
		src.offset((int) xOffset, (int) yOffset);
		
		adjustPosition();
		
		invalidate();
	}
	
	protected void adjustPosition() {
		int xAdjust = 0;
		int yAdjust = 0;
		if (src.left < 0) {
			xAdjust = 0 - src.left;
		}
		if (src.top < 0) {
			yAdjust = 0 - src.top;
		}
		
		if (src.right > dw) {
			xAdjust = dw - src.right;
		}
		
		if (src.bottom > dh) {
			yAdjust = dh - src.bottom;
		}
		
		src.offset(xAdjust, yAdjust);
	}
}
