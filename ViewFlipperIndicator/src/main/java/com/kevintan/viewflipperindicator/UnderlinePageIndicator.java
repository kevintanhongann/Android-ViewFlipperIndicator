package com.kevintan.viewflipperindicator;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ViewFlipper;

/**
 * Created by kevintan on 9/11/13.
 */
public class UnderlinePageIndicator extends View implements PageIndicator{

    private int mTouchSlop;
    ViewFlipper viewFlipper;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean mFades;
    private int mFadeDelay;
    private int mFadeLength;
    private int mFadeBy;
    private static final int FADE_FRAME_MS = 30;
    int currentDisplayedChild;
    private static final int INVALID_POINTER = -1;
    private float mLastMotionX = -1;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;

    public UnderlinePageIndicator(Context context) {
        this(context, null);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vfiUnderlinePageIndicatorStyle);
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

    @Override
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

    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((viewFlipper == null) || (viewFlipper.getChildCount() == 0)) {
            return false;
        }


        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mLastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, activePointerIndex);
                final float deltaX = x - mLastMotionX;

                if (!mIsDragging) {
                    if (Math.abs(deltaX) > mTouchSlop) {
                        mIsDragging = true;
                    }
                }

                if (mIsDragging) {
                    mLastMotionX = x;
                    /*if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
                        mViewPager.fakeDragBy(deltaX);
                    }*/
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mIsDragging) {
                    final int count = viewFlipper.getChildCount();
                    final int width = getWidth();
                    final float halfWidth = width / 2f;
                    final float sixthWidth = width / 6f;

                   /* if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager.setCurrentItem(mCurrentPage - 1);
                        }
                        return true;
                    } else if ((mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager.setCurrentItem(mCurrentPage + 1);
                        }
                        return true;
                    }*/
                }

                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                //if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                break;
        }

        return true;
    }
}