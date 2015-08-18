package com.gulu.album;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gulu.album.general.PhotoAdapter;
import com.gulu.album.view.HeaderGridView;

import java.util.Arrays;

public class HotProfileActivity extends BaseActivity implements OnMapReadyCallback {

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
    private Button mFollowingBtn;

    private MapView mMapView;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_profile);

        mPhotoGallery = (HeaderGridView) findViewById(R.id.photo_gallery);
        mProfileHeader = getLayoutInflater().inflate(R.layout.component_hot_profile_header, mPhotoGallery, false);

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


        ImageView mBackBtn = (ImageView) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

}
