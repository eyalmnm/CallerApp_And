package com.em_projects.callerapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 * Created by eyal muchtar on 02/01/2017.
 */

public class ContactsUtils {

    // "android.permission.READ_CONTACTS" permission is required.
    public static String getContactName(Context context, String phoneNumber) {

        if (true == StringUtils.isNullOrEmpty(phoneNumber)) {
            return "";
        }

        Uri uri;
        String[] projection;
        Uri mBaseUri;

        if (Build.VERSION.SDK_INT >= 5) {
            mBaseUri = Uri.parse("content://com.android.contacts/phone_lookup");
            projection = new String[]{"display_name"};
        } else {
            mBaseUri = Uri.parse("content://contacts/phones/filter");
            projection = new String[]{"name"};
        }

        try {
            Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
            mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
            projection = new String[]{"display_name"};
        } catch (Exception e) {
        }


        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(0);
        }
        if (true == contactName.trim().isEmpty()) {
            contactName = phoneNumber;
        }

        cursor.close();
        cursor = null;

        return contactName;
    }
}
