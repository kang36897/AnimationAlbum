package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.gulu.album.R;

public class CanvasView extends View {

    private Shader mShader;

    private int sWidth;
    private int sHeight;

    private Paint paint;

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);


        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        ViewGroup.LayoutParams params = getLayoutParams();
        if(params == null){
            setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(),widthMeasureSpec),resolveSize(getSuggestedMinimumHeight(),heightMeasureSpec));
            return;
        }


        int widthSpec = resolveSize(Math.max(getSuggestedMinimumWidth(),params.width),widthMeasureSpec);
        int heightSpec = resolveSize(Math.max(getSuggestedMinimumHeight(),params.height),heightMeasureSpec);

        setMeasuredDimension(widthSpec,heightSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        sWidth = Math.max(getMeasuredWidth(), getSuggestedMinimumWidth());
        sHeight = Math.max(getMeasuredHeight(), getSuggestedMinimumHeight());


        //TODO delete the border width
        sWidth -= (getPaddingLeft() + getPaddingRight());
        sHeight -= (getPaddingTop() + getPaddingBottom());

        int desiredSize = Math.min(sWidth, sHeight);

        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.football_girl2, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;

        float ratio = width * 1.0f / height;

        Bitmap source;
        int tempSampleSize = calculateSampleSizeFromFrame(width, height, sWidth, sHeight);

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = tempSampleSize;
        opts.inScaled = true;

        source = BitmapFactory.decodeResource(getResources(), R.drawable.football_girl2, opts);

        Bitmap tBitmap = Bitmap.createBitmap(source.getWidth(),source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#8fffffff"), PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(source, 0, 0, paint);


        source = tBitmap;


        int shaderSize = (int) (desiredSize * 1.12f);

        // need to  scale the orignal bitmap to be bigger than the desiredSize

        int dstWidth;
        int dstHeight;

        if (ratio > 1.0f) {
            dstHeight = shaderSize;
            dstWidth = (int) ( ratio * dstHeight);

        } else if(ratio < 1.0f){
            dstWidth = shaderSize;
            dstHeight = (int) (dstWidth / ratio);
        }else{
            dstWidth = shaderSize;
            dstHeight = shaderSize;
        }


        source = Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true);


        /**
         * because the image being used by shader must big than the destnation area
         */
        // to create the subset immuterable bitmap from the source bitmap
        Bitmap subBitmap = Bitmap.createBitmap(source, 0, 0, (int) (desiredSize * 1.1f), (int) (desiredSize * 1.1f));

        mShader = new BitmapShader(subBitmap, TileMode.CLAMP, TileMode.CLAMP);


    }


    private int calculateSampleSizeFromFrame(int orignalWidth, int orignalHeight, int frameWidth, int frameHeight) {
        int sampleSize = 1;

        while (true) {
            int tempWidth = orignalWidth / sampleSize;
            int tempHeight = orignalHeight / sampleSize;

            if (tempWidth < frameWidth || tempHeight < frameHeight) {
                break;
            }

            sampleSize++;

        }

        return Math.max(sampleSize - 1, 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        paint.setShader(mShader);
        paint.setStyle(Style.FILL);

        float radius = Math.min(sWidth, sHeight) / 2;
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawOval(oval, paint);

        paint.setColor(Color.argb(255, 0, 255, 0));
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 3);
        paint.setStyle(Style.STROKE);

        paint.setShader(null);
        radius += getResources().getDisplayMetrics().density * 2;
        canvas.drawCircle(centerX, centerY, radius, paint);


    }

}
