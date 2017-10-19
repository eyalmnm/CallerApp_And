package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.em_projects.callerapp.config.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by eyalmuchtar on 06/08/2017.
 */

// @Ref: http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev
// @Ref: http://stackoverflow.com/a/40639277

public class PreferencesUtils {
    private static final String TAG = "PreferencesUtils";
    // Shared preferences file name
    private static final String PREF_NAME = "callApp";
    private static PreferencesUtils instance = null;
    // Shared preferences access components
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    // Application context
    private Context context;
    // Shared preferences working mode
    private int PRIVATE_MODE = 0;

    private PreferencesUtils(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public static PreferencesUtils getInstance(Context context) {
        if (null == instance) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public void setPhoneNumber(String phoneNumber) throws Exception{
        if (true == StringUtils.isValidPhoneNumber(phoneNumber)) {
            editor.putString(Constants.phoneNumber, phoneNumber);
            editor.commit();
        } else {
            throw new Exception("Invalid Phone Number");
        }
    }

    public boolean isRegisteredUser() {
        String registrationCode = context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE).getString("registrationCode", null);
        return !StringUtils.isNullOrEmpty(registrationCode);
    }

    public void registerUser(String registrationCode) {
        SharedPreferences sharedPreference = context.getSharedPreferences(Constants.shared_preferences_name, MODE_PRIVATE);
        sharedPreference.edit().putString("registrationCode", registrationCode).apply();
    }

    public String getPhone() {
        return preferences.getString(Constants.phoneNumber, null);
    }

    public String getToken() {
        return preferences.getString(Constants.token, null);
    }

    public void setToken(String token) throws Exception {
        if (true == StringUtils.isNullOrEmpty(token)) {
            editor.putString(Constants.token, token);
            editor.commit();
        } else {
            throw new Exception("Invalid Token");
        }
    }

    public String getOTP() {
        return preferences.getString(Constants.otp, null);
    }

    public void setOTP(String otp) throws Exception {
        if (true == StringUtils.isNullOrEmpty(otp)) {
            editor.putString(Constants.otp, otp);
            editor.commit();
        } else {
            throw new Exception("Invalid Token");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}
