package com.em_projects.callerapp.ui.widgets.custom_text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.em_projects.callerapp.R;

/**
 * Created by eyalmuchtar on 12/22/17.
 */

@SuppressLint("AppCompatCustomView")
public class CustomRadioButton extends RadioButton {
    public CustomRadioButton(Context context, AttributeSet attrs) {
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
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName + ".otf");
            setTypeface(typeface);
        }
    }
}
