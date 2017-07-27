package com.em_projects.callerapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by eyalmuchtar on 26/07/2017.
 */

public class DailerUtils {

    public static void dail(Context context, String tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        context.startActivity(intent);
    }

    public static void call(Context context, String tel) throws ActivityNotFoundException, SecurityException {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + tel));
        context.startActivity(intent);
    }
}
