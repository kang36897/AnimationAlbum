package com.gulu.album;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.gulu.album.view.RadialGradientView;

import java.util.ArrayList;

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

    private ListView mModeComplex;
    private EditText[] mColorViews = new EditText[3];
    private ArrayList<Integer> colors = new ArrayList<>();
    private SeekBar[] mStopViews = new SeekBar[3];
    private ArrayList<Float> stops = new ArrayList<Float>();
    private ArrayList<ColorAndStop> mColorsAndStops = new ArrayList<>();

    private float shaderRadiusRatio;
    private float radiusRatio;
    private View.OnClickListener mDefaultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(true);
        }
    };
    private SeekBar.OnSeekBarChangeListener mDefaultSeekBarOnChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (checkParamsValid() == false) {
                return;
            }

            float stop = (float) progress / seekBar.getMax();
            for (int i = 0; i < mStopViews.length; i++) {
                if (mStopViews[i] == seekBar) {

                    stops.add(i, stop);
                    break;
                }
            }
            int[] tempColors = new int[mColorsAndStops.size()];
            float[] tempStops = new float[mColorsAndStops.size()];
            for (int j = 0; j < mColorsAndStops.size(); j++) {
                tempColors[j] = mColorsAndStops.get(j).color;
                tempStops[j] = mColorsAndStops.get(j).stop;
            }

            mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, tempColors, tempStops);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private ArrayAdapter<ColorAndStop> mListAdapter = new ArrayAdapter<ColorAndStop>(this, 0, mColorsAndStops) {

        private View.OnFocusChangeListener mDefaultOnFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    EditText temp = (EditText) v;
                    if (TextUtils.isEmpty(temp.getText())) {
                        v.requestFocus();
                        return;
                    }


                    ColorAndStop data = (ColorAndStop) v.getTag();
                    data.color = Color.parseColor(temp.getText().toString());

                    v.setEnabled(false);
                }
            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ColorAndStopHolder holder = null;
            if (convertView == null) {
                holder = new ColorAndStopHolder();
                LinearLayout rootView = new LinearLayout(getContext());
                rootView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rootView.setTag(holder);

                EditText colorView = new EditText(getContext());
                rootView.addView(colorView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                colorView.setOnClickListener(mDefaultOnClickListener);
                colorView.setOnFocusChangeListener(mDefaultOnFocusChangeListener);
                holder.mColorView = colorView;

                SeekBar stopView = new SeekBar(getContext());
                rootView.addView(colorView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                stopView.setOnSeekBarChangeListener(mDefaultSeekBarOnChangeListener);
                holder.mStopView = stopView;

            } else {
                holder = (ColorAndStopHolder) convertView.getTag();
            }

            ColorAndStop data = getItem(position);
            holder.mColorView.setTag(data);
            holder.mStopView.setTag(data);

            return convertView;
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


        mModeComplex = (ListView) findViewById(R.id.mode_complex);
        ArrayAdapter
        mModeComplex.setAdapter(mListAdapter);

        for (int i = 0; i < mStopViews.length; i++) {
            mStopViews[i].setOnSeekBarChangeListener(mDefaultSeekBarOnChangeListener);

        }


        mShaderRadiusSeekBar = (SeekBar) findViewById(R.id.shader_ratio);
        mShaderRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (checkParamsValid() == false) {
                    return;
                }

                shaderRadiusRatio = (float) progress / 100;
                radiusRatio = (float) mRadiusSeekBar.getProgress() / 100;

                int[] tempColors = new int[colors.size()];
                float[] tempStops = new float[colors.size()];
                for (int j = 0; j < colors.size(); j++) {
                    tempColors[j] = colors.get(j);
                    tempStops[j] = stops.get(j);
                }

                mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, tempColors, tempStops);
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

                if (checkParamsValid() == false) {
                    return;
                }

                radiusRatio = (float) progress / 100;
                shaderRadiusRatio = (float) mShaderRadiusSeekBar.getProgress() / 100;

                int[] tempColors = new int[colors.size()];
                float[] tempStops = new float[colors.size()];
                for (int j = 0; j < colors.size(); j++) {
                    tempColors[j] = colors.get(j);
                    tempStops[j] = stops.get(j);
                }

                mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, tempColors, tempStops);
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

                for (int i = 0; i < mStopViews.length; i++) {
                    stops[i] = (float) mStopViews[i].getProgress() / mStopViews[i].getMax();
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
                colors.add(0, Color.parseColor(mCenterColorView.getText().toString()));
                colors.add(1, Color.parseColor(mEdgeColorView.getText().toString()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }


            return true;

        } else {
            if (mColorsAndStops.isEmpty()) {
                return false;
            }


            return true;

        }
    }

    public static class ColorAndStop {
        int color;
        float stop;
    }

    static class ColorAndStopHolder {
        public EditText mColorView;
        public SeekBar mStopView;
    }
}
