package com.em_projects.callerapp.call_log;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by eyalmuchtar on 2/22/18.
 */

// Required "android.permission.READ_CALL_LOG" permission
public class CallLogHelper {

    public static ArrayList<CallLogEntry> getAllCallLogs(Context context) {
        ArrayList<CallLogEntry> callLogEntries = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        // reading all data in descending order according to DATE
        String strOrder = CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.query(callUri, null, null, null, strOrder);
        // loop through cursor
        long start = System.currentTimeMillis();
        while (cur.moveToNext()) {

            String callNumber = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.NUMBER));

            //String callName = ContactsUtils.getContactName(context, callNumber);

            String callDate = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.DATE));
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));

            String callType = "";
            int callTypeCode = cur.getInt(cur
                    .getColumnIndex(CallLog.Calls.TYPE));
            switch (callTypeCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }

            String isCallNewCode = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.NEW));
            String isCallNew = CallLog.Calls.NEW.equalsIgnoreCase(isCallNewCode) ? "New Call" : "Not New Call";

            String duration = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.DURATION));

            callLogEntries.add(new CallLogEntry(context, callNumber, dateString,
                    callType, isCallNew, duration));
        }
        cur.close();
        Log.e("CallLogHelper", "duration: " + String.valueOf((System.currentTimeMillis() - start)));
        return callLogEntries;
    }
}
