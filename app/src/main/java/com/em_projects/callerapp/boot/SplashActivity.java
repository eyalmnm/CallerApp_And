package com.em_projects.callerapp.boot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.em_projects.callerapp.MainActivity;
import com.em_projects.callerapp.R;

/**
 * Created by eyalmuchtar on 25/07/2017.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    public final static int REQUEST_CODE = -1;
    public final static int OVERLAY_REQUEST_CODE = 13;

    private ImageView splashImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImageView = (ImageView) findViewById(R.id.splashImageView);


        if (Build.VERSION.SDK_INT >= 23) {
            createPermissions();
        } else {
            continueInitApp();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO : Add better handling for each permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Restart the activity
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            //mandatory permissions must be accepted...
//            if (false == createPermissions()) {
                if (checkDrawOverlayPermission(this) == false) {
                    continueInitApp();
                }
//            }
            return;
        }

        if (createPermissions() == false) continueInitApp();

    }

    private void continueInitApp() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    public boolean createPermissions() {
        String[][] permissions = new String[][]{
                {Manifest.permission.READ_SMS, "Please allow reading of activation SMS", "990"},
                {Manifest.permission.READ_PHONE_STATE, "Please allow reading of activation SMS", "992"},
                {Manifest.permission.READ_EXTERNAL_STORAGE, "Please allow us to read some files", "993"},
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please allow to write some files", "994"},
                {Manifest.permission.CALL_PHONE, "Please allow initiate calls", "995"},
                {Manifest.permission.READ_CONTACTS, "Please allow access to your contact list for showing avatar", "996"},
                {Manifest.permission.ACCESS_COARSE_LOCATION, "Please allow access to your Location", "997"},
        };
        int i = 0;
        for (final String[] permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission[0])) {

                    Snackbar.make(splashImageView, permission[1], Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission[0]}, Integer.valueOf(permission[2]));
//                                                MainActivity.this, PERMISSIONS_CONTACT,
//                                                        REQUEST_CONTACTS);
                                }
                            }).show();
                } else     // request the permission
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission[0]}, Integer.valueOf(permission[2]));
                return true;
            }
        }
        return checkDrawOverlayPermission(this);

    }

    /**
     * code to post/handler request for permission
     */
    public boolean checkDrawOverlayPermission(Context c) {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(c)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, OVERLAY_REQUEST_CODE);

                return true;
            }
        }

        return false;
    }

}
