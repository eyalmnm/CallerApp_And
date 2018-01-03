package com.em_projects.callerapp.incall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.callerapp.MainActivity;
import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.ContactsUtils;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.ImageUtils;
import com.em_projects.callerapp.utils.JSONUtils;
import com.em_projects.callerapp.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by eyal muchtar on 11/9/17.
 */

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
    private ImageView pictureImageView;
    private TextView fullNameTextView;
    private TextView phoneNumberTextView;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        FirebaseCrash.log(TAG + " onCreate"); // TODO
        context = this;
        new Dynamic(context);
        // Init UI
        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        if (Build.VERSION.SDK_INT == 26) {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        } else {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        }
        mainWindow.gravity = Gravity.CENTER_HORIZONTAL; //  | Gravity.TOP; // Gravity.RIGHT | Gravity.TOP;
        mainWindow.x = 0;
        mainWindow.y = 100;

        // Connections Map View
        inflater = LayoutInflater.from(this);
        floatingWidget = inflater.inflate(R.layout.layout_floating_layout, null);


        pictureImageView = floatingWidget.findViewById(R.id.pictureImageView);
        fullNameTextView = floatingWidget.findViewById(R.id.fullNameTextView);
        phoneNumberTextView = floatingWidget.findViewById(R.id.phoneNumberTextView);
        windowManager.addView(floatingWidget, mainWindow);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "apercu_regular.otf");
        fullNameTextView.setTypeface(typeface);
        phoneNumberTextView.setTypeface(typeface);

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
        if (false == intent.getExtras().containsKey(Constants.callerPhone)) {
            stopSelf();
            FirebaseCrash.log("Empty caller phone arrived");
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
                startTime = 0;
                String myPhone = Dynamic.getMyNumber();
                String otp = Dynamic.getMyOTP();
                String deviceId = DeviceUtils.getDeviceUniqueID(context);
                String gcmToken = Dynamic.getGcmToken(context);
                final String finalCallerPhone = callerPhone;
                FirebaseCrash.log(TAG + " sending searchPhone Request. ServerUtile is null? "
                        + (null == ServerUtilities.getInstance())); // TODO
                ServerUtilities.getInstance().searchPhone(deviceId, myPhone, otp, gcmToken, callerPhone, new CommListener() {
                    @Override
                    public void newDataArrived(String response) {
                        Log.d(TAG, "searchForCallerByPhone response: " + response);
                        if (false == StringUtils.isNullOrEmpty(response)) {
                            if (true == "no phone found".equalsIgnoreCase(response.trim())
                                    || true == StringUtils.isNullOrEmpty(response)) {
                                // Display data from phone
                                fullName = ContactsUtils.getContactName(context, finalCallerPhone);
                                if (true == StringUtils.isNullOrEmpty(fullName)) {
                                    fullName = "Unknown caller";
                                }
                                showContactData(fullName, finalCallerPhone, null);
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

    private void informServer(long duration, String e164Format, String fullName) {
        Log.d(TAG, "informServer");
        FirebaseCrash.log(TAG + " informServer"); // TODO
        String myPhone = Dynamic.getMyNumber();
        String otp = Dynamic.getMyOTP();
        String deviceId = DeviceUtils.getDeviceUniqueID(context);
        String gcmToken = Dynamic.getGcmToken(context);
        ServerUtilities.getInstance().callTerminated(deviceId, myPhone, otp, gcmToken, duration, fullName, e164Format, new CommListener() {
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
                    Bitmap circleBitmap = ImageUtils.getCircleBitmap(bitmap);
                    pictureImageView.setImageBitmap(circleBitmap);
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
                        .setContentText("Incoming call from " + fullName + " Phone number " + e164Format);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        FirebaseCrash.log(TAG + " onDestroy"); // TODO
        super.onDestroy();
        if (floatingWidget != null) windowManager.removeView(floatingWidget);
    }
}
