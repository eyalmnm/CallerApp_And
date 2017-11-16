package com.em_projects.callerapp.incall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.config.Dynamic;
import com.em_projects.callerapp.network.CommListener;
import com.em_projects.callerapp.network.ServerUtilities;
import com.em_projects.callerapp.utils.DeviceUtils;
import com.em_projects.callerapp.utils.DimenUtils;
import com.em_projects.callerapp.utils.StringUtils;

/**
 * Created by eyal muchtar on 11/9/17.
 */

public class InCallService extends Service {
    private static final String TAG = "InCallService";

    private Context context;
    private Point displaySize = new Point();

    // UI Components
    private WindowManager windowManager;
    private WindowManager.LayoutParams mainWindow;
    private RelativeLayout mainView;
    private ImageView avatarImageView;
    private TextView phoneTextView;
    private LayoutInflater inflater;
    private View mapView;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // Init UI
        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        // Logo Image View
        // TODO Consider it....

        mainWindow = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |
                        WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.TRANSLUCENT);
        mainWindow.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP; // Gravity.RIGHT | Gravity.TOP;
        mainWindow.x = 0;

        mainView = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DimenUtils.dpToPx(5), DimenUtils.dpToPx(5), DimenUtils.dpToPx(5), DimenUtils.dpToPx(5));
        mainView.setLayoutParams(layoutParams);

        // Avatar Image View
        RelativeLayout.LayoutParams avatarLayoutParams = new RelativeLayout.LayoutParams(DimenUtils.dpToPx(60), DimenUtils.dpToPx(60));
        avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        avatarLayoutParams.setMarginEnd(DimenUtils.dpToPx(5));

        avatarImageView = new ImageView(context);
        avatarImageView.setAdjustViewBounds(true);
        avatarImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        avatarImageView.setLayoutParams(avatarLayoutParams);
        mainView.addView(avatarImageView);

        // Phone Text View
        RelativeLayout.LayoutParams phoneLayoutParams = new RelativeLayout.LayoutParams(DimenUtils.dpToPx(200),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        phoneLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, avatarImageView.getId());
        phoneLayoutParams.addRule(RelativeLayout.ALIGN_START, avatarImageView.getId());
        phoneLayoutParams.addRule(RelativeLayout.ALIGN_TOP, avatarImageView.getId());
        phoneLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, avatarImageView.getId());
        phoneLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        phoneTextView = new TextView(context);
        phoneTextView.setTextColor(getResources().getColor(R.color.app_text_color));
        phoneTextView.setTextSize(getResources().getDimension(R.dimen.text_size_reg));
        phoneTextView.setGravity(Gravity.RIGHT);
        phoneTextView.setGravity(Gravity.START);
        phoneTextView.setPadding(DimenUtils.dpToPx(1), DimenUtils.dpToPx(1), DimenUtils.dpToPx(1), DimenUtils.dpToPx(1));

        phoneTextView.setLayoutParams(phoneLayoutParams);
        mainView.addView(phoneTextView);

        // Buttons
        RelativeLayout.LayoutParams buttonsLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonsLayoutParams.addRule(RelativeLayout.BELOW, avatarImageView.getId());
        buttonsLayoutParams.setMargins(0, DimenUtils.dpToPx(5), 0, DimenUtils.dpToPx(5));

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setGravity(Gravity.CENTER);
        buttonsLayout.setPadding(DimenUtils.dpToPx(5), 0, DimenUtils.dpToPx(5), 0);
        // TODO Add Buttons Here

        buttonsLayout.setLayoutParams(buttonsLayoutParams);
        mainView.addView(buttonsLayout);


        // Connections Map View
        inflater = LayoutInflater.from(this);
        //mapView = inflater.inflate()

        windowManager.addView(mainView, mainWindow);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String callerPhone = null;
        context = this;
        if (true == intent.getExtras().containsKey(Constants.callerPhone)) {
            callerPhone = intent.getStringExtra(Constants.callerPhone);
            if (false == StringUtils.isNullOrEmpty(callerPhone)) {
                String myPhone = Dynamic.getMyNumber();
                String otp = Dynamic.getMyOTP();
                String deviceId = DeviceUtils.getDeviceUniqueID(context);
                ServerUtilities.getInstance().searchPhone(deviceId, myPhone, otp, callerPhone, new CommListener() {
                    @Override
                    public void newDataArrived(String response) {
                        Log.d(TAG, "searchForCallerByPhone response: " + response);
                        if (false == StringUtils.isNullOrEmpty(response)) {
                            if (true == "no phone found".equalsIgnoreCase(response.trim())) {
                                // Display data from phone
                            } else {
                                // show caller data
                            }
                        }
                        // response: no phone found
                    }

                    @Override
                    public void exceptionThrown(Throwable throwable) {
                        Log.e(TAG, "searchForCallerByPhone" + throwable);
                    }
                });

            }
        }
        return START_STICKY;
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // destroy UI
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
