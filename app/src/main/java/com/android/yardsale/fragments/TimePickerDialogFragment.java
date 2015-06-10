package com.android.yardsale.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {

    private Activity myActivity;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = activity;
        try {
            onTimeSetListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(myActivity, onTimeSetListener, hour, minute,
                DateFormat.is24HourFormat(myActivity));
    }
}