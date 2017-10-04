package com.em_projects.callerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.em_projects.callerapp.intro.IntroActivity;
import com.em_projects.callerapp.tracer.ExceptionHandler;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        ExceptionHandler.register(this);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
                finish();
            }
        }, 3000);
    }
}
