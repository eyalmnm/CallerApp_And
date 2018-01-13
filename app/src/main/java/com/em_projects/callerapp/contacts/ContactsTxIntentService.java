package com.em_projects.callerapp.contacts;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.ContactsUtils;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;

/**
 * Created by eyalmuchtar on 29/10/2017.
 */

public class ContactsTxIntentService extends IntentService {
    private static final String TAG = "ContactsTxIntentService";

    private Context context;

    // Thread's properties
    private Looper serviceLopper;
    private Handler serviceHandler;


    public ContactsTxIntentService() {
        super("ContactsTxIntentService");
    }

    public ContactsTxIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Init the context
        context = this.getApplicationContext();

        // Init service handler
        initHandler();

        // Send Contacts to server
        sendContacts();
    }


    private void sendContacts() {
        serviceHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String contactsList = ContactsUtils.getContactsJsonArray(context);
                    String deviceId = DeviceUtils.getDeviceUniqueID(context);
                    String phoneNumber = PreferencesUtils.getInstance(context).getPhone();
                    String otp = PreferencesUtils.getInstance(context).getOTP();
                    ServerUtilities.getInstance().sendContact(deviceId, phoneNumber, otp, contactsList, new CommListener() {
                        @Override
                        public void newDataArrived(String response) {
                            Log.d(TAG, "newDataArrived response: " + response);
                        }

                        @Override
                        public void exceptionThrown(Throwable throwable) {
                            Log.e(TAG, "exceptionThrown", throwable);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "onHandleIntent", e);
                    FirebaseCrash.logcat(Log.ERROR, TAG, "sendContacts");
                    FirebaseCrash.report(e);
                }
            }
        });
    }

    private void initHandler() {
        HandlerThread thread = new HandlerThread("ContactsTxIntentService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLopper = thread.getLooper();
        serviceHandler = new Handler(serviceLopper);
    }
}
