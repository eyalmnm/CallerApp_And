package com.em_projects.callerapp.verification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.telephony.PhoneNumber;
import com.em_projects.callerapp.telephony.SmsReceiver;
import com.em_projects.callerapp.ui.widgets.CustomFont;
import com.em_projects.callerapp.ui.widgets.VerificationViewPager;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.JSONUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.utils.StringUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eyalmuchtar on 08/08/2017.
 */

// Ref: https://stackoverflow.com/questions/6533234/how-to-make-httppost-call-with-json-encoded-body

public class LoginActivity extends Activity {
    // SMS Receiver Broadcast Action
    public static final String OPT_VERIFICATION_ARRIVED = "com.em_projects.callerapp.verification.LoginActivity.OPT_VERIFICATION_ARRIVED";
    public static final String OPT_VERIFICATION_SUCCESS = "com.em_projects.callerapp.verification.LoginActivity.OPT_VERIFICATION_SUCCESS";
    public static final String OPT_VERIFICATION_FAILED = "com.em_projects.callerapp.verification.LoginActivity.OPT_VERIFICATION_FAILED";
    public static final String ERROR_OCCURS = "com.em_projects.callerapp.verification.LoginActivity.ERROR_OCCURS";
    private static final String TAG = "LoginActivity";
    // UI Component of both of screens
    // (activity_sms_verification)
    private VerificationViewPager mainViewPager;
    private ViewPagerAdapter viewPagerAdapter;

    // (layout_sms_auth)
    private Spinner inputCountryCode;
    private EditText inputMobile;
    private EditText inputFullName;
    private TextView btnRequestSms;
    private PhoneNumber phoneNumber;

    // (layout_otp_auth)
    private EditText inputOtp;
    private TextView btn_resend_otp;
    private TextView btn_verify_otp;
    private ProgressBar progressBar;
    private BroadcastReceiver optVerificationReceiver;

    // Spinner Data
    private SpinnerAdapter spinnerAdapter;
    private ArrayList<String> countryCodes;
    private ArrayList<String> countryNames;
    private ArrayList<String> countryPrefix;
    private String selectedCountryPrefix;

    // Font
    private Typeface fontRegular;

    // Hold a refrence to this
    private Context context;

    private FirebaseAnalytics analytics;

