package com.gulu.album.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.TextView;

import com.gulu.album.R;

public class IconMaker {

	private static IconMaker self;

	private Context mContext;
	private LayoutInflater mInflater;
	private View mTemplateView;

	private Bitmap[] mIcons;
	private Paint mPaint;
	private float textSize;

	private IconMaker(Context context) {
		mContext = context.getApplicationContext();
		mInflater = LayoutInflater.from(mContext);

		mTemplateView = mInflater.inflate(R.layout.car_and_platenum, null);
		PlateNumHolder holder = new PlateNumHolder();
		mTemplateView.setTag(holder);

		holder.plateNum = (TextView) mTemplateView.findViewById(R.id.car_platenum);
		holder.carIcon = (ImageView) mTemplateView.findViewById(R.id.car_stateicon);

		Resources resources = context.getResources();
		//@formatter:off
		mIcons = new Bitmap[]{
				BitmapFactory.decodeResource(resources, R.drawable.car_icon_default),
				BitmapFactory.decodeResource(resources, R.drawable.car_icon_running),
				BitmapFactory.decodeResource(resources, R.drawable.car_icon_flameout),
				BitmapFactory.decodeResource(resources, R.drawable.car_icon_offline),
				};
		//@formatter:on

		textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, resources.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(textSize);
	}

	public synchronized static IconMaker getInstance(Context context) {
		if (self != null) {
			return self;
		}

		self = new IconMaker(context);
		return self;
	}

	public Bitmap generateIcon(final String vehicleNumber, final int vehicleState) {
		// TODO do some image cache
		synchronized (mTemplateView) {
			PlateNumHolder holder = (PlateNumHolder) mTemplateView.getTag();
			holder.plateNum.setText(vehicleNumber);
			holder.carIcon.setImageLevel(vehicleState);

			mTemplateView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			mTemplateView.layout(0, 0, mTemplateView.getMeasuredWidth(), mTemplateView.getMeasuredHeight());

			final Bitmap target = Bitmap.createBitmap(mTemplateView.getMeasuredWidth(), mTemplateView.getMeasuredHeight(), Config.ARGB_8888);
			final Canvas canvas = new Canvas(target);
			mTemplateView.draw(canvas);

			return target;
		}

	}

	public static class PlateNumHolder {
		public ImageView carIcon;
		public TextView plateNum;
	}
}
