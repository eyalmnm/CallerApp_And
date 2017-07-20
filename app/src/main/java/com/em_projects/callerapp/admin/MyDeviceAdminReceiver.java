package com.em_projects.callerapp.admin;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by danny on 22 אפריל 2017.
 */

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {
    };

    public void onDisabled(Context context, Intent intent) {
    };
}
