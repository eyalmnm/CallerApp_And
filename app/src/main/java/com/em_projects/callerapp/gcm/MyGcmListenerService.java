package com.em_projects.callerapp.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.utils.StringUtils;
import com.google.android.gms.iid.InstanceIDListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by eyalmuchtar on 19/08/2017.
 */

public class MyGcmListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    public void handleIntent(Intent intent) {
        Bundle data = intent.getExtras();
        // Print the incoming message properties
        Set<String> keys = data.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            Log.d(TAG, key + " = " + data.get(key));
        }

        String jsonStr = data.getString(Constants.GCM_JSON_DATA);
        if (false == StringUtils.isNullOrEmpty(jsonStr)) {
            Log.d(TAG, "onMessageReceived: incoming message" + jsonStr);
            try {
                JSONObject json = new JSONObject(jsonStr);
                if (null != json) {
                    // TODO Parse the Object.
                }
            } catch (JSONException e) {
                Log.e(TAG, "onMessageReceived", e);
            }
        }
    }
}
