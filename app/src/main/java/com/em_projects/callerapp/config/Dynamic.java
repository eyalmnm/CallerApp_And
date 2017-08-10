package com.em_projects.callerapp.config;

import android.content.Context;

import com.em_projects.callerapp.utils.PreferencesUtils;

/**
 * Created by eyal muchtar on 20/07/2017.
 */

public class Dynamic {

    private static String myNumber;
    private static String myToken;
    private static String call_unique_ID;

    public Dynamic(Context context) {
        myNumber = PreferencesUtils.getInstance(context).getPhone();
        myToken = PreferencesUtils.getInstance(context).getToken();
    }

    public static String getMyNumber() {
        return myNumber;
    }

    public static String getMyToken() {
        return myToken;
    }

    public static void setCall_unique_ID(String call_unique_ID) {
        Dynamic.call_unique_ID = call_unique_ID;
    }

    public static String getCall_unique_ID() {
        return call_unique_ID;
    }

    public static void setMyNumber(String myNumber) {
        Dynamic.myNumber = myNumber;
    }

    public static void setMyToken(String myToken) {
        Dynamic.myToken = myToken;
    }
}