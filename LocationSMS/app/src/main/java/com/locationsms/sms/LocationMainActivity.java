package com.locationsms.sms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.locationsms.sms.activities.ClientMainActivity;
import com.locationsms.sms.fragments.AlertProfileDialogFragment;
import com.locationsms.sms.fragments.HelpFragment;
import com.locationsms.sms.fragments.LocationSMSFragment;
import com.locationsms.sms.fragments.MessageFragment;
import com.locationsms.sms.fragments.NavigationDrawerFragment;
import com.locationsms.sms.fragments.ProfileFragment;
import com.locationsms.sms.utils.ClientLogs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationMainActivity extends ClientMainActivity implements NavigationDrawerFragment.FragmentDrawerListener, AlertProfileDialogFragment.NoticeDialogListener {
    private static final String TAG = "LocationMainActivity";

    private AdView mAdView;

    private static LocationSMSFragment smsFragment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private NavigationDrawerFragment navigationDrawerFragment;

    public static Intent getCallingIntent(Context context, String userName) {
        printLog("LocationMainActivity.getCallingIntent()");
        Intent intent = new Intent(context, LocationMainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_main);
        ButterKnife.bind(this);

        smsFragment = LocationSMSFragment.newInstance(null, "locationActivity");

        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0AA3D646F51740EBB63E764070D2AE79")
        .addTestDevice("FE966B87F16913A8607DF38247375B33")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        navigationDrawerFragment.setDrawerListener(this);
        replaceFragment(R.id.landing_content_frame, smsFragment);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        printLog("Drawer Position clicked" + position);
        switch (position) {
            case 0:
                setTitle(R.string.app_name);
                replaceFragment(R.id.landing_content_frame, smsFragment);
                break;
            case 1:
                setTitle(R.string.title_message);
                replaceFragment(R.id.landing_content_frame, MessageFragment.newInstance());
                break;
            case 2:
                setTitle(R.string.title_profile);
                replaceFragment(R.id.landing_content_frame, ProfileFragment.newInstance());
                break;
            case 3:
                setTitle(R.string.title_help);
                replaceFragment(R.id.landing_content_frame, HelpFragment.newInstance());
                break;
        }

    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    @Override
    public void onDialogPositiveClick(String userName) {
        printLog(userName);
        smsFragment.onDialogPositiveClick(userName);
    }

    @Override
    public void onDialogNegativeClick() {
        smsFragment.onDialogNegativeClick();
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static LocationSMSFragment getSMSFragInstance() {
        return smsFragment;
    }
}

