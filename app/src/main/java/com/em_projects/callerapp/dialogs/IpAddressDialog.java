package com.em_projects.callerapp.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.utils.StringUtils;

/**
 * Created by eyalmuchtar on 09/09/2017.
 */
// Ref: https://guides.codepath.com/android/Using-DialogFragment

public class IpAddressDialog extends DialogFragment implements View.OnClickListener {

    private String ipAddress;
    private SharedPreferences sharedPreferences;
    private EditText ipAddressEditText;
    private Button okButton;
    private Button cancelButton;
    private IpDialogClickListener listener;

    @Override
    public void onAttach(Context context) {
        listener = (IpDialogClickListener) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ip_address_dialog, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        ipAddressEditText = (EditText) view.findViewById(R.id.ipAddressEditText);
        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        if (true == getArguments().containsKey("ip_addr_pref")) {
            ipAddress = getArguments().getString("ip_addr_pref");
        }

        if (true == StringUtils.isNullOrEmpty(ipAddress)) {
            ipAddressEditText.setText(null);
            cancelButton.setEnabled(false);
        } else {
            ipAddressEditText.setText(ipAddress);
            cancelButton.setEnabled(true);
        }

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okButton:
                String ip = ipAddressEditText.getText().toString();
                if (null != listener && false == StringUtils.isNullOrEmpty(ip)) {
                    listener.okButtonClick(ip);
                }
                break;
            case R.id.cancelButton:
                if (null != listener) {
                    listener.cancelButtonClick();
                }
                break;
        }
        dismiss();
    }

    public interface IpDialogClickListener {
        public void okButtonClick(String ipAddress);

        public void cancelButtonClick();
    }

}
