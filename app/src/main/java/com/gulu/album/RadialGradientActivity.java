package com.gulu.album;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import com.gulu.album.view.RadialGradientView;

/**
 * Created by lulala on 20/9/15.
 */
public class RadialGradientActivity extends BaseActivity {
    private SeekBar mShaderRadiusSeekBar;
    private SeekBar mRadiusSeekBar;
    private RadialGradientView mRadialGradientView;

    private int mode;

    private LinearLayout mModeNormal;
    private EditText mCenterColorView;
    private EditText mEdgeColorView;

    private LinearLayout mModeComplex;
    private EditText[] mColorViews = new EditText[3];
    private int[] colors = new int[3];
    private SeekBar[] mStopViews = new SeekBar[3];
    private float[] stops = new float[3];

    private float shaderRadiusRatio;
    private float radiusRatio;


    private SeekBar.OnSeekBarChangeListener mStopsSeekBarOnChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if(checkParamsValid() == false)
            {
                return;
            }

            float stop = (float)progress / seekBar.getMax();
            for(int i = 0 ; i< mStopViews.length; i++){
                if(mStopViews[i] == seekBar)
                {

                    stops[i] = stop;
                    break;
                }
            }

            mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, colors, stops);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_demo_radial_gradient);

        mRadialGradientView = (RadialGradientView) findViewById(R.id.radial_gradient_view);
        mModeNormal = (LinearLayout) findViewById(R.id.mode_nomral);
        mCenterColorView = (EditText) findViewById(R.id.center_color_view);
        mEdgeColorView = (EditText) findViewById(R.id.edge_color_view);


        mModeComplex = (LinearLayout) findViewById(R.id.mode_complex);
        mColorViews[0] = (EditText) findViewById(R.id.color_one);
        mColorViews[1] = (EditText) findViewById(R.id.color_two);
        mColorViews[2] = (EditText) findViewById(R.id.color_three);



        mStopViews[0] = (SeekBar) findViewById(R.id.stop_one);
        mStopViews[1] = (SeekBar) findViewById(R.id.stop_two);
        mStopViews[2] = (SeekBar) findViewById(R.id.stop_three);

        for(int i = 0; i< mStopViews.length; i++){
            mStopViews[i].setOnSeekBarChangeListener(mStopsSeekBarOnChangeListener);

        }




        mShaderRadiusSeekBar = (SeekBar) findViewById(R.id.shader_ratio);
        mShaderRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(checkParamsValid() == false)
                {
                    return;
                }

                 shaderRadiusRatio = (float) progress / 100;
                 radiusRatio = (float) mRadiusSeekBar.getProgress() / 100;
                mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, colors, stops);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRadiusSeekBar = (SeekBar) findViewById(R.id.radius_ratio);
        mRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(checkParamsValid() == false)
                {
                    return;
                }

                 radiusRatio = (float) progress / 100;
                 shaderRadiusRatio = (float) mShaderRadiusSeekBar.getProgress() / 100;
                mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, colors, stops);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        Switch switcher = new Switch(this);
        switcher.setTextOff("Normal");
        switcher.setTextOn("Complex");
        switcher.setChecked(false);
        actionBar.setCustomView(switcher, lp);

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mode = RadialGradientView.POSITION_RADIAL_GRADIENT;
                    mModeNormal.setVisibility(View.GONE);
                    mModeComplex.setVisibility(View.VISIBLE);

                } else {
                    mode = RadialGradientView.NORMAL_RADIAL_GRADIENT;
                    mModeComplex.setVisibility(View.GONE);
                    mModeNormal.setVisibility(View.VISIBLE);
                }

                for(int i = 0; i< mStopViews.length; i++){
                    stops[i] = (float)mStopViews[i].getProgress() / mStopViews[i].getMax();
                    colors[i] = Color.parseColor(mColorViews[i].getText().toString());

                }

                mRadialGradientView.switchMode(mode);
            }
        });

        mode = mRadialGradientView.getMode();
        radiusRatio = (float) mRadiusSeekBar.getProgress() / 100;
        shaderRadiusRatio = (float) mShaderRadiusSeekBar.getProgress() / 100;


    }

    private boolean checkParamsValid() {
        if (mode == RadialGradientView.NORMAL_RADIAL_GRADIENT) {
            if (TextUtils.isEmpty(mCenterColorView.getText()) || TextUtils.isEmpty(mEdgeColorView.getText())) {
                return false;
            }

            try {
                colors[0] = Color.parseColor(mCenterColorView.getText().toString());
                colors[1] = Color.parseColor(mEdgeColorView.getText().toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }


            return true;

        } else {
            if (colors.length != mColorViews.length) {
                return false;
            }

            for (int i = 0; i < mColorViews.length; i++) {
                if (TextUtils.isEmpty(mColorViews[i].getText())) {
                    return false;
                }
                try{
                    colors[i] = Color.parseColor(mColorViews[i].getText().toString());

                }catch (IllegalArgumentException e){
                 e.printStackTrace();
                    return false;
                }
            }

            return true;

        }
    }
}
