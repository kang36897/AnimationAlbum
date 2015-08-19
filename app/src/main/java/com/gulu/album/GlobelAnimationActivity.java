package com.gulu.album;

import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class GlobelAnimationActivity extends BaseActivity {


    private ImageView mGlobelView1;
    private ValueAnimator m25FramesInOneSecond;

    private ImageView mGlobelView2;
    private ValueAnimator m40FramesInOneSecond;

    private ImageView mGlobelView3;
    private ValueAnimator m50FramesInOneSecond;

    private ImageView mGlobelView4;
    private ValueAnimator m60FramesInOneSecond;

    private ImageView mGlobelView5;
    private ValueAnimator m72FramesInOneSecond;

    private ImageView mGlobelView6;
    private ValueAnimator m144FramesInOneSecond;


    private ImageView mSpecialGlobelView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globel_animation);

        mGlobelView1 = (ImageView) findViewById(R.id.world_container1);
        /**
         *  1 second 25 frames , 72 * 40 = 2880
         */
        m25FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(2880);
        m25FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m25FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m25FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m25FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();
                mGlobelView1.getDrawable().setLevel(level.intValue());
            }
        });


        mGlobelView2 = (ImageView) findViewById(R.id.world_container2);
        /**
         *  1 second 40 frames , 72 * 25 = 1800
         */
        m40FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(1800);
        m40FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m40FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m40FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m40FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();
                mGlobelView2.getDrawable().setLevel(level.intValue());
            }
        });


        mGlobelView3 = (ImageView) findViewById(R.id.world_container3);
        /**
         *  1 second 50 frames , 72 * 20 = 1440
         */
        m50FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(1440);
        m50FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m50FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m50FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m50FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();
                mGlobelView3.getDrawable().setLevel(level.intValue());
            }
        });


        mGlobelView4 = (ImageView) findViewById(R.id.world_container4);
        /**
         *  1 second 60 frames , 1000 / * 60 * 72  = 1200
         */
        m60FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(1200);
        m60FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m60FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m60FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m60FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();

                mGlobelView4.getDrawable().setLevel(level.intValue());

            }
        });

        mGlobelView5 = (ImageView) findViewById(R.id.world_container5);
        /**
         *  1 second 72 frames , 1000 / * 72 * 72  = 1000
         */
        m72FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(1000);
        m72FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m72FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m72FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m72FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();
                mGlobelView5.getDrawable().setLevel(level.intValue());
            }
        });


        mGlobelView6 = (ImageView) findViewById(R.id.world_container6);
        /**
         *  1 second 144 frames , 1000 / * 144 * 72  = 500
         */
        m144FramesInOneSecond = ValueAnimator.ofInt(0, 360).setDuration(500);
        m144FramesInOneSecond.setInterpolator(new LinearInterpolator());
        m144FramesInOneSecond.setRepeatMode(ValueAnimator.RESTART);
        m144FramesInOneSecond.setRepeatCount(ValueAnimator.INFINITE);
        m144FramesInOneSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer level = (Integer) animation.getAnimatedValue();
                mGlobelView6.getDrawable().setLevel(level.intValue());
            }
        });


        mSpecialGlobelView = (ImageView) findViewById(R.id.special_world);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            m25FramesInOneSecond.start();
            m40FramesInOneSecond.start();
            m50FramesInOneSecond.start();
            m60FramesInOneSecond.start();
            m72FramesInOneSecond.start();
            m144FramesInOneSecond.start();
        } else {
            if (m25FramesInOneSecond.isPaused()) {
                m25FramesInOneSecond.resume();
            } else {
                m25FramesInOneSecond.start();
            }

            if (m40FramesInOneSecond.isPaused()) {
                m40FramesInOneSecond.resume();
            } else {
                m40FramesInOneSecond.start();
            }

            if (m50FramesInOneSecond.isPaused()) {
                m50FramesInOneSecond.resume();
            } else {
                m50FramesInOneSecond.start();
            }

            if (m60FramesInOneSecond.isPaused()) {
                m60FramesInOneSecond.resume();
            } else {
                m60FramesInOneSecond.start();
            }

            if (m72FramesInOneSecond.isPaused()) {
                m72FramesInOneSecond.resume();
            } else {
                m72FramesInOneSecond.start();
            }

            if (m144FramesInOneSecond.isPaused()) {
                m144FramesInOneSecond.resume();
            } else {
                m144FramesInOneSecond.start();
            }

        }

        AnimationDrawable drawable = (AnimationDrawable) mSpecialGlobelView.getDrawable();
        drawable.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            m25FramesInOneSecond.cancel();
            m40FramesInOneSecond.cancel();
            m50FramesInOneSecond.cancel();
            m60FramesInOneSecond.cancel();
            m72FramesInOneSecond.cancel();
            m144FramesInOneSecond.cancel();
        } else {
            m25FramesInOneSecond.pause();
            m40FramesInOneSecond.pause();
            m50FramesInOneSecond.pause();
            m60FramesInOneSecond.pause();
            m72FramesInOneSecond.pause();
            m144FramesInOneSecond.pause();
        }


        AnimationDrawable drawable = (AnimationDrawable) mSpecialGlobelView.getDrawable();
        drawable.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m25FramesInOneSecond != null) {
            m25FramesInOneSecond.cancel();
            m25FramesInOneSecond.removeAllUpdateListeners();
            m25FramesInOneSecond = null;
        }


        if (mGlobelView1 != null) {
            mGlobelView1.setImageDrawable(null);
            mGlobelView1 = null;
        }


        if (m40FramesInOneSecond != null) {
            m40FramesInOneSecond.cancel();
            m40FramesInOneSecond.removeAllUpdateListeners();
            m40FramesInOneSecond = null;
        }


        if (mGlobelView2 != null) {
            mGlobelView2.setImageDrawable(null);
            mGlobelView2 = null;
        }

        if (m50FramesInOneSecond != null) {
            m50FramesInOneSecond.cancel();
            m50FramesInOneSecond.removeAllUpdateListeners();
            m50FramesInOneSecond = null;
        }


        if (mGlobelView3 != null) {
            mGlobelView3.setImageDrawable(null);
            mGlobelView3 = null;
        }

        if (m60FramesInOneSecond != null) {
            m60FramesInOneSecond.cancel();
            m60FramesInOneSecond.removeAllUpdateListeners();
            m60FramesInOneSecond = null;
        }


        if (mGlobelView4 != null) {
            mGlobelView4.setImageDrawable(null);
            mGlobelView4 = null;
        }


        if (m72FramesInOneSecond != null) {
            m72FramesInOneSecond.cancel();
            m72FramesInOneSecond.removeAllUpdateListeners();
            m72FramesInOneSecond = null;
        }


        if (mGlobelView5 != null) {
            mGlobelView5.setImageDrawable(null);
            mGlobelView5 = null;
        }

        if (m144FramesInOneSecond != null) {
            m144FramesInOneSecond.cancel();
            m144FramesInOneSecond.removeAllUpdateListeners();
            m144FramesInOneSecond = null;
        }


        if (mGlobelView6 != null) {
            mGlobelView6.setImageDrawable(null);
            mGlobelView6 = null;
        }

    }
}
