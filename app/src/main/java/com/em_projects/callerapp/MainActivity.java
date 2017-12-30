package com.em_projects.callerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.em_projects.callerapp.intro.IntroActivity;
import com.em_projects.callerapp.main.MainScreenActivity;
import com.em_projects.callerapp.tracer.ExceptionHandler;
import com.em_projects.callerapp.utils.AppUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity /*implements IpAddressDialog.IpDialogClickListener*/ {
    private static final String TAG = "MainActivity";

    private Context context;

    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        analytics = FirebaseAnalytics.getInstance(this);    // TODO Add Analytics

        context = this;

        TextView versionNumber = findViewById(R.id.version_number);
        versionNumber.setText(getString(R.string.app_version_format,
                AppUtils.getAppVersion(this), AppUtils.getAppVerionCode(this)));

        ExceptionHandler.register(this);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextScreen();
//                showIpDialog();
            }
        }, 3000);
    }

    private void moveToNextScreen() {
        Intent intent = null;
        if (true == PreferencesUtils.getInstance(this).getIntroIsShown()) {
            intent = new Intent(context, MainScreenActivity.class);
        } else {
            intent = new Intent(context, IntroActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
        finish();
    }

//    private void showIpDialog() {
//        FragmentManager fm = getFragmentManager();
//        IpAddressDialog dialog = new IpAddressDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString("ip_addr_pref", Constants.serverURL);
//        dialog.setArguments(bundle);
//        dialog.show(fm, "IpAddressDialog");
//    }

//    @Override
//    public void okButtonClick(String ipAddress) {
//        if (false == StringUtils.isNullOrEmpty(ipAddress)) {
//            Constants.serverURL = ipAddress;
//        }
//        moveToNextScreen();
//    }

//    @Override
//    public void cancelButtonClick() {
//        moveToNextScreen();
//    }
}
