package com.em_projects.callerapp.utils;

/**
 * Created by eyal muchtar on 05/08/2017.
 */

public class TimeUtils {

    public static final long HOUR = 3600 * 1000;

    //return the time in UTC and in seconds since 1970
    public static long getTime() {
        return System.currentTimeMillis();
    }
}
