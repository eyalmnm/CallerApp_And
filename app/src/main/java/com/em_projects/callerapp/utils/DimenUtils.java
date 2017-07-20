package com.em_projects.callerapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by eyal muchtar on 21/05/2017.
 */

public class DimenUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int strLengthToPx(TextView textView, String text) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        return width;
    }

    public static Point getScrDimensPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
//        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
        return size;
    }

    public static int getScrWidthPx(Context context) {
        Point scrDimens = getScrDimensPx(context);
        return scrDimens.x;
    }

    public static int getXPosition(View view) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        int x = position[0];
        int y = position[1];
        return x;
    }

    public static Point getPosition(View view) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        int x = position[0];
        int y = position[1];
        return new Point(x, y);
    }

    public static Point getViewWidthAndHieght(View view) {
        if (null == view) {
            return new Point(0, 0);
        } else {
            view.measure(0, 0);
            return new Point(view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }
}
