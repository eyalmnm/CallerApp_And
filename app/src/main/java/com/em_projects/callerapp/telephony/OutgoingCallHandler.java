package com.em_projects.callerapp.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.utils.StringUtils;

// Ref: https://stackoverflow.com/questions/9569118/how-do-you-receive-outgoing-call-in-broadcastreceiver

// <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
public class OutgoingCallHandler extends BroadcastReceiver {

    private static final String TAG = "OutgoingCallHandler";

    public OutgoingCallHandler() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (StringUtils.isNullOrEmpty(Dynamic.myNumber)) return;

        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        // Extract phone number reformatted by previous receivers
        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            // No reformatted number, use the original
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        Log.d(TAG, "OutGoing Call : " + phoneNumber);

        // TODO Get Callee Data from Contacts
    }
}
