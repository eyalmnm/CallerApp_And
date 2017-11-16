package com.em_projects.callerapp.main.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.utils.StringUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by eyalmuchtar on 11/5/17.
 */

// Ref: https://code.tutsplus.com/tutorials/quick-tip-add-facebook-login-to-your-android-app--cms-23837
// Ref: https://developers.facebook.com/docs/facebook-login/android/accesstokens
// Ref: https://developers.facebook.com/docs/facebook-login/android/

public class FacebookLoginActivity extends Activity {
    private static final String TAG = "FacebookLoginActivity";

    private TextView info;
    private LoginButton faceBookLoginButton;

    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;

    private Context context;
    private String deviceId;
    private String phoneNumber;
    private String otp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FaceBook Components initialization.
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_facebook_login);
        context = this;
        setFinishOnTouchOutside(false);

        info = (TextView) findViewById(R.id.info);
        faceBookLoginButton = (LoginButton) findViewById(R.id.faceBookLoginButton);

        deviceId = DeviceUtils.getDeviceUniqueID(context);
        phoneNumber = PreferencesUtils.getInstance(context).getPhone();
        otp = PreferencesUtils.getInstance(context).getOTP();

        String fbToken = PreferencesUtils.getInstance(context).getFbToken();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                if (null != oldAccessToken && null != currentAccessToken) {
                    sendFacebookNewToken(oldAccessToken.getToken(), currentAccessToken.getToken());
                    try {
                        PreferencesUtils.getInstance(context).setFbToken(currentAccessToken.getToken());
                    } catch (Exception e) {
                        Log.e(TAG, "onCurrentAccessTokenChanged", e);
                    }
                }
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if (null != accessToken && false == StringUtils.isNullOrEmpty(accessToken.getToken()) && false == StringUtils.isNullOrEmpty(fbToken)) {
            finish();
            return;
        }

        faceBookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                try {
                    PreferencesUtils.getInstance(context).setFbToken(loginResult.getAccessToken().getToken());
                } catch (Exception e) {
                    Log.e(TAG, "onSuccess", e);
                }
                sendFacebookLoginToken(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    private void sendFacebookLoginToken(String token) {
        ServerUtilities.getInstance().sendFbToken(deviceId, phoneNumber, otp, token, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                Log.d(TAG, "sendFacebookLoginToken -> response: " + response);
                finish();
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "exceptionThrown", throwable);
                finish();
            }
        });
    }

    private void sendFacebookNewToken(String oldToken, String newToken) {
        ServerUtilities.getInstance().sendNewFbToken(deviceId, phoneNumber, otp, newToken, oldToken, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                Log.d(TAG, "sendFacebookNewToken -> response: " + response);
                finish();
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "sendFacebookNewToken", throwable);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
