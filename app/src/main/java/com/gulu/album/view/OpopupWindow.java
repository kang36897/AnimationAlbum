package com.gulu.album.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class OpopupWindow {
	/**
	 * Mode for {@link #setInputMethodMode(int)}: the requirements for the input method should be based on the focusability of the popup. That is if
	 * it is focusable than it needs to work with the input method, else it doesn't.
	 */
	public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;

	/**
	 * Mode for {@link #setInputMethodMode(int)}: this popup always needs to work with an input method, regardless of whether it is focusable. This
	 * means that it will always be displayed so that the user can also operate the input method while it is shown.
	 */
	public static final int INPUT_METHOD_NEEDED = 1;

	/**
	 * Mode for {@link #setInputMethodMode(int)}: this popup never needs to work with an input method, regardless of whether it is focusable. This
	 * means that it will always be displayed to use as much space on the screen as needed, regardless of whether this covers the input method.
	 */
	public static final int INPUT_METHOD_NOT_NEEDED = 2;

	private Context context;

	private WindowManager mWindowManager;
	private View mContentView;

	private boolean mIgnoreCheekPress;

	private boolean mFocusable;

	private int mInputMethodMode;

	private boolean mTouchable;

	private boolean mOutsideTouchable;

	private boolean mClippingEnabled;

	private boolean mSplitTouchEnabled;

	private boolean mLayoutInScreen;

	private Drawable mBackground;

	private int mLastWidth;

	private int mLastHeight;

	private int mWidth;

	private int mHeight;

	private int mWindowLayoutType;

	private int mSoftInputMode;

	private boolean isShowing;

	public OpopupWindow(Context ctx) {
		context = ctx;

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}

	public void setContentView(View content) {
		if (content == null) {
			return;
		}

		mContentView = content;
	}

	public void show(View ancher) {
		WindowManager.LayoutParams params = new LayoutParams();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;
		params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
		//@formatter:off
		params.flags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES 
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS 
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM 
				| WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
		params.flags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH ;
		params.x = 0;
		params.y = 0;
		//@formatter:on
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		params.token = ancher.getWindowToken();
		params.format = PixelFormat.TRANSLUCENT;
		params.packageName = context.getPackageName();
		params.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
		isShowing = true;
		mWindowManager.addView(mContentView, params);
	}

	public void dismiss() {
		if (!isShowing || mContentView == null) {
			return;
		}
		isShowing = false;
		mWindowManager.removeViewImmediate(mContentView);
	}

	public void rotate() {
		// mWindowManager.updateViewLayout(view, params);
	}

	private WindowManager.LayoutParams createPopupLayout(IBinder token) {
		// generates the layout parameters for the drop down
		// we want a fixed size view located at the bottom left of the anchor
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		// these gravity settings put the view at the top left corner of the
		// screen. The view is then positioned to the appropriate location
		// by setting the x and y offsets to match the anchor's bottom
		// left corner
		p.gravity = Gravity.LEFT | Gravity.TOP;
		p.width = mLastWidth = mWidth;
		p.height = mLastHeight = mHeight;
		if (mBackground != null) {
			p.format = mBackground.getOpacity();
		} else {
			p.format = PixelFormat.TRANSLUCENT;
		}
		p.flags = computeFlags(p.flags);
		p.type = mWindowLayoutType;
		p.token = token;
		p.softInputMode = mSoftInputMode;
		p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));

		return p;
	}

	private int computeFlags(int curFlags) {
		curFlags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
		if (mIgnoreCheekPress) {
			curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
		}
		if (!mFocusable) {
			curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			if (mInputMethodMode == INPUT_METHOD_NEEDED) {
				curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			}
		} else if (mInputMethodMode == INPUT_METHOD_NOT_NEEDED) {
			curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		}
		if (!mTouchable) {
			curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		}
		if (mOutsideTouchable) {
			curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		}
		if (!mClippingEnabled) {
			curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		}
		if (mSplitTouchEnabled) {
			curFlags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
		}
		if (mLayoutInScreen) {
			curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		}
		return curFlags;
	}

}
