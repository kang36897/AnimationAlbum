package com.gulu.album.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gulu.album.R;
import com.gulu.album.graphics.ResourceDrawable;
import com.gulu.album.item.EPhoto;
import com.gulu.album.view.CanvasView;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SinglePhotoDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinglePhotoDetailFragment extends Fragment {

    private static final String TARGET_PHOTO = "target_photo";

    private static final String[] TEST_DATA = new String[]

            {
                    "fire a hole!", "fire a hole!", "fire a hole!", "fire a hole!",
            };

    private InputMethodManager mInputMethodManager;

    private EPhoto mEPhoto;

    private ImageView mBackBtn;

    private CanvasView mThumbnailView;

    private ListView mCommentsListView;
    private View mCommentsListHeaderView;
    private View mPhotoBoard;
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
    private TextView mBlinkBtn;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ePhoto
     * @return A new instance of fragment SinglePhotoDetailFragment.
     */

    public static SinglePhotoDetailFragment newInstance(EPhoto ePhoto) {
        SinglePhotoDetailFragment fragment = new SinglePhotoDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(TARGET_PHOTO, ePhoto);

        fragment.setArguments(args);
        return fragment;
    }

    public SinglePhotoDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEPhoto = getArguments().<EPhoto>getParcelable(TARGET_PHOTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_single_photo_detail, container, false);


        mBackBtn = (ImageView) fragmentView.findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        mThumbnailView = (CanvasView) fragmentView.findViewById(R.id.thumbnail);

        mCommentContainer = (ViewGroup) fragmentView.findViewById(R.id.comment_container);
        mPhotoComment = (EditText) fragmentView.findViewById(R.id.photo_comment);

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

        mSendComment = (Button) fragmentView.findViewById(R.id.send_comment_btn);
        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send the data to server


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

        mNoCommentHintContainer = (ViewGroup) fragmentView.findViewById(R.id.no_comment_hint_block_container);
        mNoCommentHint = (TextView) fragmentView.findViewById(R.id.no_comment_hint_block);
        mNoCommentHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentContainer.setVisibility(View.VISIBLE);
                mPhotoComment.requestFocus();

            }
        });


        mCommentsListView = (ListView) fragmentView.findViewById(R.id.comments_list);




        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCommentAdapter = new CommentAdapter<String>(getActivity(), new ArrayList<String>());
        mCommentsListHeaderView = getActivity().getLayoutInflater().inflate(R.layout.component_comment_list_header, mCommentsListView, false);

        mPhotoBoard = mCommentsListHeaderView.findViewById(R.id.photo_board);
        //TODO use a special Bitmap drawable
        ResourceDrawable mPhotoDrawable = new ResourceDrawable(getResources());
        mPhotoDrawable.setImage(mEPhoto.getmBitmpResId());
        mPhotoBoard.setBackgroundDrawable(mPhotoDrawable);
        mHotTagLabel = (ImageView) mCommentsListHeaderView.findViewById(R.id.hot_tag_label);
        mHotTag = (TextView) mCommentsListHeaderView.findViewById(R.id.hot_tag);
        mDescriptionView = (TextView) mCommentsListHeaderView.findViewById(R.id.photo_description);

        mCommentNum = (TextView) mCommentsListHeaderView.findViewById(R.id.comment_nums_text);
        ((ViewGroup)mCommentNum.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentContainer.setVisibility(View.VISIBLE);
                mPhotoComment.requestFocus();

            }
        });

        mBlinkBtn = (TextView)mCommentsListHeaderView.findViewById(R.id.blink_btn_text);
        ((ViewGroup)mBlinkBtn.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO send a blink to server


                int blinkCount = TextUtils.isEmpty(mBlinkBtn.getText()) ? 1 : 1 + Integer.parseInt(mBlinkBtn.getText().toString());
                mBlinkBtn.setText(Integer.toString(blinkCount));
            }
        });

        commentHeight = (int) (getResources().getDimension(R.dimen.common_list_item_height) + 0.5f);
        final View mRoot = getView();
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

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mInputMethodManager = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
