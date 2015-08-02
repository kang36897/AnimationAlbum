package com.gulu.album.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.gulu.album.R;

public class MirrorView extends View {

	private BitmapDrawable mSourceDrawable;
	private Bitmap mSourceBM;

	public MirrorView(Context context) {
		super(context);
		mSourceBM = BitmapFactory.decodeResource(context.getResources(), R.drawable.football_girl);
		mSourceDrawable = new BitmapDrawable(context.getResources(), mSourceBM);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		// Rect src = new Rect(0, 0, width, (int) (height * 0.75f));
		// canvas.drawBitmap(mSourceBM, src, mSourceDrawable.getBounds(), null);
		canvas.drawBitmap(getMirrorBitmap(mSourceBM), 0, 0, null);

	}

	Bitmap getMirrorBitmap(Bitmap src) {
		final int width = src.getWidth();
		final int height = src.getHeight();
		Matrix m = new Matrix();
		m.setScale(-1f, 1f, width * 0.5f, height * 0.5f);
		Bitmap temp = Bitmap.createBitmap(src, 0, 0, width, height, m, true);
		temp = Bitmap.createScaledBitmap(temp, width * 3, height * 3, false);
		temp = Bitmap.createScaledBitmap(temp, width, height, false);

		Bitmap target = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		canvas.drawBitmap(temp, 0, 0, null);

		return target;
	}
}
