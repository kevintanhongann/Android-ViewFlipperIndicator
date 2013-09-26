package com.viewflipperindicator.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.kevintan.viewflipperindicator.CirclePageIndicator;
import com.kevintan.viewflipperindicator.SwipeListener;

/**
 * Created by kevintan on 9/19/13.
 */
public class CirclePagerActivity extends Activity implements SwipeListener{

    CirclePageIndicator circlePageIndicator;
    ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
        circlePageIndicator.setViewFlipper(mViewFlipper);

        final GestureDetector gestureDetector = new GestureDetector(this, new CustomGestureListener(mViewFlipper, circlePageIndicator));
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    Animation slideInRightAnimation;
    Animation slideOutLeftAnimation;
    Animation slideInLeftAnimation;
    Animation slideOutRightAnimation;

    @Override
    public void swipeLeft(ViewFlipper viewFlipper) {
        slideInRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideInRightAnimation.setDuration(150);
        slideOutLeftAnimation.setDuration(150);
        mViewFlipper.setInAnimation(slideInRightAnimation);
        mViewFlipper.setOutAnimation(slideOutLeftAnimation);
        mViewFlipper.showNext();
    }



    @Override
    public void swipeRight(ViewFlipper viewFlipper) {
        slideInLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideOutRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        slideInLeftAnimation.setDuration(150);
        slideOutRightAnimation.setDuration(150);
        mViewFlipper.setInAnimation(slideInLeftAnimation);
        mViewFlipper.setOutAnimation(slideOutRightAnimation);
        mViewFlipper.showPrevious();
    }

    class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewFlipper viewFlipper;
        CirclePageIndicator indicator;

        public CustomGestureListener(ViewFlipper viewFlipper, CirclePageIndicator indicator) {
            this.viewFlipper = viewFlipper;
            this.indicator = indicator;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int displayedChild = viewFlipper.getDisplayedChild();
            int childCount = viewFlipper.getChildCount();
            float sensitvity = 50;
            if ((e1.getX() - e2.getX()) > sensitvity) {
                if (displayedChild == childCount - 1) {
                    return false;
                } else {
                    swipeLeft(viewFlipper);
                    indicator.setCurrentDisplayedChild(displayedChild);
                }
            } else if ((e2.getX() - e1.getX()) > sensitvity) {
                if (displayedChild <= 0) {
                    return false;
                } else {
                    swipeRight(viewFlipper);
                    indicator.setCurrentDisplayedChild(displayedChild);
                }
            }
            return true;
        }
    }


}
