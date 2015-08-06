package com.gulu.album.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;


/**
 * Created by Lulala on 2015/8/3.
 */
public class SBitmapDrawable extends Drawable {
    private static final int DEFAULT_PAINT_FLAGS =
            Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;
    private final Rect mDstRect = new Rect();
    private SBitmapState mBitmapState;

    private Bitmap mBitmap;

    private int mTargetDensity;

    private boolean mApplyGravity;
    private boolean mMutated;

    private Matrix mMirrorMatrix;


    private int mBitmapWidth;
    private int mBitmapHeight;

    public int getGravity() {
        return mBitmapState.mGravity;
    }

    public void setGravity(int gravity) {
        if (mBitmapState.mGravity != gravity) {
            mBitmapState.mGravity = gravity;
            mApplyGravity = true;
            invalidateSelf();
        }
    }

    public SBitmapDrawable(Resources res, Bitmap bitmap) {
        this(new SBitmapState(bitmap), res);
        mBitmapState.mTargetDensity = mTargetDensity;
    }


    public Paint getPaint() {
        return mBitmapState.mShaderPaint;
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(int density) {
        if (mTargetDensity != density) {
            mTargetDensity = density == 0 ? DisplayMetrics.DENSITY_DEFAULT : density;
            if (mBitmap != null) {
                computeBitmapSize();
            }
            invalidateSelf();
        }


    }


    public void setAntiAlias(boolean aa) {
        mBitmapState.mShaderPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    public boolean hasMipMap() {
        return mBitmapState.mBitmap != null && mBitmapState.mBitmap.hasMipMap();
    }

    public void setTargetDensity(DisplayMetrics metrics) {
        setTargetDensity(metrics.densityDpi);
    }

    private void computeBitmapSize() {
        mBitmapWidth = mBitmap.getScaledWidth(mTargetDensity);
        mBitmapHeight = mBitmap.getScaledHeight(mTargetDensity);
    }


    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap input) {
        if (input != mBitmap) {
            mBitmap = input;
            if (input != null) {
                computeBitmapSize();
            } else {
                mBitmapWidth = mBitmapHeight = -1;
            }
            invalidateSelf();
        }
    }


    public boolean hasAntiAlias() {
        return mBitmapState.mShaderPaint.isAntiAlias();
    }

    @Override
    public void draw(Canvas canvas) {

        Bitmap bitmap = mBitmap;
        if (bitmap != null) {
            final SBitmapState state = mBitmapState;
            if (state.mRebuildShader) {
                Shader.TileMode tmx = state.mTileModeX;
                Shader.TileMode tmy = state.mTileModeY;

                if (tmx == null && tmy == null) {
                    state.mShaderPaint.setShader(null);
                } else {
                    state.mShaderPaint.setShader(new BitmapShader(bitmap,
                            tmx == null ? Shader.TileMode.CLAMP : tmx,
                            tmy == null ? Shader.TileMode.CLAMP : tmy));
                }
                state.mRebuildShader = false;
                copyBounds(mDstRect);
            }

            Shader shader = state.mShaderPaint.getShader();
            final boolean needMirroring = needMirroring();
            if (shader == null) {
                if (mApplyGravity) {

                    Gravity.apply(state.mGravity, mBitmapWidth, mBitmapHeight,
                            getBounds(), mDstRect);
                    mApplyGravity = false;
                }
                if (needMirroring) {
                    canvas.save();
                    // Mirror the bitmap
                    canvas.translate(mDstRect.right - mDstRect.left, 0);
                    canvas.scale(-1.0f, 1.0f);
                }
                canvas.drawBitmap(bitmap, null, mDstRect, state.mShaderPaint);
                if (needMirroring) {
                    canvas.restore();
                }
            } else {
                if (mApplyGravity) {
                    copyBounds(mDstRect);
                    mApplyGravity = false;
                }

                if (needMirroring) {
                    // Mirror the bitmap
                    updateMirrorMatrix(mDstRect.right - mDstRect.left);
                    shader.setLocalMatrix(mMirrorMatrix);
                } else {
                    if (mMirrorMatrix != null) {
                        mMirrorMatrix = null;
                        shader.setLocalMatrix(null);
                    }
                }

                if (mDrawOperationWithShader == null) {
                    canvas.drawRect(mDstRect, state.mShaderPaint);
                } else {
                    Rect dstRect = new Rect();
                    copyBounds(dstRect);
                    mDrawOperationWithShader.doDrawOperation(canvas, state.mShaderPaint, state.mBitmap.getWidth(), state.mBitmap.getHeight(), dstRect, mTargetDensity, state.mGravity, state.mImageSlice, state.mBorderPaint, state.mBorderSize);

                }
            }
        }
    }

    public SBitmapDrawable.DrawOperationWithShader getmDrawOperationWithShader() {
        return mDrawOperationWithShader;
    }

    public void setmDrawOperationWithShader(SBitmapDrawable.DrawOperationWithShader mDrawOperationWithShader) {
        this.mDrawOperationWithShader = mDrawOperationWithShader;
    }

    private ImageSlice mImageSlice;

    public void setImageSliceToBeShader(ImageSlice imageSlice) {
        ImageSlice old = mBitmapState.mImageSlice;
        if (old == imageSlice) {
            return;
        }

        mBitmapState.mImageSlice = imageSlice;
        invalidateSelf();
    }


    public enum ImageSlice {
        HEAD, MIDDLE, TAIL
    }


    private DrawOperationWithShader mDrawOperationWithShader;

    public interface DrawOperationWithShader {
        void doDrawOperation(Canvas canvas, Paint shaderPaint, int bitmapWidth, int bitmapHeight, Rect dstRect, int desity, int gravity, ImageSlice mImageSlice, Paint borderPaint, float borderSize);
    }

    public boolean isAutoMirrored() {
        return mBitmapState.mAutoMirrored;
    }


    private boolean needMirroring() {
        return isAutoMirrored();
    }

    private void updateMirrorMatrix(float dx) {
        if (mMirrorMatrix == null) {
            mMirrorMatrix = new Matrix();
        }
        mMirrorMatrix.setTranslate(dx, 0);
        mMirrorMatrix.preScale(-1.0f, 1.0f);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mApplyGravity = true;
        Shader shader = mBitmapState.mShaderPaint.getShader();
        if (shader != null) {
            if (needMirroring()) {
                updateMirrorMatrix(bounds.right - bounds.left);
                shader.setLocalMatrix(mMirrorMatrix);
            } else {
                if (mMirrorMatrix != null) {
                    mMirrorMatrix = null;
                    shader.setLocalMatrix(null);
                }
            }
        }
    }


    @Override
    public int getAlpha() {
        return mBitmapState.mShaderPaint.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        int oldAlpha = mBitmapState.mShaderPaint.getAlpha();
        if (alpha != oldAlpha) {
            mBitmapState.mShaderPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    public void setXfermode(Xfermode xfermode) {
        mBitmapState.mShaderPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mBitmapState.mShaderPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        mBitmapState.mShaderPaint.setDither(dither);
        invalidateSelf();
    }


    public Shader.TileMode getTileModeX() {
        return mBitmapState.mTileModeX;
    }


    public Shader.TileMode getTileModeY() {
        return mBitmapState.mTileModeY;
    }


    public void setTileModeX(Shader.TileMode mode) {
        setTileModeXY(mode, mBitmapState.mTileModeY);
    }


    public final void setTileModeY(Shader.TileMode mode) {
        setTileModeXY(mBitmapState.mTileModeX, mode);
    }


    public void setTileModeXY(Shader.TileMode xmode, Shader.TileMode ymode) {
        final SBitmapState state = mBitmapState;
        if (state.mTileModeX != xmode || state.mTileModeY != ymode) {
            state.mTileModeX = xmode;
            state.mTileModeY = ymode;
            state.mRebuildShader = true;
            invalidateSelf();
        }
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        if (mBitmapState.mAutoMirrored != mirrored) {
            mBitmapState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mBitmapState = new SBitmapState(mBitmapState);
            mMutated = true;
        }
        return this;
    }


    @Override
    public void setColorFilter(ColorFilter cf) {
        mBitmapState.mShaderPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return 0;
    }


    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mBitmapState.mChangingConfigurations;
    }

    @Override
    public ConstantState getConstantState() {
        mBitmapState.mChangingConfigurations = getChangingConfigurations();
        return mBitmapState;

    }

    public void setBorderSize(int borderSize) {


        ensureBorderPaint();
        mBitmapState.mBorderSize = (int) ((mBitmapState.mDisplayMetric == null ? 1 : mBitmapState.mDisplayMetric.density) * borderSize);
        mBitmapState.mBorderPaint.setStrokeWidth(mBitmapState.mBorderSize);
        invalidateSelf();
    }

    private void ensureBorderPaint() {
        if (mBitmapState.mBorderPaint == null) {
            Paint temp = new Paint();
            temp.setAntiAlias(true);
            temp.setStyle(Paint.Style.STROKE);
            mBitmapState.mBorderPaint = temp;

        }
    }


    public void setBorderColor(int color) {
        ensureBorderPaint();
        mBitmapState.mBorderColor = color;
        mBitmapState.mBorderPaint.setColor(color);
        invalidateSelf();
    }

    public static class SBitmapState extends ConstantState {


        Bitmap mBitmap;
        int mChangingConfigurations;
        public int mGravity = Gravity.FILL;
        Paint mShaderPaint = new Paint(DEFAULT_PAINT_FLAGS);
        Paint mBorderPaint;
        int mBorderSize;
        int mBorderColor;
        DisplayMetrics mDisplayMetric;
        Shader.TileMode mTileModeX = null;
        Shader.TileMode mTileModeY = null;

        int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        public boolean mRebuildShader;
        public boolean mAutoMirrored;
        public ImageSlice mImageSlice = ImageSlice.HEAD;


        public SBitmapState(SBitmapState bitmapState) {
            this(bitmapState.mBitmap);
            mChangingConfigurations = bitmapState.mChangingConfigurations;
            mGravity = bitmapState.mGravity;
            mTileModeX = bitmapState.mTileModeX;
            mTileModeY = bitmapState.mTileModeY;
            mTargetDensity = bitmapState.mTargetDensity;
            mShaderPaint = new Paint(bitmapState.mShaderPaint);
            mRebuildShader = bitmapState.mRebuildShader;
            mAutoMirrored = bitmapState.mAutoMirrored;
            mDisplayMetric = bitmapState.mDisplayMetric;

        }


        public SBitmapState(Bitmap bitmap) {
            mBitmap = bitmap;
        }


        @Override
        public Drawable newDrawable() {
            return new SBitmapDrawable(this, null);
        }


        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    private SBitmapDrawable(SBitmapState state, Resources res) {
        mBitmapState = state;
        if (res != null) {
            mTargetDensity = res.getDisplayMetrics().densityDpi;
            mBitmapState.mDisplayMetric = res.getDisplayMetrics();
        } else {
            mTargetDensity = state.mTargetDensity;
        }
        setBitmap(state != null ? state.mBitmap : null);
        computeBitmapSize();

    }


    public static class DrawCircleImageWithShader implements DrawOperationWithShader {

        @Override
        public void doDrawOperation(Canvas canvas, Paint shaderPaint, int bitmapWidth, int bitmapHeight, Rect dstRect, int desity, int gravity, ImageSlice imageSlice, Paint borderPaint, float borderSize) {


            int vw = dstRect.width();
            int vh = dstRect.height();

            Matrix mShaderMatrix = new Matrix();

            float size;

            float scale;
            float dx = 0;
            float dy = 0;

            // the width and height of bitmap after projected into the view coordinate
            float acw;
            float ach;
            float aOffsetX = 0;
            float aOffsetY = 0;

            // compute the container , Because the image will be in a circle , the width and height of container should be equal.
            vw = Math.min(vw, vh);
            vh = vw;

            Rect outRect = new Rect();
            Gravity.apply(gravity, vw, vh, dstRect, outRect);

            if (bitmapWidth * vh < vw * bitmapHeight) {
                //the width is equal after projection
                scale = (float) vw / (float) bitmapWidth;
                acw = vw;

                // after the projection the drawable 's height
                ach = bitmapHeight * scale;

                // the circle size after the projection
                size = Math.min(acw, ach);

                // use the smallest side as the size of circle
                //size = Math.min(vh, size);

                dy = (vh - ach) * 0.5f;

            } else {
                // the height is equal after projection
                scale = (float) vh / (float) bitmapHeight;

                ach = vh;
                acw = bitmapWidth * scale;
                // the circle size after the projection
                size = Math.min(acw, ach);

                // use the smallest side as the size of circle
                // size = Math.min(vh, size);

                dx = (vw - acw) * 0.5f;
            }

            mShaderMatrix.setScale(scale, scale);

            switch (imageSlice) {

                case HEAD:
                    /**
                     *   if you want the head part of this image to be the shader,just use the follow codes:
                     */
                    aOffsetX = (acw - size) * 0.5f;
                    aOffsetY = (ach - size) * 0.5f;
                    break;

                case TAIL:
                    /**
                     *  if you want the tail part of thie image to be the shader ,just use the follow codes:
                     */
                    aOffsetX = (size - acw) * 0.5f;
                    aOffsetY = (size - ach) * 0.5f;
                    break;

                // offset of the center of shader relative to the image
                /** if (aOffsetX == 0 && aOffsetY == 0) ,the center of the shader  is in the center of image.
                 * So the center part of this image will be used as shader;
                 *
                 */
                default:
                case MIDDLE:
                    aOffsetX = 0;
                    aOffsetY = 0;
                    break;
            }


            //Note, the center of bitmap is equal to the center of view.

            // if the height of the bitmap is larger than the height of view after transformed to the view coordinate,
            // adjust the dy offset to 0
            dy = dy + aOffsetY;
            dx = dx + aOffsetX;

            mShaderMatrix.postTranslate(dx, dy);


            canvas.save();
            canvas.translate(outRect.left, outRect.top);


            shaderPaint.getShader().setLocalMatrix(mShaderMatrix);
            RectF oval = new RectF(0, 0, vw, vh);


            canvas.drawOval(oval, shaderPaint);
            if (borderSize > 0) {
                oval.inset(borderSize * 0.5f, borderSize / 2);
                canvas.drawOval(oval, borderPaint);

            }


            canvas.restore();

                /*if (borderSize > 0 ) {
                    outRect.inset((int)(borderSize * 0.5f), (int)(borderSize/2));
                    canvas.drawOval(new RectF(outRect), borderPaint);

                }*/
        }

    }
}
