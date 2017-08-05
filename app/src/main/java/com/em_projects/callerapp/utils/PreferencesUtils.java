package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by eyalmuchtar on 06/08/2017.
 */

public class PreferencesUtils {
    private static final String TAG = "PreferencesUtils";

    // Shared preferences access components
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    // Application context
    private Context context;

    // Shared preferences file name
    private static final String PREF_NAME = "callApp";
    // Shared preferences working mode
    private int PRIVATE_MODE = 0;

}
