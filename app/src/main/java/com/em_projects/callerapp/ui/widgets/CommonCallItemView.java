package com.em_projects.callerapp.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.call_log.CallLogEntry;

/**
 * Created by eyal muchtar on 3/1/18.
 */

public class CommonCallItemView extends FrameLayout {
    private CallLogEntry entry;

    private ImageView mostCallCallerAvatarImageView;
    private TextView mostCallCallerNameTextView;
    private TextView mostCallCallerPhoneTextView;

    public CommonCallItemView(@NonNull Context context, CallLogEntry entry) {
        super(context);
        init(context, entry);
    }

    public CommonCallItemView(@NonNull Context context, @Nullable AttributeSet attrs, CallLogEntry entry) {
        super(context, attrs);
        init(context, entry);
    }

    public CommonCallItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, CallLogEntry entry) {
        super(context, attrs, defStyleAttr);
        init(context, entry);
    }

    private void init(Context context, final CallLogEntry entry) {
        this.entry = entry;
        View view = inflate(context, R.layout.layout_most_call_item, null);
        addView(view);
        mostCallCallerAvatarImageView = view.findViewById(R.id.mostCallCallerAvatarImageView);
        mostCallCallerNameTextView = view.findViewById(R.id.mostCallCallerNameTextView);
        mostCallCallerPhoneTextView = view.findViewById(R.id.mostCallCallerPhoneTextView);

        mostCallCallerAvatarImageView.setImageBitmap(entry.getAvatar());
        mostCallCallerNameTextView.setText(String.valueOf(entry.getCallName()));
        mostCallCallerPhoneTextView.setText(String.valueOf(entry.getCallNumber()));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + entry.getCallNumber()));
                getContext().startActivity(intent);
            }
        });
    }


}
