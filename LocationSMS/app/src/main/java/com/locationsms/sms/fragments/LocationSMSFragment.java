package com.locationsms.sms.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.locationsms.sms.R;
import com.locationsms.sms.activities.LocationSMSApplication;
import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.beans.SMSBean;
import com.locationsms.sms.database.ProfileDAO;
import com.locationsms.sms.reciever.SMSBroadcastReceiver;
import com.locationsms.sms.utils.ClientLogs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Javed.Salat on 5/28/2016.
 */
public class LocationSMSFragment extends ClientBaseFragment implements AlertProfileDialogFragment.NoticeDialogListener {
    private static final String TAG = "LocationSMSFragment";
    @BindView(R.id.et_mobile1)
    EditText etMobile1;
    @BindView(R.id.et_mobile2)
    EditText etMobile2;
    @BindView(R.id.et_msg_frequency)
    EditText etMsgFrequency;
    @BindView(R.id.et_msg_duration)
    EditText etMsgDuration;
    @BindView(R.id.btn_start)
    Button btnStart;
    private static final int PICK_CONTACT_ONE = 1;
    private static final int PICK_CONTACT_TWO = 2;
    private ProfileBean profileBean;
    private SMSBean smsBean;
    String from;
    private final static String ARGUMENT_PROFILE_BEAN = "profile_bean";
    private final static String ARGUMENT_CALL_FROM = "call_from";


    public static final String SMS_PREFERENCES = "sms_preferences";


    public static LocationSMSFragment newInstance(ProfileBean profileBean, String from) {
        LocationSMSFragment locationSMSFragment = new LocationSMSFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_PROFILE_BEAN, profileBean);
        bundle.putString(ARGUMENT_CALL_FROM, from);
        locationSMSFragment.setArguments(bundle);
        return locationSMSFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rowView = inflater.inflate(R.layout.sms_main_fragment, container, false);
        ButterKnife.bind(this, rowView);
        this.profileBean = new ProfileBean();
        this.smsBean = new SMSBean();
        this.profileBean.setSmsBean(smsBean);
        this.getActivity().setTitle(R.string.app_name);
        printLog("inside onCreate");
        etMobile1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMobile1.getRight() - etMobile1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_CONTACT_ONE);

                        return true;
                    }
                }
                return false;
            }
        });
        etMobile2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMobile2.getRight() - etMobile2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_CONTACT_TWO);


                        return true;
                    }
                }
                return false;
            }
        });


        from = getArguments().getString(ARGUMENT_CALL_FROM);

        if (this.from != null && from.equalsIgnoreCase("profile")) {
            profileBean = (ProfileBean) getArguments().getParcelable(ARGUMENT_PROFILE_BEAN);
            etMobile1.setText(profileBean.getSmsBean().getFirstNumber());
            etMobile2.setText(profileBean.getSmsBean().getSecondNumber());
            etMsgDuration.setText(profileBean.getSmsBean().getSmsDuration() + "");
            etMsgFrequency.setText(profileBean.getSmsBean().getSmsFrequency() + "");
        }
        return rowView;
    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        Calendar cal = Calendar.getInstance();

        String mobileno1 = etMobile1.getText().toString();
        String mobileno2 = etMobile2.getText().toString();
        String smsFreq = etMsgFrequency.getText().toString();
        String smsDuration = etMsgDuration.getText().toString();


        if (smsFreq.isEmpty() || smsDuration.isEmpty() || (mobileno1.isEmpty() && mobileno2.isEmpty())) {
            Toast.makeText(this.getContext(), "Please enter sms frequency and/or sms duration", Toast.LENGTH_SHORT).show();
        } else {

           /* AlertProfileDialogFragment alertDialog = new AlertProfileDialogFragment();
            alertDialog.setCancelable(false);
            alertDialog.show(getFragmentManager(), "dialog");*/
            if (from.equalsIgnoreCase("locationActivity")) {
                AlertProfileDialogFragment.newInstance().show(getFragmentManager(), "loading");
            }

            long duration = System.currentTimeMillis() + (Long.parseLong(smsDuration) * 60 * 1000);

            SharedPreferences.Editor editor = getActivity().getSharedPreferences(SMS_PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.putString("mobile1", mobileno1);
            editor.putString("mobile2", mobileno2);
            editor.putLong("duration", duration);
            editor.commit();

            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 1; i < 11; i++) {
                list.add(new Integer(i));
            }
            Collections.shuffle(list);
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(list.get(i));
            }
            int requestCode = Integer.parseInt(stringBuilder.toString());
            printLog("Request Code::" + requestCode);

            this.profileBean.getSmsBean().setFirstNumber(mobileno1);
            this.profileBean.getSmsBean().setSecondNumber(mobileno2);
            this.profileBean.getSmsBean().setSmsDuration(Integer.parseInt(smsDuration));
            this.profileBean.getSmsBean().setSmsFrequency(Integer.parseInt(smsFreq));
            this.profileBean.getSmsBean().setRequestCode(requestCode);
            if (this.from != null && this.from.equalsIgnoreCase("profile")) {
                ProfileDAO profileDAO = ProfileDAO.newInstance(getContext());
                profileDAO.updateSMSDetails(profileBean);
            }
            Intent intent = new Intent(getContext(), SMSBroadcastReceiver.class);
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), SMSBroadcastReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Integer.parseInt(smsFreq) * 1000 * 60, pIntent);
            Toast.makeText(getContext(), "Message started", Toast.LENGTH_SHORT).show();

            resetAllEditText();
        }
    }

    private void resetAllEditText() {
        etMobile1.setText("");
        etMobile2.setText("");
        etMsgDuration.setText("");
        etMsgFrequency.setText("");
    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    @Override
    public void onDialogPositiveClick(String userName) {
        printLog("Entered Name:" + userName);
        profileBean.setProfileName(userName);
        profileBean.setSmsBean(smsBean);
        profileBean.setProfileId("PI" + System.currentTimeMillis());
        ProfileDAO profileDAO = ProfileDAO.newInstance(getContext());
        profileDAO.insertProfileAndSMSDetails(profileBean, true);
        Toast.makeText(getContext(), "Profile has been saved", Toast.LENGTH_SHORT).show();
        /*TODO Call save methods from here*/
    }

    @Override
    public void onDialogNegativeClick() {
        profileBean.setSmsBean(smsBean);
        ProfileDAO profileDAO = ProfileDAO.newInstance(getContext());
        profileDAO.insertProfileAndSMSDetails(profileBean, false);

        /*TODO Call save methods from here*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        printLog(getArguments().getString(ARGUMENT_CALL_FROM));

        if (requestCode == PICK_CONTACT_ONE || requestCode == PICK_CONTACT_TWO) {
            if (resultCode == getActivity().RESULT_OK) {
                Cursor cursor = null;
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();

                    cursor = LocationSMSApplication.getContext().getContentResolver()
                            .query(result, projection, null, null, null);
                    cursor.moveToFirst();

                    if (cursor.moveToFirst()) {
                        do {
                            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String number = cursor.getString(column);
                            if (requestCode == PICK_CONTACT_ONE) {
                                etMobile1.setText(number);

                            } else {
                                etMobile2.setText(number);


                            }
                        } while (cursor.moveToNext());
                    } else {
                        //no results actions
                    }
                } catch (Exception e) {
                    //error actions
                    printLog(e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }

                }
            }
        }

    }
}
