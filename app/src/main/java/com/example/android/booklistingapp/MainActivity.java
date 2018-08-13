package com.example.android.booklistingapp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_main);

        ViewPager pager = findViewById(R.id.pager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
    }
}
