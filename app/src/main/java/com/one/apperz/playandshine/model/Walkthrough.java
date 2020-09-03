package com.one.apperz.playandshine.model;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.one.apperz.playandshine.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class Walkthrough {
    public Walkthrough(View v, Activity activity,String f,String s){

        new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(v.getId())
                .setPrimaryText(f)
                .setSecondaryText(s)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            // User has pressed the prompt target
//                            Toast.makeText(activity.getApplicationContext(),"done",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .show();
    }
}
