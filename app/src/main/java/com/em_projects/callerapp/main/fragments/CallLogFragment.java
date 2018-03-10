package com.em_projects.callerapp.main.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.em_projects.callerapp.R;
import com.em_projects.callerapp.call_log.CallLogEntry;
import com.em_projects.callerapp.call_log.CallLogHelper;
import com.em_projects.callerapp.models.CallCounterPair;
import com.em_projects.callerapp.ui.widgets.CommonCallItemView;
import com.em_projects.callerapp.ui.widgets.custom_text.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by eyalmuchtar on 2/24/18.
 */

public class CallLogFragment extends Fragment {
    private static final String TAG = "CallLogFragment";

    private Context context;
    private ProgressDialog progressDialog;

    // UI Components
    private ImageView settingsImageView;
    private ImageView searchImageView;
    private CustomTextView commonCallsTextView;
    private CustomTextView showMoreTextView;
    private LinearLayout commonCallAvatarsContainer;
    private ListView callLogListView;
    private CallLogListAdapter listAdapter;
    private CircleImageView profile_image;
    private CustomTextView initialsImageView;

    private ArrayList<CallLogEntry> callLogEntries = new ArrayList<>();
    private ArrayList<CallLogEntry> commonCallsItems = new ArrayList<>();
    private CallLogLoader callLogLoader;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_log, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsImageView = view.findViewById(R.id.settingsImageView);
        searchImageView = view.findViewById(R.id.searchImageView);
        commonCallsTextView = view.findViewById(R.id.commonCallsTextView);
        showMoreTextView = view.findViewById(R.id.showMoreTextView);
        commonCallAvatarsContainer = view.findViewById(R.id.commonCallAvatarsContainer);
        callLogListView = view.findViewById(R.id.callLogListView);
        profile_image = view.findViewById(R.id.profile_image);
        initialsImageView = view.findViewById(R.id.initialsImageView);

        listAdapter = new CallLogListAdapter(context);
        callLogListView.setAdapter(listAdapter);

        progressDialog = ProgressDialog.show(context, "", "Loading");
        // Loading Call Log
        new CallLogLoader().execute();
    }

    private void makeTheCalculations(ArrayList<CallLogEntry> callLogEntries) {
        commonCallsItems = getCommonCalls(callLogEntries);
    }

    private void updateCommonCallsContainer() {
        for (int i = 0; i < 5 && i < commonCallsItems.size(); i ++) {
            commonCallAvatarsContainer.addView(new CommonCallItemView(context, commonCallsItems.get(i)));
        }
    }


    private ArrayList<CallLogEntry> getCommonCalls(ArrayList<CallLogEntry> callLogEntries) {
        // Counting all entries by call number
        HashMap<String, CallLogEntry> entries = new HashMap<>();
        HashMap<String, Integer> counters = new HashMap<>();
        for(CallLogEntry entry: callLogEntries) {
            Integer i = counters.get(entry.getCallNumber());
            if (null == i) {
                i = 0;
            }
            // Collecting call log entries uniquely.
            counters.put(entry.getCallNumber(), i + 1);
            if (false == entries.containsKey(entry.getCallNumber())) {
                entries.put(entry.getCallNumber(), entry);
            }
        }

        // Convert to ArrayLis and sort
        ArrayList<CallCounterPair<String, Integer>> countersArray = new ArrayList();
        Set<String> keys = counters.keySet();
        for (String key: keys) {
            countersArray.add(new CallCounterPair<String, Integer>(key, counters.get(key)));
        }
        // sort the counters array
        Collections.sort(countersArray, new CountersArrayComparator());

        // return the most call entries
        ArrayList<CallLogEntry> retEntries = new ArrayList<>();
        for(int i = 0; i < 5 && i < countersArray.size(); i ++) {
            CallCounterPair pair = countersArray.get(i);
            CallLogEntry entry = entries.get(pair.getKey());
            retEntries.add(entry);
        }
        return retEntries;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != callLogLoader && false == callLogLoader.isCancelled()) {
            callLogLoader.cancel(true);
            callLogLoader = null;
        }
    }


    private class CallLogLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            callLogEntries = CallLogHelper.getAllCallLogs(context);
            makeTheCalculations(callLogEntries);
            long duration = System.currentTimeMillis() - start;
            Log.d(TAG, "doInBackground duration: " + duration);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listAdapter.notifyDataSetChanged();
            updateCommonCallsContainer();
            if (null != progressDialog) { progressDialog.dismiss(); }
        }
    }

    // Call Log List Adapter
    private class CallLogListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        public CallLogListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return callLogEntries.size();
        }

        @Override
        public Object getItem(int i) {
            return callLogEntries.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            CallLogHolder holder;
            if (null == view) {
                holder = new CallLogHolder();
                view = inflater.inflate(R.layout.layout_call_log_list_item, null);
                holder.callCallCallerAvatarImageView = view.findViewById(R.id.callCallCallerAvatarImageView);
                holder.callCallCallerNameTextView = view.findViewById(R.id.callCallCallerNameTextView);
                holder.callCallCallerPhoneTextView = view.findViewById(R.id.callCallCallerPhoneTextView);
                holder.callCallCallerCallTypeImageView = view.findViewById(R.id.callCallCallerCallTypeImageView);
                holder.callCallTimeTextView = view.findViewById(R.id.callCallTimeTextView);
                holder.callCallCallerPhoneImageView = view.findViewById(R.id.callCallCallerPhoneImageView);
                holder.callCallCallerWhatsAppImageView = view.findViewById(R.id.callCallCallerWhatsAppImageView);
                holder.callCallCallerMessageImageView = view.findViewById(R.id.callCallCallerMessageImageView);
                view.setTag(holder);
            } else {
                holder = (CallLogHolder) view.getTag();
            }
            CallLogEntry entry = callLogEntries.get(position);
            holder.callCallCallerAvatarImageView.setImageBitmap(entry.getAvatar());
            holder.callCallCallerNameTextView.setText(String.valueOf(entry.getCallName()));
            holder.callCallCallerPhoneTextView.setText(String.valueOf(entry.getCallNumber()));
            holder.callCallCallerCallTypeImageView.setImageDrawable(context.getResources()
                    .getDrawable(entry.getCallTypeImage()));
            holder.callCallTimeTextView.setText(String.valueOf(entry.getDateString()));
            return view;
        }
    }

    // Call Log UI Holder
    public class CallLogHolder {
        // Call Data
        ImageView callCallCallerAvatarImageView;
        TextView callCallCallerNameTextView;
        TextView callCallCallerPhoneTextView;
        ImageView callCallCallerCallTypeImageView;
        TextView callCallTimeTextView;
        //TextView callCallCallerDateTextView;
        // Actions
        ImageView callCallCallerPhoneImageView;
        ImageView callCallCallerWhatsAppImageView;
        ImageView callCallCallerMessageImageView;
    }

    // Call Log Pair comparator
    public class CountersArrayComparator implements Comparator<CallCounterPair> {

        @Override
        public int compare(CallCounterPair left, CallCounterPair right) {
            int lVal = (Integer) left.getValue();
            int rVal = (Integer) right.getValue();
            if (lVal == rVal) return 0;
            else if (lVal < rVal) return 1;
            else return -1;
        }
    }
}
