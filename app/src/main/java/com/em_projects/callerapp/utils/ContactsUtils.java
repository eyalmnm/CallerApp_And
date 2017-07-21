package com.em_projects.callerapp.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eyalmuchtar on 02/01/2017.
 */

// "android.permission.READ_CONTACTS" permission is required.
public class ContactsUtils {

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

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Bitmap photo = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

//        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    // @Ref: https://stackoverflow.com/questions/12562151/android-get-all-contacts
    public static void getAllContacts(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(context, "Name: " + name
                                + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }
        }
    }
}
