package com.em_projects.callerapp.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.verification.CommunicationService;
import com.em_projects.callerapp.verification.LoginActivity;

/**
 * Created by eyal muchtar on 14/02/2017.
 */

// <uses-permission android:name="android.permission.RECEIVE_SMS" />
// <uses-permission android:name="android.permission.READ_SMS" />
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    public SmsReceiver() {
        super();
        Log.d(TAG, "constructor");
    }

    /**
     * Return the IntentFilter this receiver uses for registration.
     *
     * @return the intentFilter
     */
    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        return intentFilter;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Constants.SMS_ORIGIN.toLowerCase())) {
                        Log.e(TAG, "SMS is not for our app!");
                        return;
                    }

                    // verification code from sms
                    final String verificationCode = getVerificationCode(message);
                    Log.e(TAG, "OTP received: " + verificationCode);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent otpIntent = new Intent(LoginActivity.OPT_VERIFICATION_ARRIVED);
                            otpIntent.putExtra("otp", verificationCode);
                            context.sendBroadcast(otpIntent);
                        }
                    }).start();

                    Intent hhtpIntent = new Intent(context, CommunicationService.class);
                    hhtpIntent.putExtra("otp", verificationCode);
                    hhtpIntent.putExtra("mobile", Dynamic.getMyNumber());
                    context.startService(hhtpIntent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Constants.OTP_DELIMITER);
        if (index != -1) {
            int start = index + Constants.OTP_DELIMITER.length() + 1;
            int length = Constants.OTP_LENGTH;
            code = message.substring(start, start + length);
            return code;
        }
        return code;
    }

}
