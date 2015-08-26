package com.gulu.album.item;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Administrator on 2015/8/25.
 */
public interface IImage {
    static final int THUMBNAIL_TARGET_SIZE = 320;
    static final int MINI_THUMB_TARGET_SIZE = 96;
    static final int THUMBNAIL_MAX_NUM_PIXELS = 512 * 384;
    static final int MINI_THUMB_MAX_NUM_PIXELS = 128 * 128;
    static final int UNCONSTRAINED = -1;

    public static final boolean ROTATE_AS_NEEDED = true;
    public static final boolean NO_ROTATE = false;

    public abstract Uri fullSizeImageUri();

    // Get metadata of the image
    public abstract long getDateTaken();

    // Get the bitmap of the mini thumbnail.
    public abstract Bitmap miniThumbBitmap();
}
