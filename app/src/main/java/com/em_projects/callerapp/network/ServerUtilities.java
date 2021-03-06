package com.em_projects.callerapp.network;

import android.os.Build;
import android.util.Log;

import com.em_projects.callerapp.config.Constants;
import com.em_projects.callerapp.utils.StringUtils;
import com.em_projects.callerapp.utils.TimeUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import static java.lang.Thread.sleep;

/**
 * Created by eyal muchtar on 29/07/2017.
 */

public final class ServerUtilities implements Runnable {
    private static String TAG = "ServerUtilities";

    private static ServerUtilities instance = null;
    private ArrayList<CommRequest> queue = new ArrayList(10);
    private Thread runner = null;
    private boolean isRunning = false;
    private Object monitor = new Object();


    private ServerUtilities() {
        isRunning = true;
        runner = new Thread(this);
        runner.start();
    }

    public static ServerUtilities getInstance() {
        if (null == instance) {
            instance = new ServerUtilities();
        }
        return instance;
    }

    public void callTerminated(String deviceId, String myPhone, String otp, String gcmToken, String wcToken, long duration, String fullName, String e164Format, String latitude, String longitude, CommListener listener) {
        Log.d(TAG, "callTerminated");
        String serverUrl = Constants.serverURL + "/" + Constants.callTerminated;
        HashMap params = new HashMap(20);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, myPhone);
        params.put(Constants.otp, otp);
        params.put(Constants.callerPhone, e164Format);
        params.put(Constants.gcmToken, gcmToken);
        params.put(Constants.duration, String.valueOf(duration));
        params.put(Constants.fullName, fullName);
        params.put(Constants.latitude, latitude);
        params.put(Constants.longitude, longitude);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, listener);
    }

    public void sendFbToken(String deviceId, String phone, String otp, String wcToken, String fbToken, CommListener listener) {
        Log.d(TAG, "sendFbToken");
        String serverUrl = Constants.serverURL + "/" + Constants.sendFbToken;
        HashMap params = new HashMap(20);
        params.put(Constants.otp, otp);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phone);
        params.put(Constants.fbToken, fbToken);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, listener);
    }

    public void sendNewFbToken(String deviceId, String phone, String otp, String wcToken, String fbToken, String oldFbToken, CommListener listener) {
        Log.d(TAG, "sendNewFbToken");
        String serverUrl = Constants.serverURL + "/" + Constants.sendNewFbToken;
        HashMap params = new HashMap(20);
        params.put(Constants.otp, otp);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phone);
        params.put(Constants.fbToken, fbToken);
        params.put(Constants.fbOldToken, oldFbToken);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, listener);
    }

    public void searchPhone(String deviceId, String myPhone, String otp, String wcToken, String gcmToken, String searchPhone, CommListener listener) {
        Log.d(TAG, "searchPhone");
        String serverUrl = Constants.serverURL + "/" + Constants.searchPhone;
        HashMap params = new HashMap(20);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, myPhone);
        params.put(Constants.otp, otp);
        params.put(Constants.callerPhone, searchPhone);
        params.put(Constants.gcmToken, gcmToken);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, CommRequest.MethodType.GET, listener);
    }

    public void sendGcmToken(String deviceId, String phone, String gcmToken, String wcToken, CommListener listener) {
        Log.d(TAG, "sendGcmToken");
        String serverUrl = Constants.serverURL + "/" + Constants.sendGcmToken;
        HashMap params = new HashMap(20);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phone);
        params.put(Constants.gcmToken, gcmToken);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, listener);
    }

    public void sendContactsList(String deviceId, String phone, String otp, String wcToken, String contactsListJsonArray, CommListener listener) {
        Log.d(TAG, "sendContact");
        String serverUrl = Constants.serverURL + "/" + Constants.sendContatcts;
        HashMap params = new HashMap(20);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phone);
        params.put(Constants.otp, otp);
        params.put(Constants.contatcts, contactsListJsonArray);
        params.put(Constants.token, wcToken);

        post(serverUrl, params, CommRequest.MethodType.POST, listener);
    }

    public void requestSMSVerification(String deviceId, String phoneNumber, String fullName, CommListener listener) {
        Log.d(TAG, "requestSMSVerification");
        String serverUrl = Constants.serverURL + "/" + Constants.smsVerification;
        HashMap params = new HashMap(20);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phoneNumber);
        params.put(Constants.fullName, fullName);

        post(serverUrl, params, listener);
    }

    public void verifyOtpCode(String otp, String phoneNumber, String deviceId, CommListener listener) {
        Log.d(TAG, "verifyOtpCode");
        String serverUrl = Constants.serverURL + "/" + Constants.otpVerification;
        HashMap params = new HashMap(20);
        params.put(Constants.otp, otp);
        params.put(Constants.deviceId, deviceId);
        params.put(Constants.phoneNumber, phoneNumber);

        post(serverUrl, params, listener);
    }

    // Puts the request into queue for requests and add some additional data
    private void post(final String serverURL, final Map<String, String> params, CommRequest.MethodType methodType, CommListener listener) {
        Log.d(TAG, "post");

        //Amend device information for the server
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        params.put("time_zone", String.valueOf(tz.getRawOffset() / TimeUtils.HOUR)); //tz.getDisplayName());
        params.put(Constants.timeStamp, String.valueOf(TimeUtils.getTime()));
        params.put("phone_model", android.os.Build.MODEL);
        params.put("phone_manufacturer", Build.MANUFACTURER);
        params.put("version", android.os.Build.VERSION.RELEASE);
        params.put("phone_type", "Android");
        queue.add(new CommRequest(serverURL, params, methodType, listener));
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }


    // Puts the request into queue for requests and add some additional data
    private void post(final String serverURL, final Map<String, String> params, CommListener listener) {
        Log.d(TAG, "post");

        //Amend device information for the server
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        params.put("time_zone", String.valueOf(tz.getRawOffset() / TimeUtils.HOUR)); //tz.getDisplayName());
        params.put(Constants.timeStamp, String.valueOf(TimeUtils.getTime()));
        params.put("phone_model", android.os.Build.MODEL);
        params.put("phone_manufacturer", Build.MANUFACTURER);
        params.put("version", android.os.Build.VERSION.RELEASE);
        params.put("phone_type", "Android");

        queue.add(new CommRequest(serverURL, params, listener));
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    // The main looper of the server requests
    @Override
    public void run() {
        Log.d(TAG, "run");
        while (isRunning) {
            if (queue.isEmpty()) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            CommRequest requestHolder = queue.remove(0);
            handleRequest(requestHolder);
            // Stop running between the threads' creation.
            try {
                sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    // Handle a single request till returns data to listener
    private void handleRequest(final CommRequest requestHolder) {
        Log.d(TAG, "handleRequest");
        try {
            if (requestHolder != null) {
                String response = transmitData(requestHolder);
                if (requestHolder.getListener() != null) {
                    if (StringUtils.isNullOrEmpty(response)) {
                        requestHolder.getListener().exceptionThrown(new Exception());
                    } else {
                        requestHolder.getListener().newDataArrived(response);
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "handleRequest", ex);
            if (requestHolder != null && requestHolder.getListener() != null) {
                requestHolder.getListener().exceptionThrown(ex);
                FirebaseCrash.logcat(Log.ERROR, TAG, "handleRequest");
                FirebaseCrash.report(ex);
            }
        }
    }

    // Sends the request to the server. Supporting GET and POST only
    private synchronized String transmitData(CommRequest commRequest) throws IOException {
        Log.d(TAG, "transmitData");
        Map<String, String> params = commRequest.getParams();
        CommRequest.MethodType method = commRequest.getMethodType();
        String serverUrl = commRequest.getServerURL();
        FirebaseCrash.log("transmitData: " + serverUrl); // TODO
        HttpResponse httpResponse = null;
        HttpClient client = new DefaultHttpClient();

        if (method == CommRequest.MethodType.GET) {
            String body = encodeParams(params);
            String urlString = serverUrl + "?" + body;
            Log.d(TAG, "transmitData urlString: " + urlString);
            HttpGet request = new HttpGet(urlString);
            httpResponse = client.execute(request);
        } else if (method == CommRequest.MethodType.POST) {
            HttpPost httpPost = new HttpPost(serverUrl);
            Log.d(TAG, "transmitData urlString: " + serverUrl);
            ArrayList<NameValuePair> nameValuePairs = convertMapToNameValuePairs(params);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//            String data = convertMapToJson(params).toString();
//            httpPost.setEntity(new StringEntity(data, "UTF8"));
            httpResponse = client.execute(httpPost);
        }

        // Check if server response is valid
        StatusLine status = httpResponse.getStatusLine();
        if (status.getStatusCode() != 200) {
            throw new IOException("Invalid response from server: " + status.toString());
        }
        // Return result from buffered stream
        String answer = handleHttpResponse(httpResponse);
        return answer;
    }

    // constructs the GET body using the parameters
    private String encodeParams(Map<String, String> params) {
        Log.d(TAG, "encodeParams");
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            if (param.getValue() != null) {
                try {
                    bodyBuilder.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "encodeParams", e);
                }
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
        }
        return bodyBuilder.toString();
    }

    // constructs the POST body using the parameters
    private ArrayList<NameValuePair> convertMapToNameValuePairs(Map<String, String> params) {
        Log.d(TAG, "convertMapToNameValuePairs");
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            if (param.getValue() != null) {
                nameValuePairs.add(new MyNameValuePair(param.getKey(), param.getValue()));
            }
        }
        return nameValuePairs;
    }

    private JSONObject convertMapToJson(Map<String, String> params) {
        Log.d(TAG, "convertMapToJson");
        JSONObject json = new JSONObject(params);
        return json;
    }

    // Reads the returned data and convert it to String
    private String handleHttpResponse(HttpResponse httpResponse) throws IllegalStateException, IOException {
        Log.d(TAG, "handleHttpResponse");
        InputStream is = httpResponse.getEntity().getContent();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer("");
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        return stringBuffer.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        isRunning = false;
        if (null != runner) {
            runner.join();
            runner = null;
        }
        if (null != queue) {
            queue.clear();
            queue = null;
        }
        super.finalize();
    }
}
