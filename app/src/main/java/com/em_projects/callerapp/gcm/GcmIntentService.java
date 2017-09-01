package com.em_projects.callerapp.gcm;

import android.os.Bundle;
import android.util.Log;

import com.em_projects.selleniumproj.config.Constants;
import com.em_projects.selleniumproj.utils.StringUtils;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by eyalmuchtar on 20/08/2017.
 */

public class GcmIntentService extends GcmListenerService {
    private static final String TAG = "GcmIntentService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        // Print the incoming message properties
        Log.d(TAG, "From: " + from);
        Set<String> keys = data.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            Log.d(TAG, key + " - " + data.get(key));
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
