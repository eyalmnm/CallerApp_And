package com.em_projects.callerapp.storage.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

/**
 * Created by eyalmuchtar on 3/11/18.
 */
@Entity(tableName = "call_log")
public class CallLogDbEntety {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "call_number")
    private String callNumber;

    @ColumnInfo(name = "call_name")
    private String callName;

    @ColumnInfo(name = "call_date")
    private String dateString;

    @ColumnInfo(name = "call_type")
    private String callType;

    @ColumnInfo(name = "call_new")
    private String isCallNew;

    @ColumnInfo(name = "call_duration")
    private String duration;

    @ColumnInfo(name = "caller_avatar")
    private Bitmap avatar;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getIsCallNew() {
        return isCallNew;
    }

    public void setIsCallNew(String isCallNew) {
        this.isCallNew = isCallNew;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
}
