package com.gulu.album;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

        SBitmapDrawable.DrawOperationWithShader operationWithShader = new SBitmapDrawable.DrawOperationWithShader() {
            @Override
            public void doDrawOperation(Canvas canvas, Paint shaderPaint, int dw, int dh, Rect dstRect, int desity , int gravity) {

                int vw = dstRect.width();
                int vh = dstRect.height();


                float size;

                float scale;
                float dx = 0;
                float dy = 0;

                // the width and height of bitmap after projected into the view coordinate
                float acw;
                float ach;
                float aOffsetX;
                float aOffsetY;

                // compute the container , Because the image will be in a circle , the width and height of container should be equal.
                vw = Math.min(vw,vh);
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
                    size = Math.min(vh, size);

                    dy = (vh - ach) * 0.5f;

                } else {
                    // the height is equal after projection
                    scale = (float) vh / (float) dh;

                    ach = vh;
                    acw = dw * scale;
                    // the circle size after the projection
                    size = Math.min(acw, ach);

                    // use the smallest side as the size of circle
                    size = Math.min(vh, size);

                    dx = (vw - acw) * 0.5f;
                }

                // offset of the center of shader relative to the image
                aOffsetX = (acw - size) * 0.5f;
                aOffsetY = (ach - size) * 0.5f;


                //Note, the center of bitmap is equal to the center of view.

                // if the height of the bitmap is larger than the height of view after transformed to the view coordinate,
                // adjust the dy offset to 0
                dy = Math.max(dy + aOffsetY, 0);
                dx = Math.max(dx + aOffsetX, 0);

                size = Math.min(dw, dh);
                canvas.save();
                canvas.translate(outRect.left, outRect.top);



                canvas.save();
                canvas.scale(scale, scale);
                canvas.translate((int) (dx + 0.5f), (int) (dy + 0.5f));


                canvas.drawOval(new RectF(0, 0, size, size), shaderPaint);

                //canvas.drawCircle(size * 0.5f, size * 0.5f, size * 0.5f, shaderPaint);

                canvas.restore();


                canvas.restore();

            }
        };

        SBitmapDrawable drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.sword_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest1.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest2.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.sword_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest3.setImageDrawable(drawable);


        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest4.setBackground(drawable);
    }
}
