package com.em_projects.callerapp.incall;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.callerapp.MainActivity;
import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.ui.widgets.custom_text.CustomTextView;
import com.em_projects.callerapp.utils.ContactsUtils;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.JSONUtils;
import com.em_projects.callerapp.utils.PreferencesUtils;
import com.em_projects.callerapp.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by eyal muchtar on 11/9/17.
 */

// Ref: https://stackoverflow.com/questions/44425584/context-startforegroundservice-did-not-then-call-service-startforeground
// Ref: https://stackoverflow.com/questions/27185989/missed-call-detection-in-android
// Ref: http://www.truiton.com/2014/10/android-foreground-service-example/
// Ref: https://stackoverflow.com/questions/26112150/android-create-circular-image-with-picasso
// Ref: https://stackoverflow.com/questions/3629179/android-activity-over-default-lock-screen
// Ref: https://stackoverflow.com/questions/37138546/when-adding-view-to-window-with-windowmanager-layoutparams-type-system-overlay/37348311#37348311
// Ref: https://stackoverflow.com/questions/45158696/android-view-windowmanagerbadtokenexception-unable-to-add-window-android-view

public class InCallService extends Service {
    private static final String TAG = "InCallService";

    private Context context;
    private Point displaySize = new Point();
    private long startTime = 0;
    private String fullName, nationalFormat, e164Format, picture;
    private Bitmap bitmap = null;

    // UI Components
    private LayoutInflater inflater;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mainWindow;
    private View floatingWidget;
    private RelativeLayout mainBgImageView;
    private CircleImageView pictureImageView;
    private TextView fullNameTextView;
    private TextView phoneNumberTextView;
    private CustomTextView initialsImageView;

    // Location
    private Thread runner;
    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        FirebaseCrash.log(TAG + " onCreate");
        context = this;
        new Dynamic(context);
        // Start Foreground service
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "wizecall_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "WizeCall Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        // Init UI
        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        if (Build.VERSION.SDK_INT == 26) {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        } else {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        }
        mainWindow.gravity = Gravity.CENTER; //  | Gravity.TOP; // Gravity.RIGHT | Gravity.TOP;
        //mainWindow.x = 0;
        //mainWindow.y = 100;

        // Connections Map View
        inflater = LayoutInflater.from(this);
        floatingWidget = inflater.inflate(R.layout.layout_floating_layout, null);

        mainBgImageView = floatingWidget.findViewById(R.id.mainBgImageView);
        initialsImageView = floatingWidget.findViewById(R.id.initialsImageView);
        pictureImageView = floatingWidget.findViewById(R.id.profile_image);
        fullNameTextView = floatingWidget.findViewById(R.id.callerNameCustomTextView);
        phoneNumberTextView = floatingWidget.findViewById(R.id.callerPhoneCustomTextView);
        windowManager.addView(floatingWidget, mainWindow);

//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/apercu_regular.otf");
//        fullNameTextView.setTypeface(typeface);
//        phoneNumberTextView.setTypeface(typeface);
        initialsImageView.setVisibility(View.GONE);

