package com.kevintan.viewflipperindicator;

import android.widget.ViewFlipper;

/**
 * Created by kevintan on 9/23/13.
 */
public interface SwipeListener {
    void swipeLeft(ViewFlipper viewFlipper);
    void swipeRight(ViewFlipper viewFlipper);
}
