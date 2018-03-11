package com.em_projects.callerapp.call_log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.storage.room.CallLogDbEntety;
import com.em_projects.callerapp.utils.ImageUtils;

/**
 * Created by eyalmuchtar on 2/22/18.
 */

public class CallLogEntry {
    private static final String TAG = "CallLogEntry";

    private Context context;
    private String callNumber;
    private String callName;
    private String dateString;
    private String callType;
    private String isCallNew;
    private String duration;
    private Bitmap avatar;

    public CallLogEntry(Context context, String callNumber, String callName, String dateString,
                        String callType, String isCallNew, String duration, Bitmap avatar) {
        this.context = context;
        this.callNumber = callNumber;
        this.callName = callName;
        this.dateString = dateString;
        this.callType = callType;
        this.isCallNew = isCallNew;
        this.duration = duration;
        if (null == avatar) {
            loadingAvatar();
        } else {
            this.avatar = avatar;
        }
    }

    public CallLogEntry(Context context, CallLogDbEntety dbEntety) {
        this(context, dbEntety.getCallNumber(), dbEntety.getCallName(), dbEntety.getDateString()
                , dbEntety.getCallType(), dbEntety.getIsCallNew(), dbEntety.getDuration()
                , ImageUtils.byteArray2Bitmap(dbEntety.getAvatar()));
    }

    private void loadingAvatar() {
        // TODO Loading from sever
        Log.e(TAG, "loadingAvatar TODO Loading from sever!!!   *********");
        avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_blank_photo);
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public String getDateString() {
        return dateString;
    }

    public String getCallType() {
        return callType;
    }

    public String getIsCallNew() {
        return isCallNew;
    }

    public String getDuration() {
        return duration;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public int getCallTypeImage() {
        if (true == callType.equalsIgnoreCase("Outgoing")) {
            return R.drawable.layer_48;
        }
        if (true == callType.equalsIgnoreCase("Incoming")) {
            return R.drawable.layer_48_copy;
        }
        return R.drawable.layer_49;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallLogEntry that = (CallLogEntry) o;

        if (!callNumber.equals(that.callNumber)) return false;
        if (!callName.equals(that.callName)) return false;
        if (!dateString.equals(that.dateString)) return false;
        if (!callType.equals(that.callType)) return false;
        if (!isCallNew.equals(that.isCallNew)) return false;
        return duration.equals(that.duration);
    }

    @Override
    public int hashCode() {
        int result = callNumber.hashCode();
        result = 31 * result + callName.hashCode();
        result = 31 * result + dateString.hashCode();
        result = 31 * result + callType.hashCode();
        result = 31 * result + isCallNew.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CallLogEntry{" +
                "callNumber='" + callNumber + '\'' +
                ", callName='" + callName + '\'' +
                ", dateString='" + dateString + '\'' +
                ", callType='" + callType + '\'' +
                ", isCallNew='" + isCallNew + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
