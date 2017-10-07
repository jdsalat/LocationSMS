package com.locationsms.sms.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.locationsms.sms.R;
import com.locationsms.sms.fragments.LocationSMSFragment;
import com.locationsms.sms.reciever.SMSBroadcastReceiver;
import com.locationsms.sms.utils.ClientLogs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Javed.Salat on 5/28/2016.
 */
public class SMSServices extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SMSServices";
    protected Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;

    public SMSServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    public void cancelAlarm() {
        printLog("cancel alarm called");
        Intent intent = new Intent(getApplicationContext(), SMSBroadcastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, SMSBroadcastReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String errorMessage = "";
        StringBuilder addressMessage = new StringBuilder();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            printLog("Latitude" + mLastLocation.getLatitude());
            printLog("Longitude" + mLastLocation.getLongitude());
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }


        SharedPreferences prefs = this.getSharedPreferences(LocationSMSFragment.SMS_PREFERENCES, Context.MODE_PRIVATE);
        String mobileno1 = prefs.getString("mobile1", null);
        String mobileno2 = prefs.getString("mobile2", null);
        long duration = prefs.getLong("duration", 0);
        String msg = "http://maps.google.com/?q=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + mLastLocation.getLatitude() +
                    ", Longitude = " + mLastLocation.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
        } else {
            Address address = addresses.get(0);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            addressMessage.append("Time: ").append(dateFormat.format(calendar.getTime()));
            addressMessage.append("\nAddress: ");
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {

                addressMessage.append(address.getAddressLine(i)).append("\n");
            }
            addressMessage.append(msg);
            printLog(addressMessage.toString());

        }


        if (System.currentTimeMillis() < duration) {

            // Do the task here
            Log.i("MyTestService", "Service running");
            SmsManager smsManager = SmsManager.getDefault();
            if (!mobileno1.isEmpty()) {
                smsManager.sendTextMessage(mobileno1, null, addressMessage.toString(), null, null);
                printLog("For Number" + mobileno1);
            }

            if (!mobileno2.isEmpty()) {
                smsManager.sendTextMessage(mobileno2, null, addressMessage.toString(), null, null);
                printLog("For Number" + mobileno2);
            }
        } else {
            cancelAlarm();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
