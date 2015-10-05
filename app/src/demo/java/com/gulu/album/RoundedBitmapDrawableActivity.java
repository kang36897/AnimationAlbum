package com.gulu.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.gulu.album.graphics.SBitmapDrawable;


/**
 * Created by lulala on 4/10/15.
 */
public class RoundedBitmapDrawableActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rounded_bitmap_drawable);


        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.football_girl);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), sourceBitmap);
        roundedBitmapDrawable.setCornerRadius(getResources().getDimension(R.dimen.default_rouned_corner_radius));
        //roundedBitmapDrawable.setGravity(Gravity.TOP|Gravity.CLIP_VERTICAL);
        roundedBitmapDrawable.setCircular(true);

        View mNormalView = findViewById(R.id.normal_view);
        mNormalView.setBackground(roundedBitmapDrawable);

        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), sourceBitmap);
        //roundedBitmapDrawable.setGravity(Gravity.TOP);

        roundedBitmapDrawable.setCircular(true);

        ImageView mNormalImageView = (ImageView) findViewById(R.id.normal_image_view);
        mNormalImageView.setImageDrawable(roundedBitmapDrawable);


        SBitmapDrawable.DrawOperationWithShader operationWithShader = new SBitmapDrawable.DrawOperationWithShader() {
            @Override
            public void doDrawOperation(Canvas canvas, Paint shaderPaint, int dw, int dh,
                                        Rect dstRect, int desity, int gravity,
                                        SBitmapDrawable.ImageSlice imageSlice, Paint borderPaint, float borderSize) {

                int vw = dstRect.width();
                int vh = dstRect.height();

                Matrix mShaderMatrix = new Matrix();

                float size;

                float scale;
                float dx = 0;
                float dy = 0;

                // the width and height of bitmap after projected into the view coordinate
                float acw;
                float ach;
                float aOffsetX = 0;
                float aOffsetY = 0;

                // compute the container , Because the image will be in a circle , the width and height of container should be equal.
                vw = Math.min(vw, vh);
                vh = vw;

                Rect outRect = new Rect();
                Gravity.apply(gravity, vw, vh, dstRect, outRect);

                if (dw * vh < vw * dh) {
                    //the width is equal after projection
                    scale = (float) vw / (float) dw;
                    acw = vw;

                    // after the projection the drawable 's height
                    ach = dh * scale;

                    // the circle size after the projection
                    size = Math.min(acw, ach);

                    // use the smallest side as the size of circle
                    //size = Math.min(vh, size);

                    dy = (vh - ach) * 0.5f;

                } else {
                    // the height is equal after projection
                    scale = (float) vh / (float) dh;

                    ach = vh;
                    acw = dw * scale;
                    // the circle size after the projection
                    size = Math.min(acw, ach);

                    // use the smallest side as the size of circle
                    // size = Math.min(vh, size);

                    dx = (vw - acw) * 0.5f;
                }

                mShaderMatrix.setScale(scale, scale);

                switch (imageSlice) {

                    case HEAD:
                        /**
                         *   if you want the head part of this image to be the shader,just use the follow codes:
                         */
                        aOffsetX = (acw - size) * 0.5f;
                        aOffsetY = (ach - size) * 0.5f;
                        break;

                    case TAIL:
                        /**
                         *  if you want the tail part of thie image to be the shader ,just use the follow codes:
                         */
                        aOffsetX = (size - acw) * 0.5f;
                        aOffsetY = (size - ach) * 0.5f;
                        break;

                    // offset of the center of shader relative to the image
                    /** if (aOffsetX == 0 && aOffsetY == 0) ,the center of the shader  is in the center of image.
                     * So the center part of this image will be used as shader;
                     *
                     */
                    default:
                    case MIDDLE:
                        aOffsetX = 0;
                        aOffsetY = 0;
                        break;
                }


                //Note, the center of bitmap is equal to the center of view.

                // if the height of the bitmap is larger than the height of view after transformed to the view coordinate,
                // adjust the dy offset to 0
                dy = dy + aOffsetY;
                dx = dx + aOffsetX;

                mShaderMatrix.postTranslate(dx, dy);


                canvas.save();
                canvas.translate(outRect.left, outRect.top);


                shaderPaint.getShader().setLocalMatrix(mShaderMatrix);
                RectF oval = new RectF(0, 0, vw, vh);


                canvas.drawOval(oval, shaderPaint);
                if (borderSize > 0) {
                    oval.inset(borderSize * 0.5f, borderSize / 2);
                    canvas.drawOval(oval, borderPaint);

                }


                canvas.restore();

                /*if (borderSize > 0 ) {
                    outRect.inset((int)(borderSize * 0.5f), (int)(borderSize/2));
                    canvas.drawOval(new RectF(outRect), borderPaint);

                }*/
            }
        };

        SBitmapDrawable drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        drawable.setAntiAlias(true);
        drawable.setDither(true);

        drawable.setmDrawOperationWithShader(operationWithShader);


        View mCircleView = findViewById(R.id.circle_view);
        mCircleView.setBackground(drawable);

    }
}
