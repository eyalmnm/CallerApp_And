package com.em_projects.callerapp.verification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.DeviceUtils;


/**
 * Created by eyal muchtar on 14/02/2017.
 */

public class CommunicationService extends IntentService {

    private static final String TAG = "CommunicationService";

    private String otp;
    private String mobile;
    private String imei;

    private Context context;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public CommunicationService() {
        super(TAG);
        Log.d(TAG, "constructor");
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        otp = intent.getStringExtra("otp");
        mobile = intent.getStringExtra("mobile");
        imei = DeviceUtils.getDeviceUniqueID(context);

        try {
            ServerUtilities.getInstance().verifyOtpCode(otp, mobile, imei, new CommListener() {
                @Override
                public void newDataArrived(String response) {
                    String action;
                    try {
                        if (true == LoginActivity.isSuccessVerification(response)) {
                            action = LoginActivity.OPT_VERIFICATION_SUCCESS;
                        } else {
                            action = LoginActivity.OPT_VERIFICATION_FAILED;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onHandleIntent - newDataArrived", e);
                        action = LoginActivity.OPT_VERIFICATION_FAILED;
                    }
                    broadcastEvent(action);
                }

                @Override
                public void exceptionThrown(Throwable throwable) {
                    Log.e(TAG, "onHandleIntent - exceptionThrown", throwable);
                    String action = LoginActivity.OPT_VERIFICATION_FAILED;
                    broadcastEvent(action);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onHandleIntent", e);
            String action = LoginActivity.OPT_VERIFICATION_FAILED;
            broadcastEvent(action);
        }
    }

    private void broadcastEvent(String action) {
        Intent intent = new Intent(action);
        if (LoginActivity.OPT_VERIFICATION_SUCCESS.equalsIgnoreCase(action)) {
            intent.putExtra("otp", otp);
        }
        sendBroadcast(intent);
    }
}
