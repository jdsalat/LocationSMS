package com.locationsms.sms.fragments;

import android.os.Bundle;
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
import com.locationsms.sms.adapters.ProfileAdapter;
import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.database.ProfileDAO;
import com.locationsms.sms.utils.ClientLogs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Javed.Salat on 5/29/2016.
 */
public class ProfileFragment extends ClientBaseFragment implements ProfileAdapter.OnClickItemListener {
    private static final String TAG = "ProfileFragment";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noData)
    TextView noData;
    ProfileAdapter profileAdapter;
    ProfileDAO profileDAO;
    List<ProfileBean> profileBeanList;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();

        return profileFragment;
    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rowView = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, rowView);
        this.getActivity().setTitle(R.string.title_profile);
        this.profileDAO = ProfileDAO.newInstance(getContext());
        profileBeanList = this.profileDAO.getProfileBean();
        if (profileBeanList.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            profileAdapter = new ProfileAdapter(profileBeanList, ProfileFragment.this);
            recyclerView.setAdapter(profileAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        return rowView;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = profileAdapter.getPosition();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        printLog("position::" + position);
        switch (item.getItemId()) {
            case ProfileAdapter.iD:
                removeDataFromList(position);
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void removeDataFromList(int position) {
        if (!profileBeanList.isEmpty()) {
            this.profileDAO = ProfileDAO.newInstance(getContext());
            this.profileDAO.deleteProfile(profileBeanList.get(position).getProfileId());
            this.profileBeanList.remove(position);
            this.profileAdapter.notifyDataSetChanged();
            if (profileBeanList.isEmpty()) {
                noData.setVisibility(View.VISIBLE);
            }
        } else {

            noData.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(int position) {
        /*LocationSMSFragment locationSMSFragment = LocationMainActivity.getSMSFragInstance();
        locationSMSFragment.setProfileData("profile", profileBeanList.get(position));*/
        replaceFragment(R.id.landing_content_frame, LocationSMSFragment.newInstance(profileBeanList.get(position),"profile"));

    }
}


