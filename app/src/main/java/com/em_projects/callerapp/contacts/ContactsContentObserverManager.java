package com.em_projects.callerapp.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eyalmuchtar on 1/8/18.
 */

// Ref: https://www.grokkingandroid.com/use-contentobserver-to-listen-to-changes/ 
// Ref: https://stackoverflow.com/questions/5020276/can-you-determine-what-contact-changes-w-registercontentobserver
// Ref: https://stackoverflow.com/questions/25686018/android-how-to-detect-contact-list-is-changed  <----------------------<<<-
// Ref: https://stackoverflow.com/questions/5751351/android-how-do-you-detect-which-contact-changed

public class ContactsContentObserverManager {
    private static final String TAG = "ContactsContentObsrMngr";

    private static Context context;
    private static ContactsContentObserverManager instance;
    private ContentObserver observer;

    private ContactsContentObserverManager(Context ctx) {
        context = ctx;
        observer = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                transmitContactsList();
            }
        };
    }

    public static ContactsContentObserverManager getInstance(Context ctx) {
        Log.d(TAG, "getInstance");
        if (null == instance) {
            instance = new ContactsContentObserverManager(ctx);
        }
        return instance;
    }

    private void transmitContactsList() {
        if (null != context) {
            Toast.makeText(context, "Updating Contacts List...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ContactsTxIntentService.class);
            context.startService(intent);
        }
    }

    public void registerContactsContentObserver() {
        if (null != context) {
            context.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer);
        }
    }
}
