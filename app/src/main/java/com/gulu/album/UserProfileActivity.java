package com.gulu.album;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gulu.album.view.HeaderGridView;

import java.util.Arrays;
import java.util.List;

public class UserProfileActivity extends BaseActivity {


    private final static Integer[] DATA = new Integer[]{
            R.drawable.football_girl, R.drawable.sword_girl, R.drawable.flower_girl,
            R.drawable.feeling_gril, R.drawable.boobs_girl,
            R.drawable.pink_girl
    };

    private HeaderGridView mPhotoGallery;
    private PhotoAdapter mPhotoAdapter ;

    private View mProfileHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mPhotoGallery = (HeaderGridView) findViewById(R.id.photo_gallery);

        mProfileHeader = getLayoutInflater().inflate(R.layout.component_user_profile_header, mPhotoGallery, false);
        mPhotoGallery.addHeaderView(mProfileHeader);

        mPhotoGallery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int columnSize = (mPhotoGallery.getWidth() - mPhotoGallery.getPaddingLeft() - mPhotoGallery.getPaddingRight())/ mPhotoGallery.getNumColumns();
                mPhotoGallery.setColumnWidth(columnSize);
                mPhotoAdapter = new PhotoAdapter(getBaseContext(), Arrays.asList(DATA), columnSize);
                mPhotoGallery.setAdapter(mPhotoAdapter);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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


    public static class PhotoAdapter extends BaseAdapter {

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


            }else{
                imageView =(ImageView) convertView;
            }


            //imageView.setImageDrawable(new ColorDrawable(Color.BLUE));

            imageView.setImageResource(mData.get(position));


            return imageView;
        }
    }
}
