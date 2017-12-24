package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.em_projects.callerapp.config.Constants;

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
        String otp = getOTP();
        String phoneNumber = getPhone();
        return (false == StringUtils.isNullOrEmpty(otp)) && (false == StringUtils.isNullOrEmpty(phoneNumber));
    }

    public void registerUser(String phoneNumber, String otp) throws Exception {
        setPhoneNumber(phoneNumber);
        setOTP(otp);
    }

    public String getPhone() {
        return preferences.getString(Constants.phoneNumber, null);
    }

//    public String getToken() {
//        return preferences.getString(Constants.token, null);
//    }

//    public void setToken(String token) throws Exception {
//        if (false == StringUtils.isNullOrEmpty(token)) {
//            editor.putString(Constants.token, token);
//            editor.commit();
//        } else {
//            throw new Exception("Invalid Token");
//        }
//    }

    public String getOTP() {
        return preferences.getString(Constants.otp, null);
    }

    public void setOTP(String otp) throws Exception {
        if (false == StringUtils.isNullOrEmpty(otp)) {
            editor.putString(Constants.otp, otp);
            editor.commit();
        } else {
            throw new Exception("Invalid OTP");
        }
    }

    public String getFbToken() {
        return preferences.getString(Constants.fbToken, null);
    }

    public void setFbToken(String fbToken) throws Exception {
        if (false == StringUtils.isNullOrEmpty(fbToken)) {
            editor.putString(Constants.fbToken, fbToken);
            editor.commit();
        } else {
            throw new Exception("Invalid FB Token");
        }
    }

    public long getLastContactsTransmissionTime() {
        return preferences.getLong("LastContactsTransmissionTime", 0);
    }

    public void setLastContactsTransmissionTime(long time) {
        editor.putLong("LastContactsTransmissionTime", time);
        editor.commit();
    }

    public String getCountryCode() {
        return preferences.getString(Constants.countryCode, null);
    }

    public void setCountryCode(String countryCode) throws Exception {
        if (false == StringUtils.isNullOrEmpty(countryCode)) {
            editor.putString(Constants.countryCode, countryCode);
            editor.commit();
        } else {
            throw new Exception("Invalid Country Code");
        }
    }

    public boolean getIntroIsShown() {
        return preferences.getBoolean(Constants.introIsShown, false);
    }

    public void setIntroIsShown(boolean isShown) {
        editor.putBoolean(Constants.introIsShown, isShown);
        editor.commit();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}
