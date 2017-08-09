package com.em_projects.callerapp.ui.widgets;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created with IntelliJ IDEA.
 * User: MediumMG
 * Date: 02.07.13
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class CustomFont {

    /**
     * <p>Create Typeface class for Apercu regular font</p>
     * @param context current application context
     * @return Typeface class specifies the typeface and intrinsic style of a font
     */
    public static Typeface fontApercuRegular(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "apercu_regular.otf");
        return font;
    }
    /**
     * <p>Create Typeface class for Apercu Medium font</p>
     * @param context current application context
     * @return Typeface class specifies the typeface and intrinsic style of a font
     */
    public static Typeface fontApercuMedium(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "apercu_medium.otf");
        return font;
    }
    /**
     * <p>Create Typeface class for Manus font</p>
     * @param context current application context
     * @return Typeface class specifies the typeface and intrinsic style of a font
     */
    public static Typeface fontManus(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "manus.otf");
        return font;
    }
}
