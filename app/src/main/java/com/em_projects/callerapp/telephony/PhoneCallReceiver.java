package com.em_projects.callerapp.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.incall.InCallService;
import com.em_projects.callerapp.utils.StringUtils;


// Ref: http://stackoverflow.com/questions/19491458/android-call-waiting-state
// Ref: http://stackoverflow.com/a/33389985/341497
// Ref: http://androidexample.com/Incomming_Phone_Call_Broadcast_Receiver__-_Android_Example/index.php?view=article_discription&aid=61

// <uses-permission android:name="android.permission.READ_PHONE_STATE" />
public class PhoneCallReceiver extends BroadcastReceiver {

    final String TAG = "PhoneCallReceiver";

    private Context context;

    private String incomingNumber;

    public PhoneCallReceiver() {
    }

    // Ref: http://stackoverflow.com/questions/19491458/android-call-waiting-state
    // Ref: http://stackoverflow.com/a/33389985/341497
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e(TAG, "onReceive: " + StringUtils.intentToString(intent));
        Toast.makeText(context, "Incoming Call Detected!!!", Toast.LENGTH_SHORT).show();

        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.d(TAG, "The received event : " + event + ", incoming_number : " + incomingNumber);

        try {
            Log.d(TAG, "incomingNumber : " + incomingNumber);
            Log.d(TAG, "Incoming event: " + event);

            if (false == StringUtils.isNullOrEmpty(Dynamic.getMyNumber())
                    && incomingNumber.equals(Dynamic.getMyNumber())) return;

            switch (event) {
                case "RINGING":
                    searchForCallerByPhone(context, incomingNumber);
                    break;
                case "OFFHOOK":
                    break;
                case "IDLE":
                    context.stopService(new Intent(context, InCallService.class)); // TODO Consider this
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "onReceive", e);
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // We need this receiver to be as quick as possible
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    }

    private void searchForCallerByPhone(final Context context, String incomingNumber) {
        Intent inCallServiceIntent = new Intent(context, InCallService.class);
        inCallServiceIntent.putExtra(Constants.callerPhone, incomingNumber);
        context.startService(inCallServiceIntent);
    }
}


