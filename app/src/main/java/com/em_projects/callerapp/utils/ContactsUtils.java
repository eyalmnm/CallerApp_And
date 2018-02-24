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
import android.util.Log;
import android.widget.Toast;

import com.em_projects.callerapp.config.Constants;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 02/01/2017.
 */

// "android.permission.READ_CONTACTS" permission is required.
public class ContactsUtils {
    private static final String TAG = "ContactsUtils";

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
        String contactIdStr = null;
        Bitmap photo = null;
        if (true == StringUtils.isNullOrEmpty(number)) {
            FirebaseCrash.log("phone number is null in " + TAG + " retrieveContactPhoto");
            return null;
        }
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
                contactIdStr = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        if (true == StringUtils.isNullOrEmpty(contactIdStr)) {
            FirebaseCrash.log("contactId is null in " + TAG + " retrieveContactPhoto");
            return null;
        }
        Long contactId = 0L;
        try {
            contactId = new Long(contactIdStr);
        } catch (Throwable ex) {
            Log.e(TAG, "retrieveContactPhoto", ex);
            FirebaseCrash.logcat(Log.ERROR, TAG, "retrieveContactPhoto for: ID" + contactIdStr);
            FirebaseCrash.report(ex);
            return null;
        }
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId));
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }
//            assert inputStream != null;
            if (null != inputStream) {
                inputStream.close();
            }
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
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(context, "Name: " + name
                                + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }
        }
    }

    public static void getContactId(Context context, String phoneNumber) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] proj = new String[]
                {///as we need only this colum from a table...
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts._ID,
                };
        String id = null;
        String sortOrder1 = ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor crsr = context.getContentResolver().query(lookupUri, proj, null, null, sortOrder1);
        while (crsr.moveToNext()) {
            String name = crsr.getString(crsr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            id = crsr.getString(crsr.getColumnIndex(ContactsContract.Contacts._ID));
            Toast.makeText(context, "Name of this cntct is   : " + name + "\n id is : " + id, Toast.LENGTH_SHORT).show();
        }
        crsr.close();
    }

    public static String getContactByPhone(Context context, String phone) {
        Cursor cursor;
        ArrayList<String> contactList = new ArrayList<>();

        String phoneNumber = null;
        String email = null;

        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        String sortOrder1 = ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));

        StringBuffer output;

        ContentResolver contentResolver = context.getContentResolver();
        cursor = contentResolver.query(lookupUri, null, null, null, sortOrder1);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                output = new StringBuffer();

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    output.append("\n First Name:" + name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);
                    }
                    phoneCursor.close();

                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        output.append("\n Email:" + email);
                    }
                    emailCursor.close();
                    // Read Contacts Photo
                }

                // Add the contact to the ArrayList
                contactList.add(output.toString());
            }
        }
        cursor.close();
        return contactList.toString();
    }

    public static ArrayList<String> getContactsArray(Context context) {
        Cursor cursor;
        ArrayList<String> contactList;
        contactList = new ArrayList<String>();
        int counter;

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output;

        ContentResolver contentResolver = context.getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {

            counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    output.append("\n First Name:" + name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);
                    }
                    phoneCursor.close();

                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        output.append("\n Email:" + email);
                    }
                    emailCursor.close();

                    // Read Contacts Photo
                    Bitmap photo = null; // = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
                    try {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contact_id)));

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);    // TODO
                            output.append("\n Photo:" + ImageUtils.bitmapToBase64String(photo));
                            photo.recycle();
                        }

//                        assert inputStream != null;
                        if (null != inputStream) {
                            inputStream.close();
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "getContacts", e);
                    }
                }

                // Add the contact to the ArrayList
                contactList.add(output.toString());
            }
        }
        cursor.close();
        return contactList;
    }

    public static String getContactsJsonArray(Context context) throws JSONException {
        Cursor cursor;
        JSONArray contactList;
        contactList = new JSONArray();

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        JSONObject output;

        ContentResolver contentResolver = context.getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        int counter = cursor.getCount();
        if (counter > 0) {

            while (cursor.moveToNext()) {
                output = new JSONObject();

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    output.put(Constants.fullName, name);

                    //This is to read multiple phone numbers associated with the same contact
                    JSONArray phoneNumberArray = new JSONArray();
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phoneNumberArray.put(phoneNumber);
                    }
                    phoneCursor.close();
                    output.put(Constants.phoneNumber, phoneNumberArray);

                    // Read every email id associated with the contact
                    JSONArray emailArray = new JSONArray();
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        emailArray.put(email);
                    }
                    emailCursor.close();
                    output.put(Constants.email, emailArray);
                }

                // Add the contact to the ArrayList
                contactList.put(output);
            }
        }
        cursor.close();
        return contactList.toString();
    }


    public static Bitmap retrieveContactPhotoNew(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
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

        Bitmap photo = null; // = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            Log.e(TAG, "retrieveContactPhotoNew", e);
        }
        return photo;
    }


}