package com.locationsms.sms.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.locationsms.sms.services.SMSServices;

/**
 * Created by poonam.deshmukh on 24-05-2016.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SMSServices.class);
        context.startService(i);
    }
}
