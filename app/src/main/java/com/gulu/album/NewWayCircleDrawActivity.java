package com.gulu.album;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gulu.album.graphics.SBitmapDrawable;
import com.gulu.album.R;

import static com.gulu.album.graphics.SBitmapDrawable.*;
import static com.gulu.album.graphics.SBitmapDrawable.ImageSlice.MIDDLE;

/**
 * Created by lulala on 3/8/15.
 */
public class NewWayCircleDrawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_way_circle_draw);

        ImageView mTest1 = (ImageView) findViewById(R.id.test1);
        ImageView mTest2 = (ImageView) findViewById(R.id.test2);
        ImageView mTest3 = (ImageView) findViewById(R.id.test3);
        LinearLayout mTest4 = (LinearLayout) findViewById(R.id.test4);

        DrawOperationWithShader operationWithShader = new DrawOperationWithShader() {
            @Override
            public void doDrawOperation(Canvas canvas, Paint shaderPaint, int dw, int dh,
                                        Rect dstRect, int desity, int gravity,
                                        ImageSlice imageSlice, Paint borderPaint, float borderSize) {

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

        SBitmapDrawable drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.sword_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setGravity(Gravity.CENTER);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setImageSliceToBeShader(ImageSlice.TAIL);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest1.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.feeling_gril));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setImageSliceToBeShader(ImageSlice.MIDDLE);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest2.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.flower_girl));
        drawable.setGravity(Gravity.BOTTOM);
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setImageSliceToBeShader(ImageSlice.HEAD);
        drawable.setAntiAlias(true);
        drawable.setBorderSize(2);
        drawable.setBorderColor(Color.CYAN);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest3.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setGravity(Gravity.RIGHT);
        drawable.setBorderColor(Color.WHITE);
        drawable.setBorderSize(2);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest4.setBackground(drawable);
    }
}
