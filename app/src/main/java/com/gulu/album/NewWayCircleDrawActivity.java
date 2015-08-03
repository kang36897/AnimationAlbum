package com.gulu.album;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.ImageView;

import com.gulu.album.graphics.SBitmapDrawable;

/**
 * Created by lulala on 3/8/15.
 */
public class NewWayCircleDrawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_way_circle_draw);

        ImageView mTest1 = (ImageView) findViewById(R.id.test1);

        SBitmapDrawable.DrawOperationWithShader operationWithShader = new SBitmapDrawable.DrawOperationWithShader() {
            @Override
            public void doDrawOperation(Canvas canvas, Paint shaderPaint, int dw, int dh, Rect dstRect) {



                int vw = dstRect.width();
                int vh = dstRect.height();


                float cx = dstRect.centerX();
                float cy = dstRect.centerY();
                float size;

                float scale;
                float dx = 0;
                float dy = 0;
                if (dw * vh < vw * dh) {
                    scale = (float) vw / (float) dw;
                    dy = (vh - dh * scale) * 0.5f;

                } else {
                    scale = (float) vh / (float) dh;
                    dx = (vw - dw * scale) * 0.5f;
                }

                size = dw > dh ? dh : dw;

                canvas.save();


                canvas.scale(scale, scale);
                canvas.translate(dx, dy);
                //canvas.drawOval(shaderPaint);
                canvas.drawOval(new RectF(0, 0, size, size), shaderPaint);
                canvas.restore();

            }
        };

        SBitmapDrawable drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);

        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);


        mTest1.setImageDrawable(drawable);


        ImageView mTest2 = (ImageView) findViewById(R.id.test2);
        drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setAntiAlias(true);

        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(operationWithShader);
        mTest2.setImageDrawable(drawable);

    }
}
