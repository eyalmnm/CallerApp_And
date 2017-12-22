package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by eyalmuchtar on 09/10/2017.
 */

public class AppUtils {

    public static String getAppVersion(Context context) {
        android.content.pm.PackageInfo appversion = null;

        try {
            appversion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appversion != null)
            return appversion.versionName;
        else
            return "";
    }

    public static int getAppVerionCode(Context context) {
        android.content.pm.PackageInfo appversion = null;

        try {
            appversion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appversion != null)
            return appversion.versionCode;
        else
            return 0;

    }

}
