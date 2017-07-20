package com.em_projects.callerapp.telephony;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * This class shall translate any given number and return eventually the formated number into international format
 */
public class PhoneNumber {
    private final String TAG = "PhoneNumber";
    private String mPhoneNumber;
    private Context mContext;
    private String mCountryCode;

    public PhoneNumber(Context context) {
        mContext = context;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
    }

    public PhoneNumber(PhoneNumber n, Context context) {
        mContext = context;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        if (!From(n.getNumber()))
            throw new NumberFormatException();
    }

    public PhoneNumber(String number, Context context) {
//        PhoneNumberUtils.formatNumber()
        mContext = context;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mCountryCode = tm.getSimCountryIso();
        if (!From(number)) { // Failed copying numbre
            //     throw new NumberFormatException();
            Log.d(TAG, "Failed to normalize phone number " + number);
            mPhoneNumber = number;
        }
    }

    public void Clear() {
        mPhoneNumber = "";
    }

    public String getNumber() {
        return mPhoneNumber;
    }

    public boolean From(String number) {
        try {
            PhoneNumberUtils pnu = new PhoneNumberUtils();//PhoneNumberUtils..getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPhoneNumber = pnu.formatNumberToE164(number, mCountryCode.toUpperCase());
//                if(mPhoneNumber.charAt(0)==(char)'+') {
//                    mPhoneNumber = mPhoneNumber.substring(1);
//                mPhoneNumber = PhoneNumberUtils.normalizeNumber(mPhoneNumber);
                if (mPhoneNumber.charAt(0) == (char) '+') {
                    mPhoneNumber = mPhoneNumber.substring(1);
                }
            } else
                mPhoneNumber = number;

//            mPhoneNumber = PhoneNumberUtils.normalizeNumber(mPhoneNumber);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mCountryCode='" + mCountryCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (!mPhoneNumber.equals(that.mPhoneNumber)) return false;
        return mCountryCode.equals(that.mCountryCode);

    }

    @Override
    public int hashCode() {
        int result = mPhoneNumber.hashCode();
        result = 31 * result + mCountryCode.hashCode();
        return result;
    }
}
