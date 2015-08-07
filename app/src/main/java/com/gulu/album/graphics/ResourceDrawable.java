package com.gulu.album.graphics;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/8/7.
 */
public class ResourceDrawable extends  AutoThumbnailDrawable<Integer> {
    private Resources mResources;

    private  BitmapFactory.Options mOptions;

    public ResourceDrawable(Resources resources){
        mResources = resources;
        mOptions = new BitmapFactory.Options();
        mOptions.inJustDecodeBounds = true;
    }

    public void setImage(int resourceId){
        InputStream is = null;
       try{
          is = getFallbackImageStream(resourceId);
           BitmapFactory.decodeStream(is, null, mOptions);

           setImage(resourceId,mOptions.outWidth,mOptions.outHeight );

       }catch(Resources.NotFoundException e){
           Log.d("ResourceDrawable", e.getMessage());
       }finally {
           if(is != null){
               try {
                   is.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }

    }



    @Override
    protected byte[] getPreferredImageBytes(Integer data) {

        return null;
    }

    @Override
    protected InputStream getFallbackImageStream(Integer data) {

        InputStream is;
        try{
          is =  mResources.openRawResource(data);
        }catch(Resources.NotFoundException e){
            return null;
        }

        return is;
    }

    @Override
    protected boolean dataChangedLocked(Integer data) {
        return !data.equals(mData);
    }
}
