package com.em_projects.callerapp.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by eyalmuchtar on 19/08/2017.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private Context context;

    // Thread's properties
    private Looper serviceLopper;
    private Handler serviceHandler;

    public RegistrationIntentService() {
        super(TAG);
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // START register for gcm
            // START get token
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(Constants.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // END get token
            Dynamic.gcm_token = token;
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token, PreferencesUtils.getInstance(context).getPhone());

            // Subscribe to topic channels
//            subscribeTopics(token);

            // Storing a boolean that indicates whether the generated token has been
            // sent to your server.
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
            // END register for gcm
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token, String phoneNumber) {
        String deviceId = DeviceUtils.getDeviceUniqueID(this);
        ServerUtilities.getInstance().sendGcmToken(deviceId, phoneNumber, token, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                Log.d(TAG, "newDataArrived: " + response);
                showToast("GCM Registration success");
                try {
                    Dynamic.gcm_token = token;
                } catch (Exception e) {
                    Log.e(TAG, "sendRegistrationToServer -> newDataArrived", e);
                }
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "exceptionThrown", throwable);
                showToast("GCM Registration failed: " + throwable.getMessage());
            }
        });
    }

    private void showToast(final String message) {
        if (null == serviceHandler) {
            HandlerThread thread = new HandlerThread("RegistrationIntentService", Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            serviceLopper = thread.getLooper();
            serviceHandler = new Handler(serviceLopper);
        }
        serviceHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}
