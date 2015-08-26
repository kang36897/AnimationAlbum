package com.gulu.album.utils;

import android.content.Context;
import android.hardware.Camera;
import android.preference.PreferenceGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/8/26.
 */
public class CameraSettings {
    private static final String TAG = "CameraSettings";

    public static void initialCameraPictureSize(
            Context context, Camera.Parameters parameters) {
      /*  // When launching the camera app first time, we will set the picture
        // size to the first one in the list defined in "arrays.xml" and is also
        // supported by the driver.
        List<Camera.Size> supported = parameters.getSupportedPictureSizes();
        if (supported == null) return;
        for (String candidate : context.getResources().getStringArray(
                R.array.pref_camera_picturesize_entryvalues)) {
            if (setCameraPictureSize(candidate, supported, parameters)) {
                SharedPreferences.Editor editor = ComboPreferences
                        .get(context).edit();
                editor.putString(KEY_PICTURE_SIZE, candidate);
                editor.apply();
                return;
            }
        }
        Log.e(TAG, "No supported picture size found");*/
    }

    public static void removePreferenceFromScreen(
            PreferenceGroup group, String key) {
        removePreference(group, key);
    }

    private static boolean removePreference(PreferenceGroup group, String key) {
       /* for (int i = 0, n = group.size(); i < n; i++) {
            CameraPreference child = group.get(i);
            if (child instanceof PreferenceGroup) {
                if (removePreference((PreferenceGroup) child, key)) {
                    return true;
                }
            }
            if (child instanceof ListPreference &&
                    ((ListPreference) child).getKey().equals(key)) {
                group.removePreference(i);
                return true;
            }
        }
        return false;*/
        return false;
    }


    public static boolean setCameraPictureSize(
            String candidate, List<Camera.Size> supported, Camera.Parameters parameters) {
        int index = candidate.indexOf('x');
        if (index == -1) return false;
        int width = Integer.parseInt(candidate.substring(0, index));
        int height = Integer.parseInt(candidate.substring(index + 1));
        for (Camera.Size size : supported) {
            if (size.width == width && size.height == height) {
                parameters.setPictureSize(width, height);
                return true;
            }
        }
        return false;
    }
}
