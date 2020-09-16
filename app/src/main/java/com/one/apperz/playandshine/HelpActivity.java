package com.one.apperz.playandshine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView tv = findViewById(R.id.askUsButton);
        TextView tv1 = findViewById(R.id.social_facebook);
        TextView tv2 = findViewById(R.id.social_instagram);
        TextView tv3 = findViewById(R.id.social_youtube);
        TextView tv4 = findViewById(R.id.link_website);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv1.setMovementMethod(LinkMovementMethod.getInstance());
        tv2.setMovementMethod(LinkMovementMethod.getInstance());
        tv3.setMovementMethod(LinkMovementMethod.getInstance());
        tv4.setMovementMethod(LinkMovementMethod.getInstance());

    }

//    public void buttonBackClicked(View view) {
//        finish();
//    }

}