package com.em_projects.callerapp.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.gcm.RegistrationIntentService;
import com.em_projects.callerapp.main.fragments.DummyFragment;
import com.em_projects.callerapp.models.Setting;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.verification.LoginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.WAKE_LOCK;

/**
 * Created by eyalmuchtar on 05/10/2017.
 */

public class MainScreenActivity extends AppCompatActivity {
    // Setting IDs
    public static final int APP_SETTING = 100;
    public static final int USER_PROFILE = 101;
    private static final String TAG = "MainScreenActivity";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Context context;

    private SearchView searchView;

    private PowerManager.WakeLock wakeLock;

    private DrawerLayout settingLayout;
    private ListView left_drawer;
    private ArrayList<Setting> settings;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBarDrawerToggle drawerToggle;
    private View view;

    // Permissions
    // Registration \ Login (SMS Verification)
    // GCM Registeration
    // Social Network Registration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        context = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        settingLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        left_drawer = (ListView) findViewById(R.id.left_drawer);
        view = settingLayout;

        setupToolbar();

        settings = new ArrayList<Setting>();
        settings.add(new Setting(getString(R.string.settings), R.mipmap.ic_launcher_round, APP_SETTING));
        settings.add(new Setting(getString(R.string.my_profile), R.mipmap.ic_launcher_round, USER_PROFILE));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        loadAppSettingFragment(); // try without back stack

        left_drawer.setAdapter(new SettingAdapter());
        left_drawer.setOnItemClickListener(new DrawerItemClickListener());
        setupDrawerToggle();
        settingLayout.setDrawerListener(drawerToggle);

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
        return PreferencesUtils.getInstance(context).isRegisteredUser();
    }

    private void setIsRestration(String otp, String phone) {
        try {
            PreferencesUtils.getInstance(context).registerUser(phone, otp);
            PreferencesUtils.getInstance(context).setPhoneNumber(phone);
            PreferencesUtils.getInstance(context).setOTP(otp);
        } catch (Exception e) {
            e.printStackTrace();
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gcmRegistration() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            startService(intent);
        } else {
            Toast.makeText(context, "This device does not supports GCM!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean checkPermissions() {
        int receiveSms = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int readSms = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int readPhoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int internetRes = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int processOutgoingCalls = ContextCompat.checkSelfPermission(getApplicationContext(), PROCESS_OUTGOING_CALLS);
        int wakeLockRes = ContextCompat.checkSelfPermission(getApplicationContext(), WAKE_LOCK);
        int readContact = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int receiveBootComplete = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_BOOT_COMPLETED);

        return receiveSms == PackageManager.PERMISSION_GRANTED &&
                readSms == PackageManager.PERMISSION_GRANTED &&
                readPhoneState == PackageManager.PERMISSION_GRANTED &&
                internetRes == PackageManager.PERMISSION_GRANTED &&
                processOutgoingCalls == PackageManager.PERMISSION_GRANTED &&
                wakeLockRes == PackageManager.PERMISSION_GRANTED &&
                readContact == PackageManager.PERMISSION_GRANTED &&
                receiveBootComplete == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECEIVE_SMS, READ_SMS, READ_PHONE_STATE, INTERNET, PROCESS_OUTGOING_CALLS, WAKE_LOCK, READ_CONTACTS, RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraRes = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationRes = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationCRes = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean internetRes = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean contectRes = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeLockRes = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean writeFileRes = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readFileRes = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                    if (cameraRes && locationRes && locationCRes && internetRes && contectRes &&
                            wakeLockRes && writeFileRes && readFileRes) {
                        Snackbar.make(view, "Permission Granted, Now you can use the application.", Snackbar.LENGTH_LONG).show();
                        continueAppLoading();
                    } else {
                        Snackbar.make(view, "Permission Denied, You cannot access the application.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (hasPermissions(context, RECEIVE_SMS, READ_SMS, READ_PHONE_STATE, INTERNET, PROCESS_OUTGOING_CALLS, WAKE_LOCK, READ_CONTACTS, RECEIVE_BOOT_COMPLETED)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{RECEIVE_SMS, READ_SMS, READ_PHONE_STATE, INTERNET, PROCESS_OUTGOING_CALLS, WAKE_LOCK, READ_CONTACTS, RECEIVE_BOOT_COMPLETED},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
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

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchView = toolbar.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter by (newText);
                return false;
            }
        });
    }

    void setupDrawerToggle() {
        drawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, settingLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }

    /**
     * Swaps fragments in the main content view
     * if args is null then show all otherwise show the related data
     */
    private void selectItem(int position /*, Bundle args*/) {
        Fragment fragment = null;
        Bundle args = null; // TODO Remove this statement
//        switch (settings.get(position).getId()) {
//            case FIND_RECORD:
//                fragment = new FindRecordFragment();
//                break;
//            case SHOW_RECORD:
//                fragment = new ShowRecordFragment();
//                break;
//            case NEW_RECORD:
//                fragment = new NewRecordFragment();
//                break;
//            case SHOW_ALL_RECORDS:
//                fragment = new ShowAllRecordsFragment();
//                break;
//            case OPEN_GALERY:
//                fragment = new OpenGaleryFragment();
//                break;
////            case FIND_RESULTS:
////                fragment = new FindResultsFragment();
////                break;
//            default:
//                fragment = null;
//                break;
//        }

        fragment = new DummyFragment();

        if (null != fragment) {
            if (null != args) {
                fragment.setArguments(args);
            }
            showFragment(fragment);
        }
        settingLayout.closeDrawer(left_drawer);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private class SettingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return settings.size();
        }

        @Override
        public Object getItem(int i) {
            return settings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = LayoutInflater.from(context).inflate(R.layout.setting_item, viewGroup, false);
            }

            // Lookup view for data population
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView stng = (TextView) view.findViewById(R.id.stng);

            // Populate the data into the template view using the data object
            Setting setting = settings.get(i);
            img.setImageResource(setting.getIconId());
            stng.setText(setting.getName());
            view.setId(setting.getId());

            return view;
        }
    }

}