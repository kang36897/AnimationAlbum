package com.gulu.album;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gulu.album.view.RadialGradientMode;
import com.gulu.album.view.RadialGradientView;

import java.util.ArrayList;


/**
 * Created by lulala on 20/9/15.
 */
public class RadialGradientActivity extends BaseActivity {
    final static String TAG = "RadialGradientActivity";
    private SeekBar mShaderRadiusSeekBar;
    private SeekBar mRadiusSeekBar;
    private RadialGradientView mRadialGradientView;

    private int mode;

    private LinearLayout mModeNormal;
    private EditText mCenterColorView;
    private EditText mEdgeColorView;

    private LinearLayout mModeComplex;
    private Button mAddBtn;
    private Button mDeleteBtn;

    private int mPadding;
    private ArrayList<ColorAndStop> mColorAndStop = new ArrayList<>();


    private float shaderRadiusRatio;
    private float radiusRatio;

    private View.OnClickListener mDefaultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"onClick()");
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
        }
    };

    private TextView.OnEditorActionListener mDefaultOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


            if (actionId == EditorInfo.IME_ACTION_DONE) {

              v.setFocusable(false);
            }

            return false;
        }
    };

    private View.OnFocusChangeListener mDefaultOnFocusChangeListenr = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText colorView = (EditText) v;
            if (hasFocus) {

                return;
            }

            ColorAndStopHolder holder = (ColorAndStopHolder) colorView.getTag();
            String colorString = colorView.getText().toString();
            if (TextUtils.isEmpty(colorString)) {
                Toast.makeText(getApplicationContext(), R.string.color_empty_warning, Toast.LENGTH_SHORT).show();
                v.requestFocus();
                return;
            }


            try {
                holder.mData.color = Color.parseColor(colorString);
                // colorView.setEnabled(false);

                drawAfterUpdate();
            } catch (Exception e) {
                colorView.setText("#");
                colorView.requestFocus();
            }

        }
    };

    private SeekBar.OnSeekBarChangeListener mDefaultSeekBarOnChangeListener = new SimpleOnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ColorAndStopHolder holder = (ColorAndStopHolder) seekBar.getTag();

            float ratio = (float) progress / 100;
            holder.mData.stop = ratio;
            holder.mHintView.setText(String.valueOf(ratio));

            drawAfterUpdate();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_demo_radial_gradient);

        mPadding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        mRadialGradientView = (RadialGradientView) findViewById(R.id.radial_gradient_view);
        mRadialGradientView.requestFocus();

        mModeNormal = (LinearLayout) findViewById(R.id.mode_nomral);

        ColorAndStopHolder holder = new ColorAndStopHolder();
        holder.mData = getColorAndStopAtPosition(0);

        mCenterColorView = (EditText) findViewById(R.id.center_color_view);
        mCenterColorView.setTag(holder);
        mCenterColorView.setOnFocusChangeListener(mDefaultOnFocusChangeListenr);
        mCenterColorView.setOnClickListener(mDefaultOnClickListener);


         mCenterColorView.setOnEditorActionListener(mDefaultOnEditorActionListener);
        holder.mData.color = Color.parseColor(getString(R.string.default_center_color));


        holder = new ColorAndStopHolder();
        holder.mData = getColorAndStopAtPosition(1);
        mEdgeColorView = (EditText) findViewById(R.id.edge_color_view);
        mEdgeColorView.setTag(holder);
        mEdgeColorView.setOnFocusChangeListener(mDefaultOnFocusChangeListenr);
        mEdgeColorView.setOnClickListener(mDefaultOnClickListener);
          mEdgeColorView.setOnEditorActionListener(mDefaultOnEditorActionListener);
        holder.mData.color = Color.parseColor(getString(R.string.default_edge_color));


        mModeComplex = (LinearLayout) findViewById(R.id.mode_complex);
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ColorAndStopHolder holder = new ColorAndStopHolder();
                holder.mData = getColorAndStopAtNextPosition();

                LinearLayout item = new LinearLayout(RadialGradientActivity.this);
                item.setTag(holder);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.
                        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                item.setLayoutParams(layoutParams);

                EditText colorView = new EditText(RadialGradientActivity.this);
                colorView.setTag(holder);
                colorView.setHint(R.string.color_hint);
                colorView.setTextColor(Color.BLACK);
                colorView.setMaxEms(12);
                colorView.setPadding(mPadding, mPadding, mPadding, mPadding);

                colorView.setOnFocusChangeListener(mDefaultOnFocusChangeListenr);
                colorView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                colorView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                 colorView.setOnEditorActionListener(mDefaultOnEditorActionListener);
                colorView.setOnClickListener(mDefaultOnClickListener);

                layoutParams = new LinearLayout.
                        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.mColorView = colorView;
                item.addView(colorView, layoutParams);


                SeekBar stopView = new SeekBar(RadialGradientActivity.this);
                stopView.setTag(holder);
                stopView.setOnSeekBarChangeListener(mDefaultSeekBarOnChangeListener);
                stopView.setMax(100);

                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                holder.mStopView = stopView;
                item.addView(stopView, layoutParams);

                TextView hintView = new TextView(RadialGradientActivity.this);
                hintView.setPadding(mPadding, mPadding, mPadding, mPadding);

                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                holder.mHintView = hintView;
                item.addView(hintView, layoutParams);

                mModeComplex.addView(item);

            }
        });


        mDeleteBtn = (Button) findViewById(R.id.delete_btn);
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mModeComplex.getChildCount() == 1) {
                    return;
                }

                int index = mModeComplex.getChildCount() - 1;
                LinearLayout item = (LinearLayout) mModeComplex.getChildAt(index);
                ColorAndStopHolder holder = (ColorAndStopHolder) item.getTag();
                item.setTag(null);

                holder.mData = null;
                holder.mColorView.setTag(null);
                holder.mColorView = null;

                holder.mStopView.setTag(null);
                holder.mStopView = null;

                holder.mHintView = null;

                mModeComplex.removeViewAt(index);


            }
        });

        mShaderRadiusSeekBar = (SeekBar) findViewById(R.id.shader_ratio);
        mShaderRadiusSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                shaderRadiusRatio = (float) progress / 100;
                drawAfterUpdate();

            }


        });

        mRadiusSeekBar = (SeekBar) findViewById(R.id.radius_ratio);
        mRadiusSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                radiusRatio = (float) progress / 100;
                drawAfterUpdate();
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
                    mode = RadialGradientMode.POSITION_RADIAL_GRADIENT;
                    mModeNormal.setVisibility(View.GONE);
                    mModeComplex.setVisibility(View.VISIBLE);

                } else {
                    mode = RadialGradientMode.NORMAL_RADIAL_GRADIENT;
                    mModeComplex.setVisibility(View.GONE);
                    mModeNormal.setVisibility(View.VISIBLE);
                }

                mRadialGradientView.switchMode(mode);
            }
        });


        mode = mRadialGradientView.getMode();
        radiusRatio = (float) mRadiusSeekBar.getProgress() / 100;
        shaderRadiusRatio = (float) mShaderRadiusSeekBar.getProgress() / 100;


    }


    private ColorAndStop getColorAndStopAtPosition(int position) {
        ColorAndStop data = null;
        if (mColorAndStop.isEmpty()) {
            data = new ColorAndStop();
            mColorAndStop.add(position, data);
            return data;
        }


        if (position < mColorAndStop.size()) {
            data = mColorAndStop.get(position);
        } else {
            data = new ColorAndStop();
            mColorAndStop.add(position, data);
        }
        return data;
    }

    @NonNull
    private ColorAndStop getColorAndStopAtNextPosition() {
        int index = mModeComplex.getChildCount() - 1;

        return getColorAndStopAtPosition(index);
    }

    private void drawAfterUpdate() {
        int[] tempColors;
        float[] tempStops;

        if (mode == RadialGradientMode.NORMAL_RADIAL_GRADIENT) {
            tempColors = new int[2];
            tempStops = null;

            for (int j = 0; j < 2; j++) {
                ColorAndStop data = mColorAndStop.get(j);
                tempColors[j] = data.color;
            }


        } else {
            int length = mModeComplex.getChildCount() - 1;
            if (length < 2) {
                return;
            }

            tempColors = new int[length];
            tempStops = new float[length];

            for (int i = 0; i < length; i++) {
                ColorAndStop data = mColorAndStop.get(i);
                tempColors[i] = data.color;
                tempStops[i] = data.stop;
            }


        }

        mRadialGradientView.updateShader(shaderRadiusRatio, radiusRatio, tempColors, tempStops);
    }


    public static class ColorAndStop {
        int color;
        float stop;
    }

    static class ColorAndStopHolder {
        public EditText mColorView;
        public SeekBar mStopView;
        public TextView mHintView;

        public ColorAndStop mData;
    }


    static class SimpleOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