    public static boolean isSuccessVerification(Context context, String response) {
        Log.d(TAG, "isSuccessVerification");
        try {
            JSONObject jsonObject = new JSONObject(response);
            String token = JSONUtils.getStringValue(jsonObject, "token");
            if (true == StringUtils.isNullOrEmpty(token)) {
                return false;
            } else {
                PreferencesUtils.getInstance(context).setWCToken(token);
                return true;
            }
        } catch (JSONException e) {
            Log.e(TAG, "isSuccessVerification", e);
            FirebaseCrash.logcat(Log.ERROR, TAG, "isSuccessVerification");
            FirebaseCrash.report(e);
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        FirebaseCrash.log("LoginActivity created");
        // Make sure the view adjust while showing keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d(TAG, "onCreate");

        // Init the reference to this
        context = this;

        analytics = FirebaseAnalytics.getInstance(this);    // TODO Add Analytics

        // Init Views
        initViewComponents();

        // Init Spinner
        initSpinner();

        // Init Buttons
        initButtons();

        // Init OPT verification Receiver
        initOptVerificationReceiver();

        // Init SMS Receiver
        registerSmsReceiver();
    }

    private void initViewComponents() {
        fontRegular = CustomFont.fontApercuRegular(this);

        // Initial the main view pager
        mainViewPager = (VerificationViewPager) findViewById(R.id.mainViewPager);
        mainViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        viewPagerAdapter = new ViewPagerAdapter();
        mainViewPager.setAdapter(viewPagerAdapter);

        // Screens UI Input Components
        inputCountryCode = (Spinner) findViewById(R.id.inputCountryCode);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputFullName = (EditText) findViewById(R.id.inputFullName);
        btnRequestSms = (TextView) findViewById(R.id.btnRequestSms);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        inputOtp = (EditText) findViewById(R.id.inputOtp);
        btn_resend_otp = (TextView) findViewById(R.id.btn_resend_otp);
        btn_verify_otp = (TextView) findViewById(R.id.btn_verify_otp);
    }

    private void initSpinner() {
        Log.d(TAG, "initSpinner");
        initSpinnerData();

        initSpinnerAdapter();

        String code = PhoneNumber.getCountryCode(this);
        setCurrentCountryCode(code);
    }

    private void initSpinnerData() {
        Log.d(TAG, "initSpinnerData");
        Resources resource = getResources();

        countryCodes = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.country_code_list)));
        countryNames = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.country_name_list)));
        countryPrefix = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.country_prefix_list)));
    }

    private void initSpinnerAdapter() {
        spinnerAdapter = new SpinnerAdapter(this, R.layout.country_code_spinner_adapter, countryNames);

        inputCountryCode.setAdapter(spinnerAdapter);

        inputCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryPrefix = countryCodes.get(position); // countryPrefix.get(position);
                TextView spinnerAdapterOptActivityTextView = (TextView) view.findViewById(R.id.spinnerAdapterOptActivityTextView);
                spinnerAdapterOptActivityTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                spinnerAdapterOptActivityTextView.setText(selectedCountryPrefix + "(" + "+" + countryPrefix.get(position) + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCurrentCountryCode(String countryCode) {
        int position = countryCodes.indexOf(countryCode.toUpperCase());
        if (-1 < position) {
            inputCountryCode.setSelection(position);
        }
    }

    private void initButtons() {

        inputMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    btnRequestSms.performClick();
                    return true;
                }
                return false;
            }
        });
        inputOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    btn_verify_otp.performClick();
                    return true;
                }
                return false;
            }
        });
        btnRequestSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO add first and last name fields
                String fullName = inputFullName.getText().toString();
                if (true == StringUtils.isNullOrEmpty(fullName)) {
                    Toast.makeText(context, R.string.invalid_fullname_entered, Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String phoneEntered = inputMobile.getText().toString();
                if (true == StringUtils.isNullOrEmpty(phoneEntered)) {
                    Toast.makeText(context, R.string.invalid_phone_entered, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        phoneNumber = new PhoneNumber(phoneEntered, selectedCountryPrefix, LoginActivity.this);
                        String imzi = DeviceUtils.getDeviceUniqueID(context);
                        ServerUtilities.getInstance().requestSMSVerification(imzi, phoneNumber.getNumber(), fullName, new CommListener() {
                            @Override
                            public void newDataArrived(String response) {
                                Log.d(TAG, "btnRequestSms - newDataArrived: " + response);
                                // moving the screen to next pager item i.e otp screen
                                Dynamic.setMyNumber(phoneNumber.getNumber());
                                new Handler(getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        mainViewPager.setCurrentItem(1);
                                    }
                                });
                            }

                            @Override
                            public void exceptionThrown(Throwable throwable) {
                                Log.e(TAG, "requestSMSVerification - exceptionThrown", throwable);
                                moveToLoginScreen();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "btnRequestSms OnClick", e);
                        Toast.makeText(context, R.string.invalid_phone_entered, Toast.LENGTH_LONG).show();
                        moveToLoginScreen();
                        FirebaseCrash.logcat(Log.ERROR, TAG, "requestSMSVerification");
                        FirebaseCrash.report(e);
                    }
                }
            }
        });
        btn_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToLoginScreen();
            }
        });
        btn_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String otp = inputOtp.getText().toString();
                if (false == StringUtils.isNullOrEmpty(otp)) {
                    try {
                        String imzi = DeviceUtils.getDeviceUniqueID(context);
                        ServerUtilities.getInstance().verifyOtpCode(otp, Dynamic.getMyNumber(), imzi, new CommListener() {

                            @Override
                            public void newDataArrived(String response) {
                                Log.d(TAG, "btn_verify_otp - newDataArrived response: " + response);
                                try {
                                    if (true == isSuccessVerification(context, response)) {
                                        backToMainScreen(otp);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "btn_verify_otp - newDataArrived", e);
                                }
                            }

                            @Override
                            public void exceptionThrown(Throwable throwable) {
                                Log.e(TAG, "btn_verify_otp - exceptionThrown", throwable);
                                moveToLoginScreen();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "btnVerifyOtp onClick ", e);
                        moveToLoginScreen();
                        FirebaseCrash.logcat(Log.ERROR, TAG, "verifyOtpCode");
                        FirebaseCrash.report(e);
                    }
                }
            }
        });
    }

    private void initOptVerificationReceiver() {
        optVerificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case OPT_VERIFICATION_ARRIVED:
                        setOtp(intent.getStringExtra("otp"));
                        progressBar.setVisibility(View.GONE);
                        break;
                    case OPT_VERIFICATION_SUCCESS:
                        LoginActivity.this.setResult(RESULT_OK);
                        backToMainScreen(intent.getStringExtra("otp"));
                        break;
                    case OPT_VERIFICATION_FAILED:
                    case ERROR_OCCURS:
                        moveToLoginScreen();
                        break;
                }
            }
        };
        registerReceiver(optVerificationReceiver, getOptVerificationReceiverIntentFilter());
    }

    private void setOtp(final String otp) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                inputOtp.setText(otp);
            }
        });
    }

    private void backToMainScreen(String otp) {
        Intent intent = new Intent();
        intent.putExtra(Constants.otp, otp);
        intent.putExtra(Constants.phoneNumber, Dynamic.getMyNumber());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);

    }

