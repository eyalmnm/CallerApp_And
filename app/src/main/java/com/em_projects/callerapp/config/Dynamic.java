package com.em_projects.callerapp.config;

import android.content.Context;

import com.em_projects.callerapp.utils.PreferencesUtils;

/**
 * Created by eyal muchtar on 20/07/2017.
 */

public class Dynamic {

    public static String call_unique_ID = "1000";
    public static String gcm_token = "";
    private static String myNumber;
    private static String myOTP;


    public Dynamic(Context context) {
        myNumber = PreferencesUtils.getInstance(context).getPhone();
        myOTP = PreferencesUtils.getInstance(context).getOTP();
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
}