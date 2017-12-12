package com.em_projects.callerapp.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.em_projects.callerapp.MainActivity;

/**
 * Created by eyalmuchtar on 25/07/2017.
 */

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Reload the application
        Intent startIntent = new Intent(context, MainActivity.class);
        context.startActivity(startIntent);
    }
}
