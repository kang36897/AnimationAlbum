package com.gulu.album.item;

import android.hardware.Camera;
import android.util.Log;
/**
 * Created by Administrator on 2015/8/26.
 */
public class ErrorCallback implements android.hardware.Camera.ErrorCallback{
    @Override
    public void onError(int error, Camera camera) {
        if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
            com.gulu.album.Camera.mMediaServerDied = true;
            Log.v(com.gulu.album.Camera.TAG, "media server died");
        }
    }
}
