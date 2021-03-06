package com.em_projects.callerapp.ui.widgets.custom_text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.em_projects.callerapp.R;


/**
 * Created by eyalmuchtar on 20/12/2017.
 */
@SuppressLint("AppCompatCustomView")
public class CustomButton extends Button {

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Typeface.createFromAsset doesn't work in the layout editor.
        // Skipping...
        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String fontName = styledAttrs.getString(R.styleable.CustomTextView_custom_font);
        styledAttrs.recycle();

        if (fontName != null) {
            AssetManager assetManager = context.getAssets();
            Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontName + ".otf");
            setTypeface(typeface);
        }
    }
}
