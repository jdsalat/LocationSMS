package com.locationsms.sms.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.locationsms.sms.R;

/**
 * Created by Javed.Salat on 5/29/2016.
 */
public class AlertProfileDialogFragment extends DialogFragment {
    public AlertProfileDialogFragment() {
    }

    public static AlertProfileDialogFragment newInstance() {
        return new AlertProfileDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
         View view = inflater.inflate(R.layout.profile_alert_layout, new LinearLayout(getActivity()), false);

        final EditText editText = (EditText) view.findViewById(R.id.username);
        builder.setView(view)
                .setTitle(R.string.save_title).setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                String msg = editText.getText().toString();
                dismiss();
                mListener.onDialogPositiveClick(msg);

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();

                mListener.onDialogNegativeClick();

            }
        }).setCancelable(false);
        return builder.create();
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String userName);

        public void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}