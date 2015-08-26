package com.gulu.album;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gulu.album.item.ErrorCallback;
import com.gulu.album.item.JpegEncodingQualityMappings;
import com.gulu.album.utils.CameraSettings;
import com.gulu.album.utils.ImageManager;
import com.gulu.album.utils.Util;
import com.gulu.album.view.FocusRectangle;
import com.gulu.album.view.PreviewFrameLayout;
import com.gulu.album.view.ShutterButton;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/26.
 */
public class Camera extends BaseActivity implements SurfaceHolder.Callback,  ShutterButton.OnShutterButtonListener{

    public static final String TAG = "camera";

    // multiple cameras support
    private int mNumberOfCameras;
    private int mCameraId;


    private boolean mStartPreviewFail = false;
    private boolean mPreviewing;
    private boolean mPausing;
    private boolean mFirstTimeInitialized;

    private boolean mRecordLocation;

    private android.hardware.Camera mCameraDevice;
    private android.hardware.Camera.Parameters mInitialParams;
    private SurfaceHolder mSurfaceHolder;
    private  android.hardware.Camera.Parameters mParameters;

    // The subset of parameters we need to update in setCameraParameters().
    private static final int UPDATE_PARAM_INITIALIZE = 1;
    private static final int UPDATE_PARAM_ZOOM = 2;
    private static final int UPDATE_PARAM_PREFERENCE = 4;
    private static final int UPDATE_PARAM_ALL = -1;


    private static final int ZOOM_STOPPED = 0;
    private static final int ZOOM_START = 1;
    private static final int ZOOM_STOPPING = 2;

    private int mZoomState = ZOOM_STOPPED;
    private boolean mSmoothZoomSupported = false;
    private int mZoomValue;  // The current zoom value.
    private int mZoomMax;
    private int mTargetZoomValue;

    private static final int IDLE = 1;
    private static final int SNAPSHOT_IN_PROGRESS = 2;


    private int mStatus;

    // Add for test
    public static boolean mMediaServerDied = false;
    private android.hardware.Camera.ErrorCallback mErrorCallback = new ErrorCallback();
    private SurfaceView mSurfaceView;

    // Focus mode. Options are pref_camera_focusmode_entryvalues.
    private String mFocusMode;
    private String mSceneMode;

    private long mFocusStartTime;
    private long mCaptureStartTime;
    private long mShutterCallbackTime;
    private long mPostViewPictureCallbackTime;
    private long mRawPictureCallbackTime;
    private long mJpegPictureCallbackTime;
    private int mPicturesRemaining = 1;


    private final Handler mHandler = new MainHandler();


    private static final int CROP_MSG = 1;
    private static final int FIRST_TIME_INIT = 2;
    private static final int RESTART_PREVIEW = 3;
    private static final int CLEAR_SCREEN_DELAY = 4;
    private static final int SET_CAMERA_PARAMETERS_WHEN_IDLE = 5;

    private FocusRectangle mFocusRectangle;

    private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;
    private int mFocusState = FOCUS_NOT_STARTED;

    // The brightness settings used when it is set to automatic in the system.
    // The reason why it is set to 0.7 is just because 1.0 is too bright.
    private static final float DEFAULT_CAMERA_BRIGHTNESS = 0.7f;

    private static final int FOCUS_BEEP_VOLUME = 100;
    private ToneGenerator mFocusToneGenerator;

    private static final int SCREEN_DELAY = 2 * 60 * 1000;

    // When setCameraParametersWhenIdle() is called, we accumulate the subsets
    // needed to be updated in mUpdateSet.
    private int mUpdateSet;

    // The device orientation in degrees. Default is unknown.
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;



    private final AutoFocusCallback mAutoFocusCallback =
            new AutoFocusCallback();
    private ShutterCallback mShutterCallback = new ShutterCallback();

    private ImageCapture mImageCapture;
    private ShutterButton mShutterButton;


    private final class AutoFocusCallback
            implements android.hardware.Camera.AutoFocusCallback {
        public void onAutoFocus(
                boolean focused, android.hardware.Camera camera) {
            if (mFocusState == FOCUSING_SNAP_ON_FINISH) {
                // Take the picture no matter focus succeeds or fails. No need
                // to play the AF sound if we're about to play the shutter
                // sound.
                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
                mImageCapture.onSnap();
            } else if (mFocusState == FOCUSING) {
                // User is half-pressing the focus key. Play the focus tone.
                // Do not take the picture now.
                ToneGenerator tg = mFocusToneGenerator;
                if (tg != null) {
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                }
                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
            } else if (mFocusState == FOCUS_NOT_STARTED) {
                // User has released the focus key before focus completes.
                // Do nothing.
            }
            updateFocusIndicator();
        }
    }

