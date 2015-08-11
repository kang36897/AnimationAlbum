package com.gulu.album.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.gulu.album.R;

/**
 * Created by lulala on 11/8/15.
 */
public class BorderLayout extends ViewGroup {

    public final static int LEFT_RIGHT_COVER = 0x01;
    public final static int TOP_BOTTOM_COVER = 0x20;

    private int mLayoutMode;

    public BorderLayout(Context context) {
        this(context, null);
    }

    public BorderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BorderLayout);
        mLayoutMode = a.getInt(R.styleable.BorderLayout_bl_mode, LEFT_RIGHT_COVER);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof  BorderLayout.LayoutParams;
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        if(mLayoutMode == LEFT_RIGHT_COVER){
            return new BorderLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }else{
            return new BorderLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, LayoutParams.NORTH);
        }



    }


    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams{



        public final static int NORTH = 0x20;
        public final static int EAST = 0x10;
        public final static int SOUTH = 0x21;
        public final static int WEST = 0x11;
        public final static int CENTER = 0x00;





        private int mode = LEFT_RIGHT_COVER;
        private int postion;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.BorderLayout_layout);
            this.postion = a.getInt(R.styleable.BorderLayout_layout_bl_position, EAST);
            a.recycle();

        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.postion = EAST;
        }

        public LayoutParams(int width, int height, int postion){
            super(width, height);
            this.postion = postion;
        }


        public LayoutParams(LayoutParams source) {
            super(source);

            this.postion = source.postion;
        }

        public LayoutParams(ViewGroup.LayoutParams params){
            super(params);
            this.postion = EAST;
        }


        public LayoutParams(MarginLayoutParams source) {
            super(source);
            this.postion = EAST;
        }
    }
}
