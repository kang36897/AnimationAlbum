package com.gulu.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;


public class NearByPhotoActivity extends BaseActivity {

    private Gallery mNearbyGallery;
    private ImageAdapter<Integer> mGalleryAdapter;

    private final static Integer[] mTestData = {
            R.drawable.gallery_photo_1,
            R.drawable.gallery_photo_2,
            R.drawable.gallery_photo_3,
            R.drawable.gallery_photo_4,
            R.drawable.gallery_photo_5,
            R.drawable.gallery_photo_6,
            R.drawable.gallery_photo_7,
            R.drawable.gallery_photo_8
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_photo);

        mNearbyGallery = (Gallery) findViewById(R.id.nearby_gallery);
        final ViewGroup mGallyerContainer = (ViewGroup) findViewById(R.id.gallery_container);
        mGallyerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                int size = mGallyerContainer.getHeight();
                mGalleryAdapter = new ImageAdapter<Integer>(NearByPhotoActivity.this,
                        Arrays.asList(mTestData), size, size);
                mNearbyGallery.setAdapter(mGalleryAdapter);
                mGallyerContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_near_by_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class ImageAdapter<T> extends BaseAdapter {
        private Context mContext;
        //  private LayoutInflater mInflater;

        private int mItemWidth;
        private int mItemHeight;


        private List<T> mData;


        public ImageAdapter(Context context, List<T> data, int itemWidth, int itemHeight) {
            mContext = context;
            //mInflater = LayoutInflater.from(mContext);
            mData = data;

            mItemWidth = itemWidth;
            mItemHeight = itemHeight;


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
            ImageView imgView;
            if (convertView == null) {
                imgView = new ImageView(mContext);
                imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                imgView.setLayoutParams(new Gallery.LayoutParams(mItemWidth, mItemHeight));

            } else {
                imgView = (ImageView) convertView;
            }

            imgView.setImageResource((Integer) mData.get(position));

            return imgView;
        }
    }
}
