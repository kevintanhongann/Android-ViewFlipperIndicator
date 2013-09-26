package com.viewflipperindicator.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ViewFlipper;

import com.kevintan.viewflipperindicator.TabPageIndicator;

/**
 * Created by kevintan on 9/19/13.
 */
public class TabPagerActivity extends Activity {

    ViewFlipper viewFlipper;
    TabPageIndicator tabPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabpager);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

    }
}
