package com.em_projects.callerapp.config;

/**
 * Created by eyalmuchtar on 20/07/2017.
 */

public class Constants {

    public static final String shared_preferences_name = "caller_app_pref";

    // Radio - Telephony Utils
    public static String mcc = "mcc";
    public static String mnc = "mnc";
    public static String lac = "lac";
    public static String cid = "cid";
    public static String psc = "psd";
    public static String radio = "radio";
    public static String dbm = "dbm";
    public static String lvl = "lvl";
    public static String cell_identity = "cell_identity";


    // Server Communication parameters
    public static String serverURL;
    public static String ourSecret;
    public static String deviceId;
    public static String phoneNumber;
    public static String timeStamp;
    public static String otp;

    // Server Communication methods
    public static String smsVerification;
    public static String otpVerification;


    // Server Communication secret
    public static String secret = "sfasfwtweerwenjhvytetqw8er9y8rhhjvctu67r687et9yqiehnnbebfdq8etqo";

    // Connection token
    public static String token = "token";



    // SMS Verifcation Section
    //*****************************
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "Cellcom";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";

}
