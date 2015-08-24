package com.gulu.album;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.gulu.album.fragment.SinglePhotoDetailFragment;
import com.gulu.album.item.EPhoto;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/7/31.
 */
public class PhotoDetailBrowserActivity extends BaseActivity {


    private ViewPager mPhotoBrowser;
    private PhotoAdapter mPhotoAdapter;
    private EPhoto[] mData = new EPhoto[]{
            new EPhoto(R.drawable.boobs_girl),
            new EPhoto(R.drawable.football_girl),
            new EPhoto(R.drawable.max_girl),
            new EPhoto(R.drawable.flower_girl)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail_browser);

        mPhotoBrowser = (ViewPager) findViewById(R.id.photo_browser);
        mPhotoAdapter = new PhotoAdapter(getFragmentManager(), Arrays.asList(mData));
        mPhotoBrowser.setAdapter(mPhotoAdapter);
    }


    public static class PhotoAdapter extends FragmentStatePagerAdapter {
        private List<EPhoto> mPhotoData;

        public PhotoAdapter(FragmentManager fm, List<EPhoto> data) {
            super(fm);
            mPhotoData = data;
        }

        @Override
        public Fragment getItem(int position) {


            return SinglePhotoDetailFragment.newInstance(mPhotoData.get(position));
        }

        @Override
        public int getCount() {
            if (mPhotoData == null)
                return 0;

            return mPhotoData.size();
        }
    }
}
