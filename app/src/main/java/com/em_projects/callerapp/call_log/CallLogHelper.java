package com.em_projects.callerapp.call_log;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by eyalmuchtar on 2/22/18.
 */

public class CallLogHelper {

    private ArrayList<CallLogEntry> getAllCallLogs(Context context) {
        ArrayList<CallLogEntry> callLogEntries = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        // reading all data in descending order according to DATE
        String strOrder = CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.query(callUri, null, null, null, strOrder);
        // loop through cursor
        while (cur.moveToNext()) {

            String callNumber = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.NUMBER));

            String callName = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.CACHED_NAME));

            String callDate = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.DATE));

            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));

            String callType = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.TYPE));

            String isCallNew = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.NEW));

            String duration = cur.getString(cur
                    .getColumnIndex(CallLog.Calls.DURATION));

            callLogEntries.add(new CallLogEntry(callNumber, callName, dateString, callType, isCallNew, duration));
        }
        cur.close();
        return callLogEntries;
    }
}
