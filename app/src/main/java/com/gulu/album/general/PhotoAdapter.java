package com.gulu.album.general;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gulu.album.graphics.ResourceDrawable;

import java.util.List;

/**
 * Created by Administrator on 2015/8/17.
 */
public class PhotoAdapter extends BaseAdapter {
    private Context mContext;

    private List<Integer> mData;

    private int mColumSize;

    public PhotoAdapter(Context context, List<Integer> data, int columSize) {
        mContext = context;
        mData = data;
        mColumSize = columSize;
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 0;

        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData == null || mData.isEmpty())
            return null;

        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mColumSize,
                    mColumSize));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        } else {
            imageView = (ImageView) convertView;
        }


        //imageView.setImageDrawable(new ColorDrawable(Color.BLUE));
        ResourceDrawable resourceDrawable;
        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof ResourceDrawable) {
            resourceDrawable = (ResourceDrawable) imageView.getDrawable();
        } else {
            resourceDrawable = new ResourceDrawable(mContext.getResources());
            imageView.setImageDrawable(resourceDrawable);
        }

        resourceDrawable.setImage(mData.get(position));

        return imageView;
    }


    public void clearSelf() {
        mContext = null;
        mData = null;
    }
}
