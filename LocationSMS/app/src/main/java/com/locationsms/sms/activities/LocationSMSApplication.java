package com.locationsms.sms.activities;

import android.app.Application;
import android.content.Context;

/**
 * Created by Javed.Salat on 6/2/2016.
 */
public class LocationSMSApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }


    public static Context getContext() {
        return mContext;
    }
}
