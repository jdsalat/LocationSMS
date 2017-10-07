package com.locationsms.sms.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Javed.Salat on 5/29/2016.
 */

public class ProfileBean implements Parcelable {
    private static final long serialVersionUID = -2185486015313161169L;

    private String profileId;
    private String profileName;
    private SMSBean smsBean;

    public ProfileBean() {

    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public SMSBean getSmsBean() {
        return smsBean;
    }

    public void setSmsBean(SMSBean smsBean) {
        this.smsBean = smsBean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profileId);
        dest.writeString(profileName);
        dest.writeValue(smsBean);

    }


    public static final Creator CREATOR = new Creator() {
        public ProfileBean createFromParcel(Parcel in) {
            return new ProfileBean(in);
        }

        public ProfileBean[] newArray(int size) {
            return new ProfileBean[size];
        }
    };

    public ProfileBean(Parcel in)
    {
        profileId=in.readString();
        profileName=in.readString();
        smsBean=in.readParcelable(SMSBean.class.getClassLoader());
    }
}
