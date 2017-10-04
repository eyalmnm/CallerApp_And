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

    public static boolean isRegisteredUser(Context context) {
        String registrationCode = context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE).getString("registrationCode", null);
        return !StringUtils.isNullOrEmpty(registrationCode);
    }

    public static void registeredUser(Context context, String registrationCode) {
        SharedPreferences sharedPreference = context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE);
        sharedPreference.edit().putString("registrationCode", registrationCode).apply();
    }
}
