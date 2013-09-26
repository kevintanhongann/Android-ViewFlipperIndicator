package com.kevintan.viewflipperindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewFlipper;

/**
 * Created by kevintan on 9/23/13.
 */
public class TabPageIndicator extends View implements PageIndicator {

    ViewFlipper viewFlipper;

    public TabPageIndicator(Context context) {
        super(context);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setViewFlipper(ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }



}
