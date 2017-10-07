package com.locationsms.sms.database;

import android.content.Context;
import android.database.Cursor;

import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.beans.SMSBean;
import com.locationsms.sms.utils.ClientLogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javed.Salat on 5/29/2016.
 */
public class ProfileDAO implements Serializable {
    private static final long serialVersionUID = -5115174269443168183L;
    private static final String TAG = "ProfileDAO";
    private static ProfileDAO profileDAO = null;
    private Context mContext;
    private ProfileDBInitializer profileDBInitializer;

    public ProfileDAO(Context mContext) {
        this.mContext = mContext;
        this.profileDBInitializer = ProfileDBInitializer.newInstance(mContext);
    }

    public static ProfileDAO newInstance(Context context) {
        if (profileDAO == null) {
            profileDAO = new ProfileDAO(context);
        }
        return profileDAO;
    }

    public void insertProfileAndSMSDetails(ProfileBean profileBean, boolean isProfileToSave) {
        this.profileDBInitializer.insertProfileAndSMSDetails(profileBean, isProfileToSave);
    }

    public List<ProfileBean> getProfileBean() {
        List<ProfileBean> profileBeans = new ArrayList<>();
        ProfileBean profileBean;
        Cursor profileCursor = this.profileDBInitializer.getProfileBean();

        if (profileCursor.moveToFirst()) {
            do {
                profileBean = new ProfileBean();
                profileBean.setProfileId(profileCursor.getString(0));
                profileBean.setProfileName(profileCursor.getString(1));
                SMSBean smsBean = new SMSBean();
                smsBean.setFirstNumber(profileCursor.getString(2));
                smsBean.setSecondNumber(profileCursor.getString(3));
                smsBean.setSmsFrequency(profileCursor.getInt(4));
                smsBean.setSmsDuration(profileCursor.getInt(5));
                smsBean.setRequestCode(profileCursor.getInt(6));
                smsBean.setSmsId(profileCursor.getInt(7));
                profileBean.setSmsBean(smsBean);
                profileBeans.add(profileBean);

            } while (profileCursor.moveToNext());
        }


        return profileBeans;
    }
    public List<ProfileBean> getSMSDetails() {
        List<ProfileBean> profileBeans = new ArrayList<>();
        ProfileBean profileBean;
        Cursor profileCursor = this.profileDBInitializer.getSMSDetails();

        if (profileCursor.moveToFirst()) {
            do {
                profileBean = new ProfileBean();

                SMSBean smsBean = new SMSBean();
                smsBean.setFirstNumber(profileCursor.getString(0));
                smsBean.setSecondNumber(profileCursor.getString(1));
                smsBean.setSmsFrequency(profileCursor.getInt(2));
                smsBean.setSmsDuration(profileCursor.getInt(3));
                smsBean.setRequestCode(profileCursor.getInt(4));
                smsBean.setSmsId(profileCursor.getInt(5));
                profileBean.setSmsBean(smsBean);
                profileBeans.add(profileBean);

            } while (profileCursor.moveToNext());
        }


        return profileBeans;
    }
    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    public void deleteProfile(String profileId) {
        this.profileDBInitializer.deleteProfile(profileId);
    }
    public void deleteSMS(String smsId) {
        this.profileDBInitializer.deleteSMS(smsId);
    }

    public void updateSMSDetails(ProfileBean profileBean)
    {
        this.profileDBInitializer.updateSMSDetails(profileBean);
    }
}