    private String createName(long dateTaken) {
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getString(R.string.image_file_name_format));

        return dateFormat.format(date);
    }

    private class ImageCapture {

        private Uri mLastContentUri;

        byte[] mCaptureOnlyData;

        // Returns the rotation degree in the jpeg header.
        private int storeImage(byte[] data, Location loc) {

            long dateTaken = System.currentTimeMillis();
            String title = createName(dateTaken);
            String filename = title + ".jpg";
            int[] degree = new int[1];

            Util.addImage(title,dateTaken,loc,Util.CAMERA_IMAGE_BUCKET_NAME,filename, null, data,degree);

           /* try {
                long dateTaken = System.currentTimeMillis();
                String title = createName(dateTaken);
                String filename = title + ".jpg";
                int[] degree = new int[1];
                mLastContentUri = ImageManager.addImage(
                        mContentResolver,
                        title,
                        dateTaken,
                        loc, // location from gps/network
                        ImageManager.CAMERA_IMAGE_BUCKET_NAME, filename,
                        null, data,
                        degree);
                return degree[0];
            } catch (Exception ex) {
                Log.e(TAG, "Exception while compressing image.", ex);
                return 0;
            }*/

            return 0;
        }

        public void storeImage(final byte[] data,
                               android.hardware.Camera camera, Location loc) {

            storeImage(data, loc);
          /*  if (!mIsImageCaptureIntent) {
                int degree = storeImage(data, loc);
                sendBroadcast(new Intent(
                        "com.android.camera.NEW_PICTURE", mLastContentUri));
                setLastPictureThumb(data, degree,
                        mImageCapture.getLastCaptureUri());
                mThumbController.updateDisplayIfNeeded();
            } else {
                mCaptureOnlyData = data;
                showPostCaptureAlert();
            }*/
        }

        /**
         * Initiate the capture of an image.
         */
        public void initiate() {
            if (mCameraDevice == null) {
                return;
            }

            capture();
        }

        public Uri getLastCaptureUri() {
            return mLastContentUri;
        }

        public byte[] getLastCaptureData() {
            return mCaptureOnlyData;
        }

        private void capture() {
            mCaptureOnlyData = null;

            // See android.hardware.Camera.Parameters.setRotation for
            // documentation.
            int rotation = 0;
            if (mOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
                android.hardware.Camera.CameraInfo info = CameraHolder.instance().getCameraInfo()[mCameraId];
                if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotation = (info.orientation - mOrientation + 360) % 360;
                } else {  // back-facing camera
                    rotation = (info.orientation + mOrientation) % 360;
                }
            }
            mParameters.setRotation(rotation);

            // Clear previous GPS location from the parameters.
            mParameters.removeGpsData();

            // We always encode GpsTimeStamp
            mParameters.setGpsTimestamp(System.currentTimeMillis() / 1000);

            // Set GPS location.
            Location loc = mRecordLocation ? getCurrentLocation() : null;
            if (loc != null) {
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                boolean hasLatLon = (lat != 0.0d) || (lon != 0.0d);

                if (hasLatLon) {
                    mParameters.setGpsLatitude(lat);
                    mParameters.setGpsLongitude(lon);
                    mParameters.setGpsProcessingMethod(loc.getProvider().toUpperCase());
                    if (loc.hasAltitude()) {
                        mParameters.setGpsAltitude(loc.getAltitude());
                    } else {
                        // for NETWORK_PROVIDER location provider, we may have
                        // no altitude information, but the driver needs it, so
                        // we fake one.
                        mParameters.setGpsAltitude(0);
                    }
                    if (loc.getTime() != 0) {
                        // Location.getTime() is UTC in milliseconds.
                        // gps-timestamp is UTC in seconds.
                        long utcTimeSeconds = loc.getTime() / 1000;
                        mParameters.setGpsTimestamp(utcTimeSeconds);
                    }
                } else {
                    loc = null;
                }
            }

            mCameraDevice.setParameters(mParameters);

            mCameraDevice.takePicture(mShutterCallback, null,
                    null, new JpegPictureCallback(loc));
            mPreviewing = false;
        }

        public void onSnap() {
            // If we are already in the middle of taking a snapshot then ignore.
            if (mPausing || mStatus == SNAPSHOT_IN_PROGRESS) {
                return;
            }
            mCaptureStartTime = System.currentTimeMillis();
            mPostViewPictureCallbackTime = 0;
           /* mHeadUpDisplay.setEnabled(false);*/
            mStatus = SNAPSHOT_IN_PROGRESS;

            initiate();
        }

        private void clearLastData() {
            mCaptureOnlyData = null;
        }
    }

    private final class JpegPictureCallback implements android.hardware.Camera.PictureCallback {
        Location mLocation;

        public JpegPictureCallback(Location loc) {
            mLocation = loc;
        }

        public void onPictureTaken(
                final byte [] jpegData, final android.hardware.Camera camera) {
            if (mPausing) {
                return;
            }


           //TODO we should pause the preview


       /*         // We want to show the taken picture for a while, so we wait
                // for at least 1.2 second before restarting the preview.
                long delay = 1200 - mPictureDisplayedToJpegCallbackTime;
                if (delay < 0) {
                    restartPreview();
                } else {
                    mHandler.sendEmptyMessageDelayed(RESTART_PREVIEW, delay);
                }*/

            mImageCapture.storeImage(jpegData, camera, mLocation);

            // Calculate this in advance of each shot so we don't add to shutter
            // latency. It's true that someone else could write to the SD card in
            // the mean time and fill it, but that could have happened between the
            // shutter press and saving the JPEG too.
          /*  calculatePicturesRemaining();

            if (mPicturesRemaining < 1) {
                updateStorageHint(mPicturesRemaining);
            }
*/
           /* if (!mHandler.hasMessages(RESTART_PREVIEW)) {
                long now = System.currentTimeMillis();
                mJpegCallbackFinishTime = now - mJpegPictureCallbackTime;
                Log.v(TAG, "mJpegCallbackFinishTime = "
                        + mJpegCallbackFinishTime + "ms");
                mJpegPictureCallbackTime = 0;
            }*/
        }
    }


    private final class ShutterCallback
            implements android.hardware.Camera.ShutterCallback {
        public void onShutter() {
            clearFocusState();
        }
    }

    public Location getCurrentLocation()
    {
        return null;
    }

    private void cancelAutoFocus() {
        // User releases half-pressed focus key.
        if (mStatus != SNAPSHOT_IN_PROGRESS && (mFocusState == FOCUSING
                || mFocusState == FOCUS_SUCCESS || mFocusState == FOCUS_FAIL)) {
            Log.v(TAG, "Cancel autofocus.");
          /*  mHeadUpDisplay.setEnabled(true);*/
            mCameraDevice.cancelAutoFocus();
        }
        if (mFocusState != FOCUSING_SNAP_ON_FINISH) {
            clearFocusState();
        }
    }

    @Override
    public void onShutterButtonFocus(ShutterButton button, boolean pressed) {
        if (mPausing) {
            return;
        }
        switch (button.getId()) {
            case R.id.shutter_button:
                doFocus(pressed);
                break;
        }
    }


    private void doFocus(boolean pressed) {
        // Do the focus if the mode is not infinity.
     /*   if (mHeadUpDisplay.collapse()) return;*/
        if (!(mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY)
                || mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_FIXED)
                || mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_EDOF))) {
            if (pressed) {  // Focus key down.
                autoFocus();
            } else {  // Focus key up.
                cancelAutoFocus();
            }
        }
    }

    private boolean canTakePicture() {
        return isCameraIdle() && mPreviewing && (mPicturesRemaining > 0);
    }

    private void autoFocus() {
        // Initiate autofocus only when preview is started and snapshot is not
        // in progress.
        if (canTakePicture()) {
          /*  mHeadUpDisplay.setEnabled(false);*/
            Log.v(TAG, "Start autofocus.");
            mFocusStartTime = System.currentTimeMillis();
            mFocusState = FOCUSING;
            updateFocusIndicator();
            mCameraDevice.autoFocus(mAutoFocusCallback);
        }
    }

    private void doSnap() {
     /*   if (mHeadUpDisplay.collapse()) return;*/

        Log.v(TAG, "doSnap: mFocusState=" + mFocusState);
        // If the user has half-pressed the shutter and focus is completed, we
        // can take the photo right away. If the focus mode is infinity, we can
        // also take the photo.
        if (mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY)
                || mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_FIXED)
                || mFocusMode.equals(android.hardware.Camera.Parameters.FOCUS_MODE_EDOF)
                || (mFocusState == FOCUS_SUCCESS
                || mFocusState == FOCUS_FAIL)) {
            mImageCapture.onSnap();
        } else if (mFocusState == FOCUSING) {
            // Half pressing the shutter (i.e. the focus button event) will
            // already have requested AF for us, so just request capture on
            // focus here.
            mFocusState = FOCUSING_SNAP_ON_FINISH;
        } else if (mFocusState == FOCUS_NOT_STARTED) {
            // Focus key down event is dropped for some reasons. Just ignore.
        }
    }

    @Override
    public void onShutterButtonClick(ShutterButton button) {
        if (mPausing) {
            return;
        }
        switch (button.getId()) {
            case R.id.shutter_button:
                doSnap();
                break;
        }
    }

    /**
     * This Handler is used to post message back onto the main thread of the
     * application
     */
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTART_PREVIEW: {
                    restartPreview();
                    if (mJpegPictureCallbackTime != 0) {
                        long now = System.currentTimeMillis();
                /*        mJpegCallbackFinishTime = now - mJpegPictureCallbackTime;
                        Log.v(TAG, "mJpegCallbackFinishTime = "
                                + mJpegCallbackFinishTime + "ms");*/
                        mJpegPictureCallbackTime = 0;
                    }
                    break;
                }

                case CLEAR_SCREEN_DELAY: {
                    getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }

                case FIRST_TIME_INIT: {
                    initializeFirstTime();
                    break;
                }

                case SET_CAMERA_PARAMETERS_WHEN_IDLE: {
                    setCameraParametersWhenIdle(0);
                    break;
                }
            }
        }
    }

    // If the Camera is idle, update the parameters immediately, otherwise
    // accumulate them in mUpdateSet and update later.
    private void setCameraParametersWhenIdle(int additionalUpdateSet) {
        mUpdateSet |= additionalUpdateSet;
        if (mCameraDevice == null) {
            // We will update all the parameters when we open the device, so
            // we don't need to do anything now.
            mUpdateSet = 0;
            return;
        } else if (isCameraIdle()) {
            setCameraParameters(mUpdateSet);
            mUpdateSet = 0;
        } else {
            if (!mHandler.hasMessages(SET_CAMERA_PARAMETERS_WHEN_IDLE)) {
                mHandler.sendEmptyMessageDelayed(
                        SET_CAMERA_PARAMETERS_WHEN_IDLE, 1000);
            }
        }
    }

    private boolean isCameraIdle() {
        return mStatus == IDLE && mFocusState == FOCUS_NOT_STARTED;
    }

    // Snapshots can only be taken after this is called. It should be called
    // once only. We could have done these things in onCreate() but we want to
    // make preview screen appear as soon as possible.
    private void initializeFirstTime() {
        if (mFirstTimeInitialized) return;

        // Create orientation listenter. This should be done first because it
        // takes some time to get first orientation.
       /* mOrientationListener = new MyOrientationEventListener(Camera.this);
        mOrientationListener.enable();*/

        // Initialize location sevice.
        //TODO if you want to add the location info,just init it here.
       /* mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        mRecordLocation = RecordLocationPreference.get(
                mPreferences, getContentResolver());
        if (mRecordLocation) startReceivingLocationUpdates();*/

        //TODO check there is enough storge for picture.
      /*  keepMediaProviderInstance();
        checkStorage();*/

        // Initialize last picture button.
    /*    mContentResolver = getContentResolver();
        if (!mIsImageCaptureIntent)  {
            findViewById(R.id.camera_switch).setOnClickListener(this);
            mLastPictureButton =
                    (ImageView) findViewById(R.id.review_thumbnail);
            mLastPictureButton.setOnClickListener(this);
            mThumbController = new ThumbnailController(
                    getResources(), mLastPictureButton, mContentResolver);
            mThumbController.loadData(ImageManager.getLastImageThumbPath());
            // Update last image thumbnail.
            updateThumbnailButton();
        }*/

        // Initialize shutter button.
        mShutterButton = (ShutterButton) findViewById(R.id.shutter_button);
        mShutterButton.setOnShutterButtonListener(this);
        mShutterButton.setVisibility(View.VISIBLE);

        mFocusRectangle = (FocusRectangle) findViewById(R.id.focus_rectangle);
        updateFocusIndicator();

        initializeScreenBrightness();
        //TODO  you can regist broadcast reciever for mounted and unmounted event
       /* installIntentFilter();*/
        initializeFocusTone();
        //TODO you can support zoom gesture here,but let it just default now.
       /* initializeZoom();
        mHeadUpDisplay = new CameraHeadUpDisplay(this);
        mHeadUpDisplay.setListener(new MyHeadUpDisplayListener());
        initializeHeadUpDisplay();*/
        mFirstTimeInitialized = true;
       /* changeHeadUpDisplayState();
        addIdleHandler();*/
    }

    private void initializeFocusTone() {
        // Initialize focus tone generator.
        try {
            mFocusToneGenerator = new ToneGenerator(
                    AudioManager.STREAM_SYSTEM, FOCUS_BEEP_VOLUME);
        } catch (Throwable ex) {
            Log.w(TAG, "Exception caught while creating tone generator: ", ex);
            mFocusToneGenerator = null;
        }
    }

    private void initializeScreenBrightness() {
        Window win = getWindow();
        // Overright the brightness settings if it is automatic
        int mode = Settings.System.getInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.screenBrightness = DEFAULT_CAMERA_BRIGHTNESS;
            win.setAttributes(winParams);
        }
    }


    private void clearFocusState() {
        mFocusState = FOCUS_NOT_STARTED;
        updateFocusIndicator();
    }

    private void updateFocusIndicator() {
        if (mFocusRectangle == null) return;

        if (mFocusState == FOCUSING || mFocusState == FOCUSING_SNAP_ON_FINISH) {
            mFocusRectangle.showStart();
        } else if (mFocusState == FOCUS_SUCCESS) {
            mFocusRectangle.showSuccess();
        } else if (mFocusState == FOCUS_FAIL) {
            mFocusRectangle.showFail();
        } else {
            mFocusRectangle.clear();
        }
    }
    @Override
    public boolean onSearchRequested() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mNumberOfCameras = CameraHolder.instance().getNumberOfCameras();

        // we need to reset exposure for the preview
       // resetExposureCompensation();
        /*
         * To reduce startup time, we start the preview in another thread.
         * We make sure the preview is started at the end of onCreate.
         */
        Thread startPreviewThread = new Thread(new Runnable() {
            public void run() {
                try {
                    mStartPreviewFail = false;
                    startPreview();
                } catch (CameraHardwareException e) {
                    // In eng build, we throw the exception so that test tool
                    // can detect it and report it
               /*     if ("eng".equals(Build.TYPE)) {
                        throw new RuntimeException(e);
                    }*/
                    mStartPreviewFail = true;
                }
            }
        });
        startPreviewThread.start();


        // don't set mSurfaceHolder here. We have it set ONLY within
        // surfaceChanged / surfaceDestroyed, other parts of the code
        // assume that when it is set, the surface is also set.
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //TODO inital some control components here

      /*  ViewGroup rootView = (ViewGroup) findViewById(R.id.camera);
        View controlBar = getLayoutInflater().inflate(
                    R.component_camera_control, rootView);*/



        // Make sure preview is started.
        try {
            startPreviewThread.join();
            if (mStartPreviewFail) {
                showCameraErrorAndFinish();
                return;
            }
        } catch (InterruptedException ex) {
            // ignore
        }

    }

    private void showCameraErrorAndFinish() {
        Resources ress = getResources();
        Util.showFatalErrorAndFinish(Camera.this,
                ress.getString(R.string.camera_error_title),
                ress.getString(R.string.cannot_connect_camera));
    }

    private void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCameraDevice.setPreviewDisplay(holder);
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("setPreviewDisplay failed", ex);
        }
    }

    // We separate the parameters into several subsets, so we can update only
    // the subsets actually need updating. The PREFERENCE set needs extra
    // locking because the preference can be changed from GLThread as well.
    private void setCameraParameters(int updateSet) {
        mParameters = mCameraDevice.getParameters();

        if ((updateSet & UPDATE_PARAM_INITIALIZE) != 0) {
            updateCameraParametersInitialize();
        }

        if ((updateSet & UPDATE_PARAM_ZOOM) != 0) {
            updateCameraParametersZoom();
        }

        if ((updateSet & UPDATE_PARAM_PREFERENCE) != 0) {
            updateCameraParametersPreference();
        }

        mCameraDevice.setParameters(mParameters);
    }


    /**
     *  try to get the biggest size
     * @param parameters
     * @return
     */
    private android.hardware.Camera.Size getBestPictureSize(android.hardware.Camera.Parameters parameters){
        android.hardware.Camera.Size  best = parameters.getSupportedPictureSizes().get(0);
        int area = best.width * best.height;


        for(android.hardware.Camera.Size item : parameters.getSupportedPictureSizes()){

            if(item.width * item.height > area)
            {
                best = item;
                area = item.width * item.height;
            }

        }

        return best;
    }



    private void updateCameraParametersPreference() {
        //TODO Set picture size. choose the value closed to 2M size as the default picture size


       /*  String pictureSize = mPreferences.getString(
                CameraSettings.KEY_PICTURE_SIZE, null);
        if (pictureSize == null) {
            CameraSettings.initialCameraPictureSize(this , mParameters);
        } else {
            List<android.hardware.Camera.Size> supported = mParameters.getSupportedPictureSizes();
            CameraSettings.setCameraPictureSize(
                    pictureSize, supported, mParameters);
        }*/

        // Set the preview frame aspect ratio according to the picture size.
        android.hardware.Camera.Size size = mParameters.getPictureSize();
       /* PreviewFrameLayout frameLayout =
                (PreviewFrameLayout) findViewById(R.id.frame_layout);
        frameLayout.setAspectRatio((double) size.width / size.height);*/

        // Set a preview size that is closest to the viewfinder height and has
        // the right aspect ratio.
        List<android.hardware.Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
        android.hardware.Camera.Size optimalSize = getOptimalPreviewSize(
                sizes, (double) size.width / size.height);
        if (optimalSize != null) {
                android.hardware.Camera.Size original = mParameters.getPreviewSize();
                if (!original.equals(optimalSize)) {
                mParameters.setPreviewSize(optimalSize.width, optimalSize.height);

                // Zoom related settings will be changed for different preview
                // sizes, so set and read the parameters to get lastest values
                mCameraDevice.setParameters(mParameters);
                mParameters = mCameraDevice.getParameters();
            }
        }

        // Since change scene mode may change supported values,
        // Set scene mode first,
        //TODO YOU can change the scene mode , but we left it just as default.
       /* mSceneMode = mPreferences.getString(
                CameraSettings.KEY_SCENE_MODE,
                getString(R.string.pref_camera_scenemode_default));
        if (isSupported(mSceneMode, mParameters.getSupportedSceneModes())) {
            if (!mParameters.getSceneMode().equals(mSceneMode)) {
                mParameters.setSceneMode(mSceneMode);
                mCameraDevice.setParameters(mParameters);

                // Setting scene mode will change the settings of flash mode,
                // white balance, and focus mode. Here we read back the
                // parameters, so we can know those settings.
                mParameters = mCameraDevice.getParameters();
            }
        } else {*/
            mSceneMode = mParameters.getSceneMode();
            if (mSceneMode == null) {
                mSceneMode = android.hardware.Camera.Parameters.SCENE_MODE_AUTO;
            }
        /*}*/

        // Set JPEG quality.
      /*  String jpegQuality = mPreferences.getString(
                CameraSettings.KEY_JPEG_QUALITY,
                getString(R.string.pref_camera_jpegquality_default));*/
        String jpegQuality = getString(R.string.pref_camera_jpegquality_default);

        mParameters.setJpegQuality(JpegEncodingQualityMappings.getQualityNumber(jpegQuality));

        // For the following settings, we need to check if the settings are
        // still supported by latest driver, if not, ignore the settings.

        // Set color effect parameter.
       /* String colorEffect = mPreferences.getString(
                CameraSettings.KEY_COLOR_EFFECT,
                getString(R.string.pref_camera_coloreffect_default));
        if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
            mParameters.setColorEffect(colorEffect);
        }*/

        // Set exposure compensation
        /*String exposure = mPreferences.getString(
                CameraSettings.KEY_EXPOSURE,
                getString(R.string.pref_exposure_default));
        try {
            int value = Integer.parseInt(exposure);
            int max = mParameters.getMaxExposureCompensation();
            int min = mParameters.getMinExposureCompensation();
            if (value >= min && value <= max) {
                mParameters.setExposureCompensation(value);
            } else {
                Log.w(TAG, "invalid exposure range: " + exposure);
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "invalid exposure: " + exposure);
        }*/

      /*  if (mHeadUpDisplay != null) updateSceneModeInHud();*/

        if (android.hardware.Camera.Parameters.SCENE_MODE_AUTO.equals(mSceneMode)) {
            // Set flash mode.
            //TODO you can use the flashMode saved in preference before, or just the default;
           /* String flashMode = mPreferences.getString(
                    CameraSettings.KEY_FLASH_MODE,
                    getString(R.string.pref_camera_flashmode_default));*/
            String flashMode =  getString(R.string.pref_camera_flashmode_default);
            List<String> supportedFlash = mParameters.getSupportedFlashModes();
            if (isSupported(flashMode, supportedFlash)) {
                mParameters.setFlashMode(flashMode);
            } else {
                flashMode = mParameters.getFlashMode();
                if (flashMode == null) {
                    flashMode = getString(
                            R.string.pref_camera_flashmode_no_flash);
                }
            }

            // Set white balance parameter.
            // TODO you can use the balance saved in preference before, or just the defualt value;
          /*  String whiteBalance = mPreferences.getString(
                    CameraSettings.KEY_WHITE_BALANCE,
                    getString(R.string.pref_camera_whitebalance_default));*/
            String whiteBalance = getString(R.string.pref_camera_whitebalance_default);
            if (isSupported(whiteBalance,
                    mParameters.getSupportedWhiteBalance())) {
                mParameters.setWhiteBalance(whiteBalance);
            } else {
                whiteBalance = mParameters.getWhiteBalance();
                if (whiteBalance == null) {
                    whiteBalance = android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;
                }
            }

            // Set focus mode.
            // TODO you can use the balance saved in preference before, or just the defualt value;
            /*   mFocusMode = mPreferences.getString(
                    CameraSettings.KEY_FOCUS_MODE,
                    getString(R.string.pref_camera_focusmode_default));*/
            mFocusMode = getString(R.string.pref_camera_focusmode_default);
            if (isSupported(mFocusMode, mParameters.getSupportedFocusModes())) {
                mParameters.setFocusMode(mFocusMode);
            } else {
                mFocusMode = mParameters.getFocusMode();
                if (mFocusMode == null) {
                    mFocusMode = android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
                }
            }
        } else {
            mFocusMode = mParameters.getFocusMode();
        }
    }

    private void switchCameraId(int cameraId) {
        if (mPausing || !isCameraIdle()) return;
        mCameraId = cameraId;
      /*  CameraSettings.writePreferredCameraId(mPreferences, cameraId);*/

        stopPreview();
        closeCamera();

        // Remove the messages in the event queue.
        mHandler.removeMessages(RESTART_PREVIEW);

        // Reset variables
        mJpegPictureCallbackTime = 0;
        mZoomValue = 0;

        // Reload the preferences.
    /*    mPreferences.setLocalId(this, mCameraId);*/
 /*       CameraSettings.upgradeLocalPreferences(mPreferences.getLocal());*/

        // Restart the preview.
      /*  resetExposureCompensation();*/
        if (!restartPreview()) return;

       /* initializeZoom();*/

        // Reload the UI.
        if (mFirstTimeInitialized) {
          /*  initializeHeadUpDisplay();*/
        }
    }


    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    private android.hardware.Camera.Size getOptimalPreviewSize(List<android.hardware.Camera.Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        android.hardware.Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (android.hardware.Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.v(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (android.hardware.Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    private void updateCameraParametersZoom() {

    }

    private void updateCameraParametersInitialize() {
        // Reset preview frame rate to the maximum because it may be lowered by
        // video camera application.
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

    }

    private void startPreview() throws CameraHardwareException {
        if (mPausing || isFinishing()) return;

        ensureCameraDevice();

        // If we're previewing already, stop the preview first (this will blank
        // the screen).
        if (mPreviewing) stopPreview();

        setPreviewDisplay(mSurfaceHolder);
        Util.setCameraDisplayOrientation(this, mCameraId, mCameraDevice);
        setCameraParameters(UPDATE_PARAM_ALL);

        mCameraDevice.setErrorCallback(mErrorCallback);

        try {
            Log.v(TAG, "startPreview");
            mCameraDevice.startPreview();
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("startPreview failed", ex);
        }
        mPreviewing = true;
        mZoomState = ZOOM_STOPPED;
        mStatus = IDLE;
    }

    private void ensureCameraDevice() throws CameraHardwareException {
        if (mCameraDevice == null) {
            mCameraDevice = CameraHolder.instance().open(mCameraId);
            mInitialParams = mCameraDevice.getParameters();
        }
    }

    private void stopPreview() {
        if (mCameraDevice != null && mPreviewing) {
            Log.v(TAG, "stopPreview");
            mCameraDevice.stopPreview();
        }
        mPreviewing = false;
        // If auto focus was in progress, it would have been canceled.
        clearFocusState();
    }




    private void closeCamera() {
        if (mCameraDevice != null) {
            CameraHolder.instance().release();
            mCameraDevice.setZoomChangeListener(null);
            mCameraDevice = null;
            mPreviewing = false;
        }
    }


    private boolean restartPreview() {
        try {
            startPreview();
        } catch (CameraHardwareException e) {
            showCameraErrorAndFinish();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
       super.onResume();
        mPausing = false;
        mJpegPictureCallbackTime = 0;
        mZoomValue = 0;
        // create a image handle class
       mImageCapture = new ImageCapture();

        // Start the preview if it is not started.
        if (!mPreviewing && !mStartPreviewFail) {
         /*   resetExposureCompensation();*/
            if (!restartPreview()) return;
        }

        if (mSurfaceHolder != null) {
            // If first time initialization is not finished, put it in the
            // message queue.
            if (!mFirstTimeInitialized) {
                mHandler.sendEmptyMessage(FIRST_TIME_INIT);
            } else {
                initializeSecondTime();
            }
        }
        keepScreenOnAwhile();
    }

    private void keepScreenOnAwhile() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
    }

    // If the activity is paused and resumed, this method will be called in
    // onResume.
    private void initializeSecondTime() {
        // Start orientation listener as soon as possible because it takes
        // some time to get first orientation.
       /* mOrientationListener.enable();*/

        //TODO if you want to add the location info,just init it here.
        // Start location update if needed.
      /*  mRecordLocation = RecordLocationPreference.get(
                mPreferences, getContentResolver());
        if (mRecordLocation) startReceivingLocationUpdates();*/

        //TODO  you can regist broadcast reciever for mounted and unmounted event
        /*installIntentFilter();*/
        initializeFocusTone();
        //TODO you can support zoom gesture here,but let it just default now.
      /*  initializeZoom();
        changeHeadUpDisplayState();

        keepMediaProviderInstance();*/
        //TODO check there is enough storge for picture.
      /*  checkStorage();*/

     /*   if (!mIsImageCaptureIntent) {
            updateThumbnailButton();
        }*/
    }

    private void resetScreenOn() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        mPausing = true;
        stopPreview();
        // Close the camera now because other activities may need to use it.
        closeCamera();
        resetScreenOn();
        /*changeHeadUpDisplayState();*/

        if (mFirstTimeInitialized) {
         /*   mOrientationListener.disable();
            if (!mIsImageCaptureIntent) {
                mThumbController.storeData(
                        ImageManager.getLastImageThumbPath());
            }
            hidePostCaptureAlert();*/
        }

       /* if (mDidRegister) {
            unregisterReceiver(mReceiver);
            mDidRegister = false;
        }
        stopReceivingLocationUpdates();*/

        if (mFocusToneGenerator != null) {
            mFocusToneGenerator.release();
            mFocusToneGenerator = null;
        }

       /* if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;
        }*/

        // If we are in an image capture intent and has taken
        // a picture, we just clear it in onPause.
      /*  mImageCapture.clearLastData();
        mImageCapture = null;*/

        // Remove the messages in the event queue.
        mHandler.removeMessages(RESTART_PREVIEW);
        mHandler.removeMessages(FIRST_TIME_INIT);

        super.onPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // Make sure we have a surface in the holder before proceeding.
        if (holder.getSurface() == null) {
            Log.d(TAG, "holder.getSurface() == null");
            return;
        }

        // We need to save the holder for later use, even when the mCameraDevice
        // is null. This could happen if onResume() is invoked after this
        // function.
        mSurfaceHolder = holder;

        // The mCameraDevice will be null if it fails to connect to the camera
        // hardware. In this case we will show a dialog and then finish the
        // activity, so it's OK to ignore it.
        if (mCameraDevice == null) return;

        // Sometimes surfaceChanged is called after onPause or before onResume.
        // Ignore it.
        if (mPausing || isFinishing()) return;

        if (mPreviewing && holder.isCreating()) {
            // Set preview display if the surface is being created and preview
            // was already started. That means preview display was set to null
            // and we need to set it now.
            setPreviewDisplay(holder);
        } else {
            // 1. Restart the preview if the size of surface was changed. The
            // framework may not support changing preview display on the fly.
            // 2. Start the preview now if surface was destroyed and preview
            // stopped.
            restartPreview();
        }

        // If first time initialization is not finished, send a message to do
        // it later. We want to finish surfaceChanged as soon as possible to let
        // user see preview first.
        if (!mFirstTimeInitialized) {
            mHandler.sendEmptyMessage(FIRST_TIME_INIT);
        } else {
            initializeSecondTime();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        mSurfaceHolder = null;
    }
}
