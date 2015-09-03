package com.gulu.album.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.gulu.album.R;

/**
 * A button designed to be used for the on-screen shutter button.
 * It's currently an {@code ImageView} that can call a delegate when the
 * pressed state changes.
 */
public class ShutterButton extends ImageView {

    /**
     * A callback to be invoked when a ShutterButton's pressed state changes.
     */
    public interface OnShutterButtonListener {
        /**
         * Called when a ShutterButton has been pressed.
         *
         * @param b The ShutterButton that was pressed.
         */
        void onShutterButtonFocus(ShutterButton b, boolean pressed);
        void onShutterButtonClick(ShutterButton b);
    }



    private OnShutterButtonListener mListener;
    private boolean mOldPressed;

    final static int ID_SENSOR_CHANGED = 0X11;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticSensor;
    private boolean mIsListened = false;

    private float[] gravity;
    private float[] geomagnetic;
    private float[] mOrientation;

    private boolean readyA;
    private boolean readyB;

    public ShutterButton(Context context) {
        super(context);
        initSensor();
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSensor();
    }

    public ShutterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

            initSensor();

    }

    public void setOnShutterButtonListener(OnShutterButtonListener listener) {
        mListener = listener;
    }


    private void initSensor()
    {
        if(isInEditMode())
        {
            return;
        }

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }


    private void HandleOrientation(float[] orientation)
    {

    }


    public void onResume()
    {
        if(mIsListened)
        {
            return;
        }

        if (mAccelerometer != null && mMagneticSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_UI);
        }

        mIsListened = true;

    }


    public void onPause()
    {
        if(!mIsListened)
        {
            return;
        }

        if (mAccelerometer != null && mMagneticSensor != null) {
            mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);
            mSensorManager.unregisterListener(mSensorEventListener, mMagneticSensor);
        }

        mIsListened = false;
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == mAccelerometer) {
                gravity = event.values;
                readyA = true;
            } else if (event.sensor == mMagneticSensor) {
                geomagnetic = event.values;
                readyB = true;
            }

            if (readyA && readyB) {
                float[] mRotateMatrix = new float[9];
                if (mSensorManager.getRotationMatrix(mRotateMatrix, null, gravity, geomagnetic)) {
                    float[] values = new float[3];
                    mOrientation = mSensorManager.getOrientation(mRotateMatrix, values);

                    ShutterButton.this.post(new Runnable() {
                        @Override
                        public void run() {
                            float azimuth = mOrientation[0];


                             getDrawable().setLevel((int) map(azimuth, -Math.PI, Math.PI, 0, 360));

                            HandleOrientation(mOrientation);
                        }
                    });
                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };

    public static double map(float value, double minValue, double maxValue, double minTarget, double maxTarget)
    {
        if(minValue == maxValue)
        {
            throw new IllegalArgumentException("minValue and maxValue should not be equal");
        }

       return   (value - minValue) / (maxValue - minValue) * (maxTarget - minTarget) + minTarget;


    }


    /**
     * Hook into the drawable state changing to get changes to isPressed -- the
     * onPressed listener doesn't always get called when the pressed state
     * changes.
     */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean pressed = isPressed();
        if (pressed != mOldPressed) {
            if (!pressed) {
                // When pressing the physical camera button the sequence of
                // events is:
                //    focus pressed, optional camera pressed, focus released.
                // We want to emulate this sequence of events with the shutter
                // button. When clicking using a trackball button, the view
                // system changes the the drawable state before posting click
                // notification, so the sequence of events is:
                //    pressed(true), optional click, pressed(false)
                // When clicking using touch events, the view system changes the
                // drawable state after posting click notification, so the
                // sequence of events is:
                //    pressed(true), pressed(false), optional click
                // Since we're emulating the physical camera button, we want to
                // have the same order of events. So we want the optional click
                // callback to be delivered before the pressed(false) callback.
                //
                // To do this, we delay the posting of the pressed(false) event
                // slightly by pushing it on the event queue. This moves it
                // after the optional click notification, so our client always
                // sees events in this sequence:
                //     pressed(true), optional click, pressed(false)
                post(new Runnable() {
                    public void run() {
                        callShutterButtonFocus(pressed);
                    }
                });
            } else {
                callShutterButtonFocus(pressed);
            }
            mOldPressed = pressed;
        }
    }

    private void callShutterButtonFocus(boolean pressed) {
        if (mListener != null) {
            mListener.onShutterButtonFocus(this, pressed);
        }
    }

    @Override
    public boolean performClick() {
        boolean result = super.performClick();
        if (mListener != null) {
            mListener.onShutterButtonClick(this);
        }
        return result;
    }

}
