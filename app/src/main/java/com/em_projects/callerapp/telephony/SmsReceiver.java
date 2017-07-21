package com.em_projects.callerapp.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

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
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
