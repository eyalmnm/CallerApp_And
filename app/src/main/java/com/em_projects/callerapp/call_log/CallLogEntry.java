package com.em_projects.callerapp.call_log;

/**
 * Created by eyalmuchtar on 2/22/18.
 */

public class CallLogEntry {

    private String callNumber;
    private String callName;
    private String dateString;
    private String callType;
    private String isCallNew;
    private String duration;

    public CallLogEntry(String callNumber, String callName, String dateString, String callType, String isCallNew, String duration) {
        this.callNumber = callNumber;
        this.callName = callName;
        this.dateString = dateString;
        this.callType = callType;
        this.isCallNew = isCallNew;
        this.duration = duration;
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
