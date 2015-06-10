package com.android.yardsale.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFragment extends DialogFragment {

    private EditText etAddYSTitle;
    private EditText etAddYSDescription;
    private EditText etAddYSAddress;
    private static TextView tvAddYSStart;
    private static TextView tvAddYSEnd;
    private Button btnSave;
    private Button btnCancel;
    private static String start;
    private static String end;
    private YardSaleApplication client;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");

    public AddFragment() {
    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_activity, container);

        client = new YardSaleApplication(getActivity());
        etAddYSTitle = (EditText) view.findViewById(R.id.etAddYSTitle);
        etAddYSDescription = (EditText) view.findViewById(R.id.etAddYSDescription);
        etAddYSAddress = (EditText) view.findViewById(R.id.etAddYSAddress);
        tvAddYSStart = (TextView) view.findViewById(R.id.tvAddYSStart);
        tvAddYSEnd = (TextView) view.findViewById(R.id.tvAddYSEnd);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        tvAddYSStart.setClickable(true);
        tvAddYSStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("start_time");
                datePicker("start_date");
                //  tvAddYSStart.setText(start.toString());
            }
        });

        tvAddYSEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("end_time");
                datePicker("end_date");
                // tvAddYSEnd.setText(end.toString());
            }
        });

        final String title = String.valueOf(etAddYSTitle.getText());
        final String description = String.valueOf(etAddYSDescription.getText());
        final String address = String.valueOf(etAddYSAddress.getText());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("before save", start + "\t" + end);
                    client.createYardSale(title, description, dateFormat.parse(start), dateFormat.parse(end), address);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        getDialog().setTitle("Create Yard Sale");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    private void datePicker(String date) {
        DialogFragment dateFragment = new DatePickerDialogFragment();
        dateFragment.show(getActivity().getSupportFragmentManager(), date);
    }

    private void timePicker(String time) {
        DialogFragment timeFragment = new DatePickerDialogFragment();
        timeFragment.show(getActivity().getSupportFragmentManager(), time);
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //DateEdit.setText(day + "/" + (month + 1) + "/" + year);
            Log.d("Tags", view.getTag().toString());
            if (view.getTag().equals("start_date")) {
                start = year + "-" + (month + 1) + "-" + day;
                Log.d("Debug: on dateset", start);
            } else if (view.getTag().equals("end_date")) {
                end = year + "-" + (month + 1) + "-" + day;
                Log.d("Debug: on dateset", end);
            }
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            // DateEdit.setText(DateEdit.getText() + " -" + hourOfDay + ":" + minute);

            Log.d("Tags", view.getTag().toString());
            if (view.getTag().equals("start_time")) {
                start = start + " " + view.getCurrentHour() + ":" + view.getCurrentMinute();
                Log.d("Debug: on timeset", start);
                tvAddYSStart.setText(dateFormat.format(start));
            } else if (view.getTag().equals("end_time")) {
                end = end + " " + view.getCurrentHour() + ":" + view.getCurrentMinute();
                tvAddYSEnd.setText(dateFormat.format(end));
                Log.d("Debug: on timeset", end);
            }
        }
    }
}


