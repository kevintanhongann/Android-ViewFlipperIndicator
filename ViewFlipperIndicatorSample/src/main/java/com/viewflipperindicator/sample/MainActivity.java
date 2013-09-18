package com.viewflipperindicator.sample;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import com.kevintan.viewflipperindicator.UnderlinePageIndicator;
import java.util.List;

public class MainActivity extends Activity {

    private ViewFlipper viewFlipper;
    private UnderlinePageIndicator viewFlipperIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipperIndicator = (UnderlinePageIndicator) findViewById(R.id.viewFlipperIndicator);
        viewFlipperIndicator.setViewFlipper(viewFlipper);

        //FIXME by right I shouldnt have to manually assign a color since I've assigned a style with proper values
        viewFlipperIndicator.setSelectedColor(getResources().getColor(R.color.dark_red));
        final GestureDetector gestureDetector = new GestureDetector(this, new CustomGestureListener(viewFlipper, viewFlipperIndicator));

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * class for setting gestures for the viewflipper for categories view
     */
    class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewFlipper viewFlipper;
        UnderlinePageIndicator indicator;

        public CustomGestureListener(ViewFlipper viewFlipper, UnderlinePageIndicator indicator) {
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

    Animation slideInRightAnimation;
    Animation slideOutLeftAnimation;
    Animation slideInLeftAnimation;
    Animation slideOutRightAnimation;

    private void swipeLeft(ViewFlipper flipper) {
        slideInRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideInRightAnimation.setDuration(150);
        slideOutLeftAnimation.setDuration(150);
        flipper.setInAnimation(slideInRightAnimation);
        flipper.setOutAnimation(slideOutLeftAnimation);
        flipper.showNext();
    }

    private void swipeRight(ViewFlipper flipper) {
        slideInLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideOutRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        slideInLeftAnimation.setDuration(150);
        slideOutRightAnimation.setDuration(150);
        flipper.setInAnimation(slideInLeftAnimation);
        flipper.setOutAnimation(slideOutRightAnimation);
        flipper.showPrevious();
    }

}
