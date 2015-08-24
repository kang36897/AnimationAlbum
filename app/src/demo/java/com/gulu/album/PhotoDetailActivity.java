package com.gulu.album;

import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gulu.album.view.CanvasView;

import java.util.ArrayList;
import java.util.List;


public class PhotoDetailActivity extends BaseActivity {

    private Button mBackBtn;

    private CanvasView mThumbnailView;

    private ListView mCommentsListView;
    private View mCommentsListHeaderView;
    private CommentAdapter mCommentAdapter;
    private int commentHeight;

    private TextView mDescriptionView;
    private ImageView mHotTagLabel;
    private TextView mHotTag;

    private ViewGroup mNoCommentHintContainer;
    private TextView mNoCommentHint;

    private ViewGroup mCommentContainer;
    private EditText mPhotoComment;
    private Button mSendComment;

    private TextView mCommentNum;

    private static final String[] TEST_DATA = new String[]

            {
                    "fire a hole!", "fire a hole!", "fire a hole!", "fire a hole!",
            };


    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final View mRoot = (View) findViewById(Window.ID_ANDROID_CONTENT);

        mThumbnailView = (CanvasView) findViewById(R.id.thumbnail);

        mHotTagLabel = (ImageView) findViewById(R.id.hot_tag_label);
        mHotTag = (TextView) findViewById(R.id.hot_tag);
        mDescriptionView = (TextView) findViewById(R.id.photo_description);

        mCommentContainer = (ViewGroup) findViewById(R.id.comment_container);
        mPhotoComment = (EditText) findViewById(R.id.photo_comment);

        mPhotoComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (hasFocus == true) {
                    mNoCommentHintContainer.setVisibility(View.GONE);
                    mInputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mCommentContainer.setVisibility(View.GONE);
                    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS, new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            super.onReceiveResult(resultCode, resultData);
                            if (resultCode == InputMethodManager.RESULT_HIDDEN) {

                                if (mCommentAdapter == null) {
                                    return;
                                }

                                if (mCommentAdapter.getCount() == 0) {
                                    mNoCommentHintContainer.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    });


                }
            }
        });

        mSendComment = (Button) findViewById(R.id.send_comment_btn);
        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send the data to server

                Dialog d;
                mCommentAdapter.addComment("what's wrong with you?");
                mCommentContainer.setVisibility(View.GONE);
                mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);
                        if (resultCode == InputMethodManager.RESULT_HIDDEN) {

                            if (mCommentAdapter == null) {
                                return;
                            }

                            if (mCommentAdapter.getCount() == 0) {
                                mNoCommentHintContainer.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
                mPhotoComment.setText(null);
            }
        });

        mNoCommentHintContainer = (ViewGroup) findViewById(R.id.no_comment_hint_block_container);
        mNoCommentHint = (TextView) findViewById(R.id.no_comment_hint_block);
        mNoCommentHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentContainer.setVisibility(View.VISIBLE);
                mPhotoComment.requestFocus();

            }
        });


        mCommentsListView = (ListView) findViewById(R.id.comments_list);
        mCommentAdapter = new CommentAdapter<String>(this, new ArrayList<String>());
        mCommentsListHeaderView = getLayoutInflater().inflate(R.layout.component_comment_list_header, mCommentsListView, false);
        commentHeight = (int) (getResources().getDimension(R.dimen.common_list_item_height) + 0.5f);
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mCommentsListHeaderView.getLayoutParams().height = mRoot.getHeight() - commentHeight;
                mCommentsListView.addHeaderView(mCommentsListHeaderView);
                mCommentsListView.setAdapter(mCommentAdapter);
            }
        });

        mCommentAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mCommentNum.setText("" + mCommentAdapter.getCount());
            }

            @Override
            public void onInvalidated() {

            }
        });

        mCommentNum = (TextView) mCommentsListHeaderView.findViewById(R.id.comment_nums_text);
        mCommentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentContainer.setVisibility(View.VISIBLE);
                mPhotoComment.requestFocus();

            }
        });
    }


    public static class CommentAdapter<T> extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        private List<T> mCommentsData;

        public CommentAdapter(Context context, List<T> data) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);

            mCommentsData = data;
        }

        public void addComment(T item) {
            if (mCommentsData == null) {
                mCommentsData = new ArrayList<T>();
            }

            mCommentsData.add(item);
            //TODO sort comments by timestamp

            notifyDataSetChanged();
        }


        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

        @Override
        public int getCount() {
            if (mCommentsData == null)
                return 0;

            return mCommentsData.size();
        }

        @Override
        public Object getItem(int position) {
            if (mCommentsData == null) {
                return null;
            }

            return mCommentsData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentViewHolder holder = new CommentViewHolder();
            if (convertView == null) {
                holder = new CommentViewHolder();
                convertView = mInflater.inflate(R.layout.component_comment_list_item, parent, false);
                holder.mThumbnailView = convertView.findViewById(R.id.item_thumbnail);
                holder.mVisiter = (TextView) convertView.findViewById(R.id.item_visitor);
                holder.mComment = (TextView) convertView.findViewById(R.id.item_comment);
                holder.mPastTime = (TextView) convertView.findViewById(R.id.item_pasttime);
                convertView.setTag(holder);
            } else {
                holder = (CommentViewHolder) convertView.getTag();
            }

            //TODO do some other data binding
            bindData(position, holder);


            return convertView;
        }

        private void bindData(int position, CommentViewHolder holder) {
            T t = mCommentsData.get(position);
            holder.mComment.setText(t.toString());
        }


        static class CommentViewHolder {
            public View mThumbnailView;
            public TextView mVisiter;
            public TextView mComment;
            public TextView mPastTime;
        }
    }


}
