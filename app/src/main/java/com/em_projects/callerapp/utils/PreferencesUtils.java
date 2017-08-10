package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.em_projects.callerapp.config.Constants;

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

    private static PreferencesUtils instance = null;

    public static PreferencesUtils getInstance(Context context) {
        if (null == instance) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    private PreferencesUtils(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setPhoneNumber(String phoneNumber) throws Exception{
        if (true == StringUtils.isValidPhoneNumber(phoneNumber)) {
            editor.putString(Constants.phoneNumber, phoneNumber);
            editor.commit();
        } else {
            throw new Exception("Invalid Phone Number");
        }
    }

    public void setToken(String token) throws Exception {
        if (true == StringUtils.isNullOrEmpty(token)) {
            editor.putString(Constants.token, token);
            editor.commit();
        } else {
            throw new Exception("Invalid Token");
        }
    }

    public String getPhone() {
        return preferences.getString(Constants.phoneNumber, null);
    }

    public String getToken() {
        return preferences.getString(Constants.token, null);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}
