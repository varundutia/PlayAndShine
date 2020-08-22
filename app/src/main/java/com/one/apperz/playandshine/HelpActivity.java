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
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void buttonBackClicked(View view) {
        finish();
    }

}