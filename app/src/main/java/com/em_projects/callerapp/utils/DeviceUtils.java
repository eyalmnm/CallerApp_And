package com.em_projects.callerapp.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by eyal muchtar on 13/02/2017.
 */

public class DeviceUtils {

    /**
     * Retrieve device's IMEI
     *
     * @param context application or base context
     * @return device's IMEI
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * Retrieve device's unique Id
     *
     * @param context application or base context
     * @return device's UID
     */
    public static String getDeviceUniqueID(Context context) {
        String device_unique_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    /**
     * Retrieve device's IMSI
     * Uses uses-permission android:name="android.permission.READ_PHONE_STATE"
     *
     * @param context application or base context
     * @return device's IMSI
     */
    public static String getDeviceImsi(Context context) {
//        String myIMSI = SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
//        String myIMEI = SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMEI);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        if (true == StringUtils.isNullOrEmpty(imsi)) {
            imsi = get(context, "android.telephony.TelephonyProperties.PROPERTY_IMSI");
        }
        if (true == StringUtils.isNullOrEmpty(imsi)) {
            imsi = findDeviceID(context);
        }
        return imsi;
    }

    /**
     * Get the value for the given key.
     *
     * @return an empty string if the key isn't found
     */
    private static String get(Context context, String key) {
        String ret = "";

        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params = new Object[1];
            params[0] = new String(key);

            ret = (String) get.invoke(SystemProperties, params);
        } catch (Exception e) {
            ret = "";
            //TODO : Error handling
        }

        return ret;
    }

    private static String findDeviceID(Context context) {
        String deviceID = null;
        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) context.getSystemService(serviceName);
        int deviceType = m_telephonyManager.getPhoneType();
        switch (deviceType) {
            case (TelephonyManager.PHONE_TYPE_GSM):
                break;
            case (TelephonyManager.PHONE_TYPE_CDMA):
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                break;
            default:
                break;
        }
        deviceID = m_telephonyManager.getDeviceId();
        return deviceID;
    }
}
