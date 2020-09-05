package com.one.apperz.playandshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences myPrefs = getSharedPreferences("myPrefs",MODE_PRIVATE);
        if(myPrefs != null && myPrefs.getBoolean("isWatched",false)){
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String hashKey = new String(Base64.encode(md.digest(), 0));
                    Log.i("hash", "printHashKey() Hash Key: " + hashKey);
                }
            } catch (NoSuchAlgorithmException e) {
                Log.e("hash", "printHashKey()", e);
            } catch (Exception e) {
                Log.e("hash", "printHashKey()", e);
            }
            startActivity(new Intent(AppIntroActivity.this,LoginActivity.class));
            finish();
        }


        setTransformer(new AppIntroPageTransformerType.Parallax(1.0,-1.0,2.0));

        addSlide(AppIntroFragment.newInstance("Welcome to\nPlay and Shine app!!!",
                "Non-profit pan India initiative using sports a cohesive and flexible tool to foster individual and community development.",
                R.drawable.slide1_drawable,
                ContextCompat.getColor(getApplicationContext(),R.color.appIntroColor1),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                R.font.raleway,
                R.font.raleway,
                R.drawable.slide1_gradient));

        addSlide(AppIntroFragment.newInstance("Join",
                "Sign up and be a part of our diverse sport community.",
                R.drawable.slide2_drawable,
                ContextCompat.getColor(getApplicationContext(),R.color.appIntroColor1),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                R.font.raleway,
                R.font.raleway,
                R.drawable.slide1_gradient));

        addSlide(AppIntroFragment.newInstance("Search",
                "Network with other individuals from similar sports or interests.",
                R.drawable.slide3_drawable,
                ContextCompat.getColor(getApplicationContext(),R.color.appIntroColor1),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                R.font.raleway,
                R.font.raleway,
                R.drawable.slide1_gradient));

        addSlide(AppIntroFragment.newInstance("Connect",
                "Receive mentorship and access the expertise of coaches and other professionals.",
                R.drawable.slide4_drawable,
                ContextCompat.getColor(getApplicationContext(),R.color.appIntroColor1),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                ContextCompat.getColor(getApplicationContext(),android.R.color.white),
                R.font.raleway,
                R.font.raleway,
                R.drawable.slide1_gradient));



    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences myPrefs = getSharedPreferences("myPrefs",MODE_PRIVATE);
        if(myPrefs != null) {
            SharedPreferences.Editor editor = myPrefs.edit();
            if(editor!= null) {
                editor.putBoolean("isWatched", true);
                editor.commit();
            }
        }
        startActivity(new Intent(AppIntroActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        SharedPreferences myPrefs = getSharedPreferences("myPrefs",MODE_PRIVATE);
        if(myPrefs != null) {
            SharedPreferences.Editor editor = myPrefs.edit();
            if(editor!= null) {
                editor.putBoolean("isWatched", true);
                editor.commit();
            }
        }
        startActivity(new Intent(AppIntroActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
