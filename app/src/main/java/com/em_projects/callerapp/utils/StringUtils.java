package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by eyal muchtar on 27/11/2016.
 */

public class StringUtils {

    /**
     * Check whether the given string is null or empty
     *
     * @param str the string to be checked
     * @return true if the given string in null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null)
            return true;
        str = str.replace("?", "").replace("<", "").replace(">", "")
                .replace("&", "").replace("\"", "").replace("\'", "")
                .replace(";", "").replace("\n", "").replace("\r", "")
                .replace("\t", "").trim();
        if (str.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * Format the given millis to date and time string
     *
     * @param currentTime the time to be formatted
     * @return date and time string
     */
    public static String timeToFormatedString(long currentTime) {
        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date(currentTime));
        return out;
    }

    /**
     * Create MD5 Hash
     *
     * @param str String to be converted
     * @return converted md5 string
     */
    public static String toMD5Str(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder out = new StringBuilder("Bundle[");

        if (bundle == null) {
            out.append("null");
        } else {
            boolean first = true;
            for (String key : bundle.keySet()) {
                if (!first) {
                    out.append(", ");
                }

                out.append(key).append('=');

                Object value = bundle.get(key);

                if (value instanceof int[]) {
                    out.append(Arrays.toString((int[]) value));
                } else if (value instanceof byte[]) {
                    out.append(Arrays.toString((byte[]) value));
                } else if (value instanceof boolean[]) {
                    out.append(Arrays.toString((boolean[]) value));
                } else if (value instanceof short[]) {
                    out.append(Arrays.toString((short[]) value));
                } else if (value instanceof long[]) {
                    out.append(Arrays.toString((long[]) value));
                } else if (value instanceof float[]) {
                    out.append(Arrays.toString((float[]) value));
                } else if (value instanceof double[]) {
                    out.append(Arrays.toString((double[]) value));
                } else if (value instanceof String[]) {
                    out.append(Arrays.toString((String[]) value));
                } else if (value instanceof CharSequence[]) {
                    out.append(Arrays.toString((CharSequence[]) value));
                } else if (value instanceof Parcelable[]) {
                    out.append(Arrays.toString((Parcelable[]) value));
                } else if (value instanceof Bundle) {
                    out.append(bundleToString((Bundle) value));
                } else {
                    out.append(value);
                }

                first = false;
            }
        }

        out.append("]");
        return out.toString();
    }


    /**
     * Create SHA-256 Hash
     *
     * @param str String to be converted
     * @return converted md5 string
     * @Ref: https://www.mkyong.com/java/java-sha-hashing-example/
     */
    public static String toSha256Str(String str) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            Log.d("StringUtils", "toSha256Str -> Hex format : " + sb.toString());

            //convert the byte to hex format method 2
//            StringBuffer hexString = new StringBuffer();
//            for (int i = 0; i < byteData.length; i++) {
//                String hex = Integer.toHexString(0xff & byteData[i]);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            Log.d("StringUtils", "toSha256Str -> Hex format : " + hexString.toString());
//            return hexString.toString();
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("StringUtils", "toSha256Str", e);
        }
        return "";
    }

    /**
     * Convert text to Base64
     *
     * @param text the given text
     * @return the Base64 string
     * @throws UnsupportedEncodingException
     */
    public static final String toBase64(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes("UTF-8");
        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        return base64;
    }

    /**
     * Convert Base64 to text
     *
     * @param base64 the given Base64 string
     * @return return text
     * @throws UnsupportedEncodingException
     */
    public static final String fromBase64(String base64) throws UnsupportedEncodingException {
        byte[] data = Base64.decode(base64, Base64.NO_WRAP);
        String text = new String(data, "UTF-8");
        return text;
    }

    /**
     * Check whether the given string array contains the given string
     *
     * @param strArr the String array
     * @param str    the String
     * @return true if it contained
     */
    public static boolean isContained(String[] strArr, String str) {
        if (true == isNullOrEmpty(str)) return false;
        if (null == strArr || 0 == strArr.length) return false;
        for (String string : strArr) {
            if (str.equals(string)) return true;
        }
        return false;
    }

    public static boolean isPhoneNumber(String number) {
        Pattern pattern = Patterns.PHONE;
        if (true == isNullOrEmpty(number) || false == pattern.matcher(number).matches()) {
            return false;
        } else {
            return true;
        }
    }

    public static SpannableString getSpannableMessage(Context context, String whiteString, String orangeString) {
        SpannableString spanStr = new SpannableString(whiteString + '\n' + orangeString);
        spanStr.setSpan(new ForegroundColorSpan(Color.WHITE), 0, whiteString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.YELLOW), whiteString.length(),
                (whiteString.length() + orangeString.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }


    public static SpannableString getSpannableMessage(Context context, String firstString, int firstColorResId, String secondString, int secondColorResId) {
        SpannableString spanStr = new SpannableString(firstString + secondString);
        spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(firstColorResId)), 0, firstString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(secondColorResId)), secondString.length(),
                (firstString.length() + secondString.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    /**
     * Constract a String from the given array
     *
     * @param strArr the array to be used for the String
     * @param delimeter the delimiter for separate between the words
     * @return the constructed String
     */
    public static String stringArr2String(ArrayList<String> strArr, String delimeter) {
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(prefix);
            prefix = delimeter; // ","
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Check if the given number is valid phone number.
     *
     * @param number the string to be checked
     * @return true if it is valid phone number
     */
    public static boolean isValidPhoneNumber(String number) {
        if (false == StringUtils.isNullOrEmpty(number)) {
            return android.util.Patterns.PHONE.matcher(number).matches(); // number.matches("^[+]?[0-9]{10,13}$");
        }
        return false;
    }
}
