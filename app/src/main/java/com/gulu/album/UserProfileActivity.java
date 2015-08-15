package com.gulu.album;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gulu.album.graphics.ResourceDrawable;
import com.gulu.album.graphics.SBitmapDrawable;
import com.gulu.album.view.HeaderGridView;

import java.util.Arrays;
import java.util.List;

public class UserProfileActivity extends BaseActivity implements OnMapReadyCallback {


    private final static Integer[] DATA = new Integer[]{
            R.drawable.football_girl, R.drawable.sword_girl, R.drawable.flower_girl,
            R.drawable.feeling_gril, R.drawable.boobs_girl,
            R.drawable.pink_girl, R.drawable.max_girl, R.drawable.nice_boobs,
            R.drawable.zhangmin1, R.drawable.zhangmin2, R.drawable.feeling_gril,
            R.drawable.boobs_girl,
            R.drawable.pink_girl, R.drawable.max_girl, R.drawable.nice_boobs,
            R.drawable.zhangmin1, R.drawable.zhangmin2
    };

    private HeaderGridView mPhotoGallery;
    private PhotoAdapter mPhotoAdapter;

    private View mProfileHeader;
    private ImageView mUserThumbnail;
    private Button mFollowingBtn;

    private MapView mMapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mPhotoGallery = (HeaderGridView) findViewById(R.id.photo_gallery);

        mProfileHeader = getLayoutInflater().inflate(R.layout.component_user_profile_header, mPhotoGallery, false);
        mUserThumbnail = (ImageView) mProfileHeader.findViewById(R.id.user_thumbnail);
        SBitmapDrawable drawable = new SBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.football_girl));
        drawable.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        drawable.setBorderColor(Color.WHITE);
        drawable.setBorderSize(2);
        drawable.setAntiAlias(true);
        drawable.setDither(true);
        drawable.setmDrawOperationWithShader(new SBitmapDrawable.DrawCircleImageWithShader());
        mUserThumbnail.setImageDrawable(drawable);
        mFollowingBtn = (Button) mProfileHeader.findViewById(R.id.following_btn);
        mFollowingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMapView = (MapView) mProfileHeader.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);


        mPhotoGallery.addHeaderView(mProfileHeader);

        mPhotoGallery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int columnSize = (mPhotoGallery.getWidth() - mPhotoGallery.getPaddingLeft() - mPhotoGallery.getPaddingRight()) / mPhotoGallery.getNumColumns();
                mPhotoGallery.setColumnWidth(columnSize);
                mPhotoAdapter = new PhotoAdapter(getBaseContext(), Arrays.asList(DATA), columnSize);
                mPhotoGallery.setAdapter(mPhotoAdapter);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {

        if (mUserThumbnail != null) {
            mUserThumbnail.setImageDrawable(null);
            mUserThumbnail = null;
        }

        if (mFollowingBtn != null) {
            mFollowingBtn.setOnClickListener(null);
            mFollowingBtn = null;
        }


        if (mPhotoGallery != null) {
            mPhotoGallery.setAdapter(null);
            mPhotoGallery.removeHeaderView(mProfileHeader);
            mPhotoGallery = null;
        }

        if (mPhotoAdapter != null) {
            mPhotoAdapter.clearSelf();
            mPhotoAdapter = null;
        }

        mProfileHeader = null;

        mMap = null;
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng initViewPoint = new LatLng(31.175044, 121.410621);
        mMap.addMarker(new MarkerOptions().position(initViewPoint).title("I am Here!"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initViewPoint, 16));

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
}
