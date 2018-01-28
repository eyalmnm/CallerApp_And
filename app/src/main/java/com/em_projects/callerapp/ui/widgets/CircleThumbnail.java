package com.em_projects.callerapp.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.em_projects.callerapp.R;

public class CircleThumbnail extends FrameLayout {
    private LinearLayout container;
    private LinearLayout placeholder;
    private ImageView image;
    //    private TextView dimensionsLabel;
//    private String size = ;
    private boolean minimal = false;//minimal means display just the image, no padding
    private String text = "";
    private int imageWidth = 55;
    private int imageHeight = 55;
    private int padding = 4;

    public CircleThumbnail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialise(attrs);
    }

    public CircleThumbnail(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    public CircleThumbnail(Context context) {
        super(context);
        initialise(null);
    }

    private void initialise(AttributeSet attrs) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.CircleThumbnail);

        padding = a.getInt(R.styleable.CircleThumbnail_bct_padding, padding);

        imageHeight = getHeight() - padding;
        imageWidth = getWidth() - padding;

        int imageDrawable = 0;

        if (a.getString(R.styleable.CircleThumbnail_bct_image) != null) {
            imageDrawable = a.getResourceId(R.styleable.CircleThumbnail_bct_image, 0);

        }

        if (a.getString(R.styleable.CircleThumbnail_android_text) != null) {
            text = a.getString(R.styleable.CircleThumbnail_android_text);
        }

        if (a.getString(R.styleable.CircleThumbnail_bct_minimal) != null) {
            this.minimal = a.getBoolean(R.styleable.CircleThumbnail_bct_minimal, false);
        }

        a.recycle();

        View v = inflator.inflate(R.layout.thumbnail_circle, null, false);
//        dimensionsLabel = (TextView) v.findViewById(R.id.dimensionsLabel);
        container = (LinearLayout) v.findViewById(R.id.container);
        placeholder = (LinearLayout) v.findViewById(R.id.placeholder);
        image = (ImageView) v.findViewById(R.id.image);
        float scale = getResources().getDisplayMetrics().density;


        //convert padding to pixels
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int paddingPX = (int) ((padding * scale) + 0.5);

        //convert image size to pixels
        int imageSizeWidthPX = (int) ((imageWidth * scale) + 0.5);
        int imageSizeHeightPX = (int) ((imageHeight * scale) + 0.5);

        //make inner image smaller to compensate for the padding so that entire circle including padding equals the size
        //ex. small image = 48dp, small padding = 4dp, inner image = 48 - (4 * 2) = 40
        if (this.minimal == false) {
            imageSizeWidthPX = imageSizeWidthPX - (paddingPX * 2);
            imageSizeHeightPX = imageSizeHeightPX - (paddingPX * 2);

            this.container.setPadding(paddingPX, paddingPX, paddingPX, paddingPX);
            container.setBackgroundResource(R.drawable.thumbnail_circle_container);
        } else {
            container.setBackgroundResource(R.drawable.thumbnail_circle_minimal);
        }

        //if no image is given
        if (imageDrawable == 0) {
            this.image.setVisibility(View.GONE);
            placeholder.setLayoutParams(new LinearLayout.LayoutParams(imageSizeWidthPX, imageSizeHeightPX));
            placeholder.setPadding(paddingPX, paddingPX, paddingPX, paddingPX);

            //set placeholder image
            placeholder.setBackgroundResource(R.drawable.thumbnail_circle);

//            this.dimensionsLabel.setText(text);
        } else {
            placeholder.setPadding(0, 0, 0, 0);
//            this.dimensionsLabel.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), imageDrawable);

            Bitmap roundBitmap = ImageUtils.getCircleBitmap(bitmap, imageSizeWidthPX, imageSizeHeightPX);
            image.setImageBitmap(roundBitmap);
        }

        this.addView(v);
    }

    public void setImage(int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), drawable);

        float scale = getResources().getDisplayMetrics().density;

        //convert image size to pixels
        int widthPX = (int) ((this.imageWidth * scale) + 0.5);
        int heightPX = (int) ((this.imageHeight * scale) + 0.5);

        int paddingPX = (int) ((this.padding * scale) + 0.5);

        if (this.minimal == false) {
            widthPX = widthPX - (paddingPX * 2);
            heightPX = heightPX - (paddingPX * 2);
        }

        Bitmap roundBitmap = ImageUtils.getCircleBitmap(bitmap, widthPX, heightPX);
        image.setImageBitmap(roundBitmap);

        invalidate();
        requestLayout();
    }
}
