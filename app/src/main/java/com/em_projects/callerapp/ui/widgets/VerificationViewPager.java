package com.em_projects.callerapp.ui.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by eyal muchtar on 04/08/2017.
 */

public class VerificationViewPager extends ViewPager {

    private static final String TAG = "AuthViewPager";

    public VerificationViewPager(Context context) {
        super(context);
        Log.d(TAG, "constructor");
    }

    public VerificationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }


}
