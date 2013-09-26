package com.viewflipperindicator.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by kevintan on 9/18/13.
 */
public class HomeActivity extends Activity {

    Button underlineBtn;

    Button tabBtn;

    Button circleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        underlineBtn = (Button) findViewById(R.id.button_underline);
        underlineBtn.setOnClickListener(onUnderlineClick);

        tabBtn = (Button) findViewById(R.id.button_tab);
        tabBtn.setOnClickListener(onTabPagerClick);

        circleBtn = (Button) findViewById(R.id.button_circle);
        circleBtn.setOnClickListener(onCirclePagerClick);
    }

    View.OnClickListener onUnderlineClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this, UnderlineActivity.class));
        }
    };

    View.OnClickListener onTabPagerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this, TabPagerActivity.class));
        }
    };

    View.OnClickListener onCirclePagerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this, CirclePagerActivity.class));
        }
    };
}
