package com.em_projects.callerapp.config;

import android.content.Context;

import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.utils.StringUtils;

/**
 * Created by eyal muchtar on 20/07/2017.
 */

public class Dynamic {

    public static String call_unique_ID = "1000";
    private static String gcm_token = "";
    private static String myNumber;
    private static String myOTP;
    private static String myCountryCode;


    public Dynamic(Context context) {
        myNumber = PreferencesUtils.getInstance(context).getPhone();
        myOTP = PreferencesUtils.getInstance(context).getOTP();
        myCountryCode = PreferencesUtils.getInstance(context).getCountryCode();
    }

    public static String getMyNumber() {
        return myNumber;
    }

    public static void setMyNumber(String myNumber) {
        Dynamic.myNumber = myNumber;
    }

    public static String getMyOTP() {
        return myOTP;
    }

    public static void setMyOTP(String myOTP) {
        Dynamic.myOTP = myOTP;
    }

    public static String getCall_unique_ID() {
        return call_unique_ID;
    }

    public static void setCall_unique_ID(String call_unique_ID) {
        Dynamic.call_unique_ID = call_unique_ID;
    }

    public static String getCountryCode() {
        return myCountryCode;
    }

    public static void setCountryCode(String countryCode) {
        Dynamic.myCountryCode = countryCode;
    }

    public static String getGcmToken(Context context) {
        if (StringUtils.isNullOrEmpty(gcm_token)) {
            gcm_token = PreferencesUtils.getInstance(context).getGcmToken();
        }
        return gcm_token;
    }

    public static void setGcmToken(Context context, String gcm_token) throws Exception {
        if (true == StringUtils.isNullOrEmpty(Dynamic.gcm_token)) {
            PreferencesUtils.getInstance(context).setGcmToken(gcm_token);
            Dynamic.gcm_token = gcm_token;
        }
    }
}