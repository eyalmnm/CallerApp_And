package com.em_projects.callerapp.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.contacts.ContactsContentObserverManager;
import com.em_projects.callerapp.contacts.ContactsTxIntentService;
import com.em_projects.callerapp.gcm.RegistrationIntentService;
import com.em_projects.callerapp.main.activities.FacebookLoginActivity;
import com.em_projects.callerapp.main.fragments.CallLogFragment;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.verification.LoginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.WAKE_LOCK;

/**
 * Created by eyalmuchtar on 05/10/2017.
 */

// Ref: https://gist.github.com/Aeonitis/2337b1ca652173839395be82db7d05c3
// Ref: https://stackoverflow.com/questions/6922312/get-location-name-from-fetched-coordinates
// Ref: https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null


// Ref: https://stackoverflow.com/questions/14540394/listen-to-incoming-whatsapp-messages-notifications
// Ref: https://stackoverflow.com/a/14907818/341497
// Ref: https://developer.android.com/training/accessibility/service.html#create


public class MainScreenActivity extends AppCompatActivity {
    // Setting IDs
    public static final int CALL_LOG = 10;
    public static final int APP_SETTING = 100;
    public static final int USER_PROFILE = 101;
    public static final int INTRO_SCREEN = 102;

    private static final String TAG = "MainScreenActivity";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final boolean USE_FACEBOOK_LOGIN = true;  // Uses Facebook
    private static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;
    private Context context;

    private FirebaseAnalytics analytics;

    private PowerManager.WakeLock wakeLock;

    // Permissions
    // Registration \ Login (SMS Verification)
    // GCM Registration
    // Social Network Registration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        FirebaseCrash.log("MainScreenActivity created");
        analytics = FirebaseAnalytics.getInstance(this);    // TODO Add Analytics

        context = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        // Check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                requestPermission();
            } else {
                continueAppLoading();
            }
        } else {
            continueAppLoading();
        }

        // Make sure the view adjust while showing keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    private void loadAppSettingFragment() {
        // TODO try without back stack
    }

    private void continueAppLoading() {
        if (false == checkIfRegistered()) {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }
            }, 250);
        } else {
            gcmRegistration();
        }
    }

    private boolean checkIfRegistered() {
        new Dynamic(this);
        return PreferencesUtils.getInstance(context).isRegisteredUser();
    }

    private void setIsRestration(String otp, String phone) {
        try {
            PreferencesUtils.getInstance(context).registerUser(phone, otp);
            PreferencesUtils.getInstance(context).setPhoneNumber(phone);
            PreferencesUtils.getInstance(context).setOTP(otp);
            new Dynamic(this);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "setIsRestration");
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PERMISSION_REQUEST_CODE == requestCode) {   // Login response
            if (Activity.RESULT_OK == resultCode) {
                String otp = data.getStringExtra(Constants.otp);
                String phone = data.getStringExtra(Constants.phoneNumber);
                setIsRestration(otp, phone);
                gcmRegistration();
            }
        } else if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!checkPermissions()) {
                    requestPermission();
                } else {
                    continueAppLoading();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createContactsContentObserver() {
        Log.d(TAG, "createContactsContentObserver");
        ContactsContentObserverManager.getInstance(context).registerContactsContentObserver();
    }

    private void gcmRegistration() {
        Log.d(TAG, "gcmRegistration");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            startService(intent);
        } else {
            Toast.makeText(context, "This device does not supports GCM!", Toast.LENGTH_LONG).show();
            finish();
        }
        if (true == USE_FACEBOOK_LOGIN) {
            faceBookLogin();
        }
        getLocation();
        createContactsContentObserver();

        if (true == isFirstTime()) {
            transmitContactsList();
            setFirstTime(false);
        } else {
            showCallLog();
        }
    }

    private void showCallLog() {
        CallLogFragment fragment = new CallLogFragment();
        showFragment(fragment);
    }

    private void transmitContactsList() {
        Intent intent = new Intent(context, ContactsTxIntentService.class);
        startService(intent);  // TODO
    }

    private boolean isFirstTime() {
        return PreferencesUtils.getInstance(context).isFirstTime();
    }

    private void setFirstTime(boolean firstTime) {
        PreferencesUtils.getInstance(context).setFirstTime(firstTime);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        String countryCode = null;
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        Location locations = getLastKnownLocation(); // locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String location = listAddresses.get(0).getAddressLine(0);
                    countryCode = listAddresses.get(0).getCountryCode();
                    Log.d(TAG, "Location: " + location + " country code: " + countryCode);
                    // Save country code in Preferences
                    PreferencesUtils.getInstance(context).setCountryCode(countryCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "getLocation", e);
                // Get country code from preferences
                PreferencesUtils.getInstance(context).getCountryCode();
            }
        }
        // Save country code in config.Dynamic
        Dynamic.setCountryCode(countryCode);
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void faceBookLogin() {
        Intent intent = new Intent(context, FacebookLoginActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        int receiveSms = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int readSms = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int readPhoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int internetRes = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int processOutgoingCalls = ContextCompat.checkSelfPermission(getApplicationContext(), PROCESS_OUTGOING_CALLS);
        int wakeLockRes = ContextCompat.checkSelfPermission(getApplicationContext(), WAKE_LOCK);
        int readContact = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int receiveBootComplete = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_BOOT_COMPLETED);
        int locationCoarse = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int locationFine = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int callLog = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);

        boolean overLay = Settings.canDrawOverlays(this);

        return receiveSms == PackageManager.PERMISSION_GRANTED &&
                readSms == PackageManager.PERMISSION_GRANTED &&
                readPhoneState == PackageManager.PERMISSION_GRANTED &&
                internetRes == PackageManager.PERMISSION_GRANTED &&
                processOutgoingCalls == PackageManager.PERMISSION_GRANTED &&
                wakeLockRes == PackageManager.PERMISSION_GRANTED &&
                readContact == PackageManager.PERMISSION_GRANTED &&
                receiveBootComplete == PackageManager.PERMISSION_GRANTED &&
                locationCoarse == PackageManager.PERMISSION_GRANTED &&
                locationFine == PackageManager.PERMISSION_GRANTED &&
                callLog == PackageManager.PERMISSION_GRANTED &&
                overLay;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECEIVE_SMS, READ_SMS, READ_PHONE_STATE, INTERNET,
                PROCESS_OUTGOING_CALLS, WAKE_LOCK, READ_CONTACTS, RECEIVE_BOOT_COMPLETED, ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION, READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean receiveSmsRes = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readSmsRes = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneStateRes = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean internetRes = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean outGoingCallRes = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeLockRes = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean readContactRes = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean bootRes = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean corseLocationRes = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean fineLocationRes = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean callLog = grantResults[10] == PackageManager.PERMISSION_GRANTED;

                    if (receiveSmsRes && readSmsRes && readPhoneStateRes && internetRes && outGoingCallRes &&
                            wakeLockRes && readContactRes && bootRes && corseLocationRes && fineLocationRes && callLog) {
                        if (Settings.canDrawOverlays(context)) {
                            //Toast.makeText(context, "Permission Granted, Now you can use the application.", Toast.LENGTH_LONG).show();
                            continueAppLoading();
                        } else {
                            //Toast.makeText(context, "Permission Denied, You cannot access the application.", Toast.LENGTH_LONG).show();
                            permissionToDrawOverlays();
                        }
                    } else {
                        Toast.makeText(context, "Permission Denied, You cannot access the application.", Toast.LENGTH_LONG).show();
                        if (!hasPermissions(context, RECEIVE_SMS, READ_SMS, READ_PHONE_STATE, INTERNET, PROCESS_OUTGOING_CALLS,
                                WAKE_LOCK, READ_CONTACTS, RECEIVE_BOOT_COMPLETED, ACCESS_COARSE_LOCATION,
                                ACCESS_FINE_LOCATION, READ_CALL_LOG)) {
                            Toast.makeText(context, "You need to allow access to all the permissions", Toast.LENGTH_LONG).show();
                            requestPermission();
                        }
                    }
                }
        }
    }

    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            FirebaseCrash.log("permissionToDrawOverlays");
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showFragment(Fragment fragment) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack("fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != wakeLock && false == wakeLock.isHeld()) {
            try {
                wakeLock.acquire();
            } catch (SecurityException e) {
                Log.e(TAG, "onResume");
            }
        }
        getLocation();
    }

    @Override
    protected void onPause() {
        if (null != wakeLock && true == wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (SecurityException e) {
                Log.e(TAG, "onPause");
            }
        }
        super.onPause();
    }
}