        floatingWidget.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.w(TAG, "ACTION_DOWN");
                        initialX = mainWindow.x;
                        initialY = mainWindow.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff > 10 || Ydiff > 10) {
                            Log.w(TAG, "XDiff: " + Xdiff + "  YDiff: " + Ydiff);
                            if (Math.abs(Xdiff) > Math.abs(Ydiff * 5)) {  // Swipe to Hide
                                floatingWidget.setVisibility(View.GONE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainWindow.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mainWindow.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingWidget, mainWindow);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        FirebaseCrash.log(TAG + " onStartCommand"); // TODO
        String callerPhone = null;
//        if (null == intent) {
//            FirebaseCrash.log("Intent is null in InCallService!!!");
//            stopSelf();
//            return START_NOT_STICKY;
//        }
        if (null == intent || false == intent.getExtras().containsKey(Constants.callerPhone)) {
            stopSelf();
            FirebaseCrash.log("Empty caller phone or intent is null");
            Log.e(TAG, "Empty caller phone or intent is null");
            return START_STICKY;
        }
        if (true == intent.getExtras().containsKey("offhook")) {
            startTime = System.currentTimeMillis();
        } else if (true == intent.getExtras().containsKey("idle")) {
            long duration = System.currentTimeMillis() - startTime;
            informServer(duration, e164Format, fullName);
            if (0 == startTime) {  // Missed call
                showNotification(fullName, e164Format);
            }
            // stopSelf();
        } else if (true == intent.getExtras().containsKey("ringing")) {
            callerPhone = intent.getStringExtra(Constants.callerPhone);
            if (false == StringUtils.isNullOrEmpty(callerPhone)) {
                e164Format = callerPhone;
                fullName = ContactsUtils.getContactName(context, callerPhone);
                if (true == StringUtils.isNullOrEmpty(fullName)) {
                    fullName = "";
                }
                bitmap = ContactsUtils.retrieveContactPhoto(context, callerPhone);
                showContactData(fullName, callerPhone, bitmap);
                startTime = 0;
                String myPhone = Dynamic.getMyNumber();
                String otp = Dynamic.getMyOTP();
                String deviceId = DeviceUtils.getDeviceUniqueID(context);
                String gcmToken = Dynamic.getGcmToken(context);
                startLocationFinder(); // TODO Use new thread
                final String finalCallerPhone = callerPhone;
                String wcToken = PreferencesUtils.getInstance(context).getWCToken();
                if (true == StringUtils.isNullOrEmpty(wcToken)) wcToken = "";
                FirebaseCrash.log(TAG + " sending searchPhone Request. ServerUtile is null? "
                        + (null == ServerUtilities.getInstance())); // TODO
                ServerUtilities.getInstance().searchPhone(deviceId, myPhone, otp, wcToken, gcmToken, callerPhone, new CommListener() {
                    @Override
                    public void newDataArrived(String response) {
                        Log.d(TAG, "searchForCallerByPhone response: " + response);
                        if (false == StringUtils.isNullOrEmpty(response)) {
                            if (true == "no phone found".equalsIgnoreCase(response.trim())
                                    || true == StringUtils.isNullOrEmpty(response)) {
                                // Display data from phone
//                                e164Format = finalCallerPhone;
//                                fullName = ContactsUtils.getContactName(context, finalCallerPhone);
//                                if (true == StringUtils.isNullOrEmpty(fullName)) {
//                                    fullName = "";
//                                }
//                                showContactData(fullName, finalCallerPhone, null);
                                // Do Nothing.
                            } else {
                                // show caller data
                                try {
                                    JSONObject callerData = new JSONObject(response);
                                    fullName = JSONUtils.getStringValue(callerData, "full_name");
                                    nationalFormat = JSONUtils.getStringValue(callerData, "nationalFormat");
                                    e164Format = JSONUtils.getStringValue(callerData, "e164Format");
                                    picture = JSONUtils.getStringValue(callerData, "picture");
                                    try {
                                        URL url = new URL(picture);
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    } catch (Exception ex) {
                                        Log.e(TAG, "newDataArrived", ex);
                                    }
                                    showContactData(fullName, e164Format, bitmap);
                                } catch (JSONException ex) {
                                    FirebaseCrash.logcat(Log.ERROR, TAG, "searchPhone newDataArrived");
                                    FirebaseCrash.report(ex);
                                    Log.e(TAG, "searchPhone newDataArrived", ex);
                                }
                            }
                        }
                    }

                    @Override
                    public void exceptionThrown(Throwable throwable) {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "searchPhone exceptionThrown");
                        FirebaseCrash.report(throwable);
                        Log.e(TAG, "searchForCallerByPhone" + throwable);
                    }
                });
            }
        }
        return START_STICKY;
    }

    private void startLocationFinder() {
        runner = new Thread(new Runnable() {
            @Override
            public void run() {
                location = getLastKnownLocation();
            }
        });
        runner.start();
    }

    private void informServer(long duration, String e164Format, String fullName) {
        Log.d(TAG, "informServer");
        FirebaseCrash.log(TAG + " informServer"); // TODO
        double longitudeDbl = -1000;
        double latitudeDbl = -1000;
        if (null != location) {
            longitudeDbl = location.getLongitude();
            latitudeDbl = location.getLatitude();
        }
        String longitude = String.valueOf(longitudeDbl);
        String latitude = String.valueOf(latitudeDbl);
        String myPhone = Dynamic.getMyNumber();
        String otp = Dynamic.getMyOTP();
        String deviceId = DeviceUtils.getDeviceUniqueID(context);
        String gcmToken = Dynamic.getGcmToken(context);
        String wcToken = PreferencesUtils.getInstance(context).getWCToken();
        if (true == StringUtils.isNullOrEmpty(wcToken)) wcToken = "";
        ServerUtilities.getInstance().callTerminated(deviceId, myPhone, otp, gcmToken, wcToken, duration, fullName, e164Format, latitude, longitude, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
            }
        });
    }

    private void showContactData(final String fullName, final String e164Format, final Bitmap bitmap) {
        Log.d(TAG, "showContactData");
        FirebaseCrash.log(TAG + " showContactData"); // TODO
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                phoneNumberTextView.setText(e164Format);
                fullNameTextView.setText(fullName);
                if (null != bitmap) {
//                    Bitmap circleBitmap = ImageUtils.getCircleBitmap(bitmap);
//                    pictureImageView.setImageBitmap(circleBitmap);
                    initialsImageView.setVisibility(View.INVISIBLE);
                    pictureImageView.setVisibility(View.VISIBLE);
                    pictureImageView.setImageBitmap(bitmap);
                } else if (false == StringUtils.isNullOrEmpty(fullName) && null == bitmap) {
                    pictureImageView.setVisibility(View.INVISIBLE);
                    initialsImageView.setVisibility(View.VISIBLE);
                    initialsImageView.setText(StringUtils.getInitials(fullName));
                }
                floatingWidget.invalidate();
            }
        });

    }


    private void showNotification(String fullName, String e164Format) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("CallWize")
                        .setOngoing(false)
                        .setContentText("Incoming call from " +
                                (false == StringUtils.isNullOrEmpty(fullName) ? fullName : e164Format));
        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        nManager.notify(NOTIFICATION_ID, notification);
    }

    private void showMessage(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        Location bestLocation = null;
        try {
            LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);

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
        } catch (Exception ex) {
            Log.e(TAG, "getLastKnownLocation", ex);
            FirebaseCrash.logcat(Log.ERROR, TAG, "getLastKnownLocation");
            FirebaseCrash.report(ex);
        } finally {
            return bestLocation;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (null != runner) {
            try {
                runner.join();
            } catch (InterruptedException e) {
            }
            runner = null;
        }
        FirebaseCrash.log(TAG + " onDestroy"); // TODO
        super.onDestroy();
        if (floatingWidget != null) windowManager.removeView(floatingWidget);
    }
}
