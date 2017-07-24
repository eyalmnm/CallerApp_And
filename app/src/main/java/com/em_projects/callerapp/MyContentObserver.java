package com.em_projects.callerapp;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.ContactsContract;

/**
 * Created by eyalmuchtar on 24/07/2017.
 */

// Ref: https://stackoverflow.com/questions/1401280/how-to-listen-for-changes-in-contact-database
// Ref: https://stackoverflow.com/questions/5996921/how-do-i-make-my-android-contentobserver-for-contactscontract-detect-a-added-up/31970349#31970349

public class MyContentObserver extends ContentObserver {

    private Context context;

    public MyContentObserver(Context context) {
        super(null);
        this.context = context;
        this.context.getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, this);
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        context.getApplicationContext().getContentResolver().unregisterContentObserver(this);
    }
}
