package com.one.apperz.playandshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.graphics.Color.rgb;

public class ImageActivity extends AppCompatActivity {
//    private TextView[] mDots;
    LinearLayout mDotLayout;
    private Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        ImageAdapter adapterView = new ImageAdapter(this);
//        mDotLayout = (LinearLayout) findViewById(R.id.linearLayout);
        finish = findViewById(R.id.buttonFinish);
        mViewPager.setAdapter(adapterView);
    }

    public void buttonFinishClicked(View view) {
        Intent intent = new Intent(this,Search.class);
        startActivity(intent);
    }
}