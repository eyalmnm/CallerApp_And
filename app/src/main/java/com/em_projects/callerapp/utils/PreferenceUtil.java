package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.em_projects.callerapp.config.Constants;

import static android.content.Context.MODE_PRIVATE;

// @Ref: http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev
// @Ref: http://stackoverflow.com/a/40639277

/**
 * Created by eyal muchtar on 10/05/2017.
 */

public class PreferenceUtil {

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE).getBoolean(permission, true);
    }
}
