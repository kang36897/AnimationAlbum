package com.gulu.album.item;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/7/31.
 */
public class EPhoto implements Parcelable{

    private int mBitmpResId;
    private Bitmap mBitmap;

    public EPhoto(Parcel input){
        mBitmpResId = input.readInt();
     mBitmap = input.readParcelable(EPhoto.class.getClassLoader());
    }

    public EPhoto(int resId){
        mBitmpResId = resId;

    }

    public EPhoto(Bitmap bitmap){
        mBitmap = bitmap;
    }

    public int getmBitmpResId() {
        return mBitmpResId;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBitmpResId);
        dest.writeParcelable(mBitmap, flags);
    }

    public final static Creator<EPhoto> CREATOR = new Creator<EPhoto>(){

        @Override
        public EPhoto createFromParcel(Parcel source) {
            return new EPhoto(source);
        }

        @Override
        public EPhoto[] newArray(int size) {
            return new EPhoto[size];
        }
    };


}
