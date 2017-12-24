package com.em_projects.callerapp.config;

/**
 * Created by eyalmuchtar on 20/07/2017.
 */
// Ref: http://www.theappguruz.com/blog/detecting-incoming-phone-calls-in-android

public class Constants {

    public static final String shared_preferences_name = "caller_app_pref";

    // GCM Constants
    public static final String SENT_TOKEN_TO_SERVER = "sent_token_to_server";
    public static final String REGISTRATION_COMPLETE = "registration_complete";

    // GCM Properties
    public static final String GCM_JSON_DATA = "data";
    public static final String GCM_SENDER_ID = "633244248355";
    public static final String GCM_COMMAND_METHOD = "method";
    public static final String GCM_COMMAND_COUNTER = "counter";
    public static final String GCM_COMMAND_CONTACT = "contacts";

    // SMS Verifcation Section
    //*****************************
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "+12077470438";
    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
    public static final int OTP_LENGTH = 6;
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
    public static String serverURL = "http://advert.zone:37443"; //77.127.141.67:37443"; // http://server.8200.zone:8080";
    public static String ourSecret = "ourSecret";
    public static String deviceId = "deviceId";
    public static String gcmToken = "gcmToken";
    public static String phoneNumber = "phone";
    public static String email = "email";
    public static String firstName = "firstName";
    public static String lastName = "lastName";
    public static String fullName = "fullName";
    public static String timeStamp = "timeStamp";
    public static String otp = "sms_validation";
    public static String fbToken = "fb_token";
    public static String fbOldToken = "fb_old_token";
    public static String callerPhone = "caller_phone";
    public static String contatcts = "contatcts_list";
    public static String countryCode = "country_code";

    // Server Communication methods
    public static String smsVerification = "new_user";
    public static String otpVerification = "validate";
    public static String sendContatcts = "upload_contacts";
    public static String sendGcmToken = "send_token";
    public static String searchPhone = "search_phone";
    public static String sendFbToken = "send_token";
    public static String sendNewFbToken = "send_token";

    // Server Communication secret
    public static String secret = "sfasfwtweerwenjhvytetqw8er9y8rhhjvctu67r687et9yqiehnnbebfdq8etqo";
    // Connection token
    public static String token = "token";

    // Preferences
    public static String introIsShown = "introIsShown";

}
