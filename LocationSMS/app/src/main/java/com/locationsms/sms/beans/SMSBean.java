package com.locationsms.sms.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Javed.Salat on 30-05-2016.
 */
public class SMSBean implements Parcelable {
    private int smsId;
    private String firstNumber;
    private String secondNumber;
    private int smsFrequency;
    private int smsDuration;
    private int requestCode;

    public int getSmsId() {
        return smsId;
    }

    public void setSmsId(int smsId) {
        this.smsId = smsId;
    }

    public String getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(String firstNumber) {
        this.firstNumber = firstNumber;
    }

    public String getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(String secondNumber) {
        this.secondNumber = secondNumber;
    }

    public int getSmsFrequency() {
        return smsFrequency;
    }

    public void setSmsFrequency(int smsFrequency) {
        this.smsFrequency = smsFrequency;
    }

    public int getSmsDuration() {
        return smsDuration;
    }

    public void setSmsDuration(int smsDuration) {
        this.smsDuration = smsDuration;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(smsId);
        dest.writeString(firstNumber);
        dest.writeString(secondNumber);
        dest.writeInt(smsFrequency);
        dest.writeInt(smsDuration);
        dest.writeInt(requestCode);
    }

    public static final Creator CREATOR = new Creator() {
        public SMSBean createFromParcel(Parcel in) {
            return new SMSBean(in);
        }

        public SMSBean[] newArray(int size) {
            return new SMSBean[size];
        }
    };

    public SMSBean(Parcel in) {
        smsId = in.readInt();
        firstNumber = in.readString();
        secondNumber = in.readString();
        smsFrequency = in.readInt();
        smsDuration = in.readInt();
        requestCode = in.readInt();
    }

    public SMSBean() {
    }
}