//    private void setOtp(final String otp) {
//        try {
//            PreferencesUtils.getInstance(context).setOTP(otp);
//        } catch (Exception e) {
//            Log.e(TAG, "setOtp", e);
//        }
//        new Handler(getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                inputOtp.setText(otp);
//            }
//        });
//    }

    private void moveToLoginScreen() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                View view = getCurrentFocus();
                if (null != view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                mainViewPager.setCurrentItem(0, true);
                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Construct the OPT code receiver Intent filter.
    public IntentFilter getOptVerificationReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPT_VERIFICATION_ARRIVED);
        intentFilter.addAction(OPT_VERIFICATION_SUCCESS);
        intentFilter.addAction(OPT_VERIFICATION_FAILED);
        intentFilter.addAction(ERROR_OCCURS);
        return intentFilter;
    }


    private void registerSmsReceiver() {
        ComponentName component = new ComponentName(context, SmsReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void unregisterSmsReceiver() {
        ComponentName component = new ComponentName(context, SmsReceiver.class);
        if (context != null)
            context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSmsReceiver();
        unregisterOptVerificationReceiver();
    }

    private void unregisterOptVerificationReceiver() {
        if (null != optVerificationReceiver) {
            unregisterReceiver(optVerificationReceiver);
        }
    }

    // The country code spinner adapter
    private class SpinnerAdapter extends ArrayAdapter<String> {

        public Resources res;
        LayoutInflater inflater;
        private Activity activity;
        private ArrayList<String> data;

        public SpinnerAdapter(Activity activity, int textViewResourceId, ArrayList<String> countryName) {
            super(activity, textViewResourceId, countryName);

            this.activity = activity;
            data = countryName;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            // Inflate spinner_adapter_opt_activity.xml file for each row
            View row = inflater.inflate(R.layout.country_code_spinner_adapter, parent, false);

            TextView spinnerAdapterOptActivityTextView = (TextView) row.findViewById(R.id.spinnerAdapterOptActivityTextView);
            spinnerAdapterOptActivityTextView.setTypeface(fontRegular);

            spinnerAdapterOptActivityTextView.setText(data.get(position));

            return row;
        }
    }

    // Main View Pager adapter implementation
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {
            int resId = 0;
            String screenName = "";
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    screenName = "SMS";
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    screenName = "OTP";
                    break;
            }
            return findViewById(resId);
        }

        // Ref: http://stackoverflow.com/questions/26654486/how-to-resolve-unsupportedoperationexception-required-method-destroyitem-was-not
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

}
