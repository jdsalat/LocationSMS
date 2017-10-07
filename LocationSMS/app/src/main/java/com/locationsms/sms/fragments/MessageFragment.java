package com.locationsms.sms.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.locationsms.sms.R;
import com.locationsms.sms.adapters.MessageAdapter;
import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.database.ProfileDAO;
import com.locationsms.sms.reciever.SMSBroadcastReceiver;
import com.locationsms.sms.utils.ClientLogs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Javed.Salat on 6/4/2016.
 */
public class MessageFragment extends ClientBaseFragment implements MessageAdapter.OnClickItemListener {
    private static final String TAG = "MessageFragment";
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ProfileDAO profileDAO;
    List<ProfileBean> profileBeanList;
    MessageAdapter messageAdapter;

    public static MessageFragment newInstance() {
        MessageFragment messageFragment = new MessageFragment();
        return messageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_fragment, container, false);
        ButterKnife.bind(this, rootView);
        this.getActivity().setTitle(R.string.title_message);
        this.profileDAO = ProfileDAO.newInstance(getContext());
        profileBeanList = this.profileDAO.getSMSDetails();
        if (profileBeanList.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            messageAdapter = new MessageAdapter(profileBeanList, MessageFragment.this);
            recyclerView.setAdapter(messageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = messageAdapter.getPosition();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        printLog("position::" + position);
        switch (item.getItemId()) {
            case MessageAdapter.iD:
                removeDataFromList(position);
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void removeDataFromList(int position) {
        if (!profileBeanList.isEmpty()) {
            this.profileDAO = ProfileDAO.newInstance(getContext());
            cancelAlarm(profileBeanList.get(position).getSmsBean().getRequestCode());
            this.profileDAO.deleteSMS(profileBeanList.get(position).getSmsBean().getSmsId() + "");
            this.profileBeanList.remove(position);
            this.messageAdapter.notifyDataSetChanged();
            if (profileBeanList.isEmpty()) {
                noData.setVisibility(View.VISIBLE);
            }
        } else {

            noData.setVisibility(View.VISIBLE);
        }
    }

    public void cancelAlarm(int request_code) {
        printLog("cancel alarm called");
        Intent intent = new Intent(getContext(), SMSBroadcastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), request_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    @Override
    public void onItemClick(int position) {

    }
}
