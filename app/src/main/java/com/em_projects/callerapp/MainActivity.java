package com.em_projects.callerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.em_projects.callerapp.tracer.ExceptionHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExceptionHandler.register(this);
    }
}
