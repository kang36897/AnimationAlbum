package com.gulu.album;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gulu.album.fragment.SinglePhotoDetailFragment;
import com.gulu.album.item.EPhoto;
import com.gulu.album.view.DotView;
import com.gulu.album.view.PagerIndicator;

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

    private int mLastPosition;
    private LinearLayout mPageIndicator;
    private PagerIndicator mIndicator;


    private ViewPager.SimpleOnPageChangeListener simpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            DotView item = (DotView) mPageIndicator.getChildAt(mLastPosition);
           item.setCheckedState(false);

            mLastPosition = position;

            item = (DotView) mPageIndicator.getChildAt(mLastPosition);
            item.setCheckedState(true);
            item.setSelected(true);

            mIndicator.setCurrentPageByAnimation(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail_browser);

        mPhotoBrowser = (ViewPager) findViewById(R.id.photo_browser);
        mPhotoAdapter = new PhotoAdapter(getFragmentManager(), Arrays.asList(mData));
        mPhotoBrowser.setAdapter(mPhotoAdapter);

        mPhotoBrowser.addOnPageChangeListener(simpleOnPageChangeListener);
        mPageIndicator = (LinearLayout) findViewById(R.id.pager_indicators);
        for(int i = 0; i < mData.length; i++ ){
            DotView item = new DotView(this);
            mPageIndicator.addView(item, new LinearLayout.LayoutParams((int)getResources().getDisplayMetrics().density * 32, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        mLastPosition = mPhotoBrowser.getCurrentItem();

        mIndicator = (PagerIndicator) findViewById(R.id.pager_indicators2);
        mIndicator.setCurrentPage(mLastPosition);
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
