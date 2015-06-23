package com.android.yardsale.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.yardsale.R;

public class YouDoNotOwnThisAlertDialog extends DialogFragment {
    public YouDoNotOwnThisAlertDialog() {
    }

    public static YouDoNotOwnThisAlertDialog newInstance(String tag) {
        YouDoNotOwnThisAlertDialog frag = new YouDoNotOwnThisAlertDialog();
        Bundle args = new Bundle();
        args.putString("tag", tag);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertDialogLayout = inflater.inflate(R.layout.alert_dialog_layout, null);
        String tag = getArguments().getString("tag");
        Log.e("ON_ALERT", tag + alertDialogLayout.getTag());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(alertDialogLayout);
        TextView tvMessage = (TextView) alertDialogLayout.findViewById(R.id.tvMessage);
        tvMessage.setText(tag);
        return alertDialogBuilder.create();
    }

}
