package com.kevintan.viewflipperindicator;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ViewFlipper;

/**
 * Created by kevintan on 9/11/13.
 */
public class UnderlinePageIndicator extends View {

    private int mTouchSlop;
    ViewFlipper viewFlipper;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean mFades;
    private int mFadeDelay;
    private int mFadeLength;
    private int mFadeBy;
    private static final int FADE_FRAME_MS = 30;

    int currentDisplayedChild;

    public UnderlinePageIndicator(Context context) {
        super(context);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.vfiUnderlinePageIndicatorStyle);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) return;

        final Resources res = getResources();
        //Load defaults from resources
        final boolean defaultFades = res.getBoolean(R.bool.default_underline_indicator_fades);
        final int defaultFadeDelay = res.getInteger(R.integer.default_underline_indicator_fade_delay);
        final int defaultFadeLength = res.getInteger(R.integer.default_underline_indicator_fade_length);
        final int defaultSelectedColor = res.getColor(R.color.default_underline_indicator_selected_color);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UnderlinePageIndicator, defStyle, 0);

        setFades(a.getBoolean(R.styleable.UnderlinePageIndicator_fades, defaultFades));
        setSelectedColor(a.getColor(R.styleable.UnderlinePageIndicator_selectedColor, defaultSelectedColor));
        setFadeDelay(a.getInteger(R.styleable.UnderlinePageIndicator_fadeDelay, defaultFadeDelay));
        setFadeLength(a.getInteger(R.styleable.UnderlinePageIndicator_fadeLength, defaultFadeLength));

        Drawable background = a.getDrawable(R.styleable.UnderlinePageIndicator_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        }

        a.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    public void setCurrentDisplayedChild(int currentDisplayedChild) {
        this.currentDisplayedChild = currentDisplayedChild;
        invalidate();
    }

    public int getSelectedColor() {
        return mPaint.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        mPaint.setColor(selectedColor);
        invalidate();
    }

    public int getFadeDelay() {
        return mFadeDelay;
    }

    public void setFadeDelay(int fadeDelay) {
        mFadeDelay = fadeDelay;
    }

    public int getFadeLength() {
        return mFadeLength;
    }

    public void setFadeLength(int fadeLength) {
        mFadeLength = fadeLength;
        mFadeBy = 0xFF / (mFadeLength / FADE_FRAME_MS);
    }

    public boolean getFades() {
        return mFades;
    }

    public void setFades(boolean fades) {
        if (fades != mFades) {
            mFades = fades;
            if (fades) {
                post(mFadeRunnable);
            } else {
                removeCallbacks(mFadeRunnable);
                mPaint.setAlpha(0xFF);
                invalidate();
            }
        }
    }

    private final Runnable mFadeRunnable = new Runnable() {
        @Override public void run() {
            if (!mFades) return;
            final int alpha = Math.max(mPaint.getAlpha() - mFadeBy, 0);
            mPaint.setAlpha(alpha);
            invalidate();
            if (alpha > 0) {
                postDelayed(this, FADE_FRAME_MS);
            }
        }
    };

    public void setViewFlipper(ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int count = viewFlipper.getChildCount();
        if (count == 0) {
            return;
        }

        currentDisplayedChild = viewFlipper.getDisplayedChild();

        final int paddingLeft = getPaddingLeft();
        final float pageWidth = (getWidth() - paddingLeft - getPaddingRight()) / (1f * count);
        final float left = paddingLeft + pageWidth * (currentDisplayedChild);
        final float right = left + pageWidth;
        final float top = getPaddingTop();
        final float bottom = getHeight() - getPaddingBottom();
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentDisplayedChild = savedState.currentDisplayedChild;
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentDisplayedChild = currentDisplayedChild;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentDisplayedChild;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentDisplayedChild = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentDisplayedChild);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}