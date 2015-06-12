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

import java.util.Calendar;
import java.util.Date;

public class AddFragment extends DialogFragment {

    private EditText etAddYSTitle;
    private EditText etAddYSDescription;
    private EditText etAddYSAddress;
    private static TextView tvAddYSStart;
    private static TextView tvAddYSEnd;
    private Button btnSave;
    private Button btnCancel;
    private static Date start;
    private static Date end;
    private YardSaleApplication client;
    static final int DATE_DIALOG_ID_START = 1;
    static final int DATE_DIALOG_ID_END = 2;
    static final int TIME_DIALOG_ID_START = 3;
    static final int TIME_DIALOG_ID_END = 4;
    private static int current_date = 0;
    private static int current_time = 0;

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
            }
        });

        tvAddYSEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("end_time");
                datePicker("end_date");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("before save", start + "\t" + end);
                client.createYardSale(String.valueOf(etAddYSTitle.getText()),
                        String.valueOf(etAddYSDescription.getText()),
                        start,
                        end,
                        String.valueOf(etAddYSAddress.getText()));
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("Create Yard Sale");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    private void datePicker(String date) {
        if (date.equals("start_date")) {
            current_date = DATE_DIALOG_ID_START;
        } else if (date.equals("end_date")) {
            current_date = DATE_DIALOG_ID_END;
        }
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getActivity().getSupportFragmentManager(), date);
    }

    private void timePicker(String time) {
        if (time.equals("start_time")) {
            current_time = TIME_DIALOG_ID_START;
        } else if (time.equals("end_time")) {
            current_time = TIME_DIALOG_ID_END;
        }
        DialogFragment timeFragment = new TimePickerFragment();
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

        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            if (current_date == DATE_DIALOG_ID_START) {
                start = getDate(datePicker);
                Log.d("Debug: on dateset", start.toString());
            } else if (current_date == DATE_DIALOG_ID_END) {
                end = getDate(datePicker);
                Log.d("Debug: on dateset", end.toString());
            }
        }
    }

    private static Date getDate(DatePicker datePicker) {
        int selectedDay = datePicker.getDayOfMonth();
        int selectedMonth = datePicker.getMonth();
        int selectedYear = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay);

        return calendar.getTime();
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
            Log.d("Current time tag", current_time + "");
            if (current_time == TIME_DIALOG_ID_START) {
                start.setHours(view.getCurrentHour());
                start.setMinutes(view.getCurrentMinute());
                Log.d("Debug: on timeset", start.toString());
                tvAddYSStart.setText(start.toString());
            } else if (current_time == TIME_DIALOG_ID_END) {
                end.setHours(view.getCurrentHour());
                end.setMinutes(view.getCurrentMinute());
                tvAddYSEnd.setText(end.toString());
                Log.d("Debug: on timeset", end.toString());
            }
        }
    }
}


