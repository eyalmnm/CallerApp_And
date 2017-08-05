package com.em_projects.callerapp.utils;

/**
 * Created by eyal muchtar on 05/08/2017.
 */

public class TimeUtils {

    //return the time in UTC and in seconds since 1970
    public static long getTime() {
        return System.currentTimeMillis();
    }
}
