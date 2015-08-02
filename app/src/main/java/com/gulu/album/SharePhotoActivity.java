package com.gulu.album;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class SharePhotoActivity extends BaseActivity {


    // use target photo as background
    private LinearLayout mTargetPhotoView;
    private Button mBackBtn;

    private EditText mDescriptionView;
    private TextView mAddEventView;
    private TextView mAddPointView;

    private Button mShareBtn;

    private static String[] mTestData = new String[]{"Fire Man","Striper","Hooker","Snow White","What's the time?"};

    private HotTagChooser mHotTogChooser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photo);

        //  handle the intent here
        setActivityView();

    }

    void setActivityView() {
        mHotTogChooser = new HotTagChooser(this);

        mTargetPhotoView = (LinearLayout)findViewById(R.id.target_photo);
        mBackBtn = (Button)findViewById(R.id.back_btn);

        mDescriptionView = (EditText)findViewById(R.id.description);
        mAddEventView = (TextView)findViewById(R.id.add_hot_event_btn);
        mAddPointView = (TextView)findViewById(R.id.add_hot_point_btn);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAddEventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHotTogChooser.isShowing()){
                    mHotTogChooser.dismiss();
                }

                mHotTogChooser.showTagChoosenPopUp(mAddEventView, HotTagType.Hot_Event, Arrays.<String>asList(mTestData));
            }
        });

        mAddPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHotTogChooser.isShowing()){
                    mHotTogChooser.dismiss();
                }

                mHotTogChooser.showTagChoosenPopUp(mAddPointView,HotTagType.Hot_Point,Arrays.<String>asList(mTestData));
            }
        });

        mShareBtn = (Button)findViewById(R.id.share_btn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share the photo here
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        return super.dispatchKeyEvent(event);
    }

    public static class HotTagChooser {



        private Context mContext;
        private LayoutInflater mInflater;

        private View mRootView;

        private FrameLayout mAddHotItemArea;
        private TextView mCreateHotTagBtn;

        private LinearLayout mEnterHotTagArea;
        private EditText mHotTagEditView;

        private ListView mHotTagList;


        private HotTagType mTargeType;
        private TextView mTargetHotTag;

        private PopupWindow mPopupWindow;
        private int mWindowWidth;
        private int mWindowHeight;

        public HotTagChooser(Context context){
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mRootView = mInflater.inflate(R.layout.component_add_hot_item, new LinearLayout(mContext), false);
            ViewGroup.LayoutParams parameters  = mRootView.getLayoutParams();
            mWindowWidth = parameters.width;
            mWindowHeight = parameters.height;

            mAddHotItemArea = (FrameLayout)mRootView.findViewById(R.id.add_hot_item_area);
            mCreateHotTagBtn = (TextView)mRootView.findViewById(R.id.create_new_item_btn);
            mEnterHotTagArea = (LinearLayout)mRootView.findViewById(R.id.enter_new_item_area);
            mHotTagEditView = (EditText)mRootView.findViewById(R.id.new_hot_item_edit);

            mCreateHotTagBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.INVISIBLE);
                    mEnterHotTagArea.setVisibility(View.VISIBLE);
                }
            });


            mHotTagList = (ListView)mRootView.findViewById(R.id.hot_list);
            mHotTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (mTargetHotTag == null) {
                        return;
                    }

                    //TODO use the type to select different type
                    String tag = (String) parent.getItemAtPosition(position);
                    mTargetHotTag.setText(tag);

                    dismiss();
                }
            });

            mHotTagEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if(actionId != EditorInfo.IME_ACTION_DONE){
                        return handled;
                    }


                    if(TextUtils.isEmpty(mHotTagEditView.getText())){

                        return handled;
                    }

                    mTargetHotTag.setText(mHotTagEditView.getText());
                    handled = true;

                    dismiss();
                    return handled;
                }
            });

        }

        public <T> void  showTagChoosenPopUp(TextView targetView,HotTagType type, List<T> mData ){


            mTargetHotTag = targetView;
            mTargeType = type;
            switch(type){
                case Hot_Event:
                    mCreateHotTagBtn.setText("Create New Event");
                    mCreateHotTagBtn.setTextColor(Color.parseColor("#00b900"));
                    mHotTagList.setAdapter(new HotTagAdapter<T>(mData,HotTagType.Hot_Event,mInflater));
                    break;

                case Hot_Point:
                    mCreateHotTagBtn.setText("Create New Point");
                    mCreateHotTagBtn.setTextColor(Color.parseColor("#fe0000"));
                    mHotTagList.setAdapter(new HotTagAdapter<T>(mData, HotTagType.Hot_Point, mInflater));
                    break;

                default:
                    break;
            }


            mPopupWindow = new PopupWindow(mRootView,mWindowWidth, mWindowHeight, true);
            mPopupWindow.setTouchable(true);
            // if you want to dismiss the popup window, just set a drawable background for it.
            mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.gray_round_rect_white_bg));

            int[] anchorLocation = new int[2];
            mTargetHotTag.getLocationOnScreen(anchorLocation);
            int anchorCenterX = anchorLocation[0] + (int)(mTargetHotTag.getWidth() * 0.5f);

            int y = anchorLocation[1] - mWindowHeight;
            int x = anchorCenterX - (int)(mWindowWidth * 0.5f);

            x = x > 0 ? x :(int)(mContext.getResources().getDisplayMetrics().density * 16 + 0.5f);
            mPopupWindow.showAtLocation((ViewGroup) mTargetHotTag.getParent(), Gravity.TOP | Gravity.LEFT,x, y);

        }

        public boolean isShowing(){
            if(mPopupWindow == null){
                return false;
            }

            return mPopupWindow.isShowing();
        }

        public void dismiss(){
            if(mPopupWindow == null){
                return;
            }

            mPopupWindow.dismiss();
            mPopupWindow = null;
        }



        class HotTagAdapter<T> extends BaseAdapter{
            private LayoutInflater mInflater;
            private List<T> mData;
            private HotTagType mType;

            public HotTagAdapter(List<T> data,HotTagType type,LayoutInflater inflater){
                mData = data;
                mType = type;
                mInflater = inflater;
            }

            @Override
            public int getCount() {
                if(mData == null)
                return 0;

                return mData.size();
            }

            @Override
            public Object getItem(int position) {
                if(mData == null)
                return null;

                return mData.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                HotTagViewHolder holder;
                if(convertView == null){

                    convertView = mInflater.inflate(R.layout.component_hot_list_item,parent,false);
                    holder = new HotTagViewHolder();
                    holder.mHotTagLabel = (ImageView)convertView.findViewById(R.id.hot_tag_label);
                    holder.mHotTagView = (TextView)convertView.findViewById(R.id.hot_tag);
                    convertView.setTag(holder);

                }else{

                   holder = (HotTagViewHolder)convertView.getTag();
                }



                if(mType == HotTagType.Hot_Event){
                    holder.mHotTagLabel.setImageResource(R.drawable.hotevent);

                }else{
                    holder.mHotTagLabel.setImageResource(R.drawable.hotpoint);
                }
                holder.mHotTagView.setText(mData.get(position).toString());

                return convertView;
            }
        }

        static class HotTagViewHolder{
            public ImageView mHotTagLabel;
            public TextView mHotTagView;
        }




    }


    public enum HotTagType{ Hot_Event, Hot_Point}
}
