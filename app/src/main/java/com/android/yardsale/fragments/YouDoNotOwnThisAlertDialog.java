package com.android.yardsale.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;

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

        String tag = getArguments().getString("tag");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Sorry!!");
        alertDialogBuilder.setIcon(R.drawable.ic_warning);
        alertDialogBuilder.setMessage("The item you are trying to " + Html.fromHtml("<b>" + tag + "</b>") + " is not owned by you");
        return alertDialogBuilder.create();
    }

}
