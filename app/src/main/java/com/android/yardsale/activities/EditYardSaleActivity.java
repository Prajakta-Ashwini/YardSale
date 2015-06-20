package com.android.yardsale.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditYardSaleActivity extends ActionBarActivity {

    private EditText etEditYSTitle;
    private EditText etEditYSDescription;
    private EditText etEditYSAddress;
    private static TextView tvEditYSStart;
    private static TextView tvEditYSEnd;
    private YardSale yardSale;
    private static DateFormat format = DateFormat.getDateTimeInstance();
    YardSaleApplication client;
    static final int DATE_DIALOG_ID_START = 1;
    static final int DATE_DIALOG_ID_END = 2;
    static final int TIME_DIALOG_ID_START = 3;
    static final int TIME_DIALOG_ID_END = 4;
    private static int current_date = 0;
    private static int current_time = 0;

    private static Date start;
    private static Date end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_yard_sale);

        client = new YardSaleApplication(this);
        String yardSaleId = getIntent().getStringExtra("edit_yard_sale_id");

        //TODO allow edit only If its my yard sale - ACL in parse
        etEditYSTitle = (EditText) findViewById(R.id.etYSTitle);
        etEditYSDescription = (EditText) findViewById(R.id.etYSDescription);
        etEditYSAddress = (EditText) findViewById(R.id.etYSAddress);
        tvEditYSStart = (TextView) findViewById(R.id.tvYSStart);
        tvEditYSEnd = (TextView) findViewById(R.id.tvYSEnd);

        ParseQuery getQuery = YardSale.getQuery();
        try {
            yardSale = (YardSale) getQuery.get(yardSaleId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        etEditYSTitle.setText(yardSale.getTitle());
        etEditYSDescription.setText(yardSale.getDescription());
        etEditYSAddress.setText(yardSale.getAddress());
        tvEditYSStart.setText(format.format(yardSale.getStartTime()));
        tvEditYSEnd.setText(format.format(yardSale.getEndTime()));

        try {
            start = format.parse(tvEditYSStart.getText().toString());
            end = format.parse(tvEditYSEnd.getText().toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        final Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        tvEditYSStart.setClickable(true);
        tvEditYSStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("start_time", startCal.get(Calendar.HOUR), startCal.get(Calendar.MINUTE));
                datePicker("start_date", startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DATE));
            }
        });


        final Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        tvEditYSEnd.setClickable(true);
        tvEditYSEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("end_time", endCal.get(Calendar.HOUR), endCal.get(Calendar.MINUTE));
                datePicker("end_date", endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DATE));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_yard_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSave(View view) {
        client.updateYardSale(yardSale.getObjectId(), String.valueOf(etEditYSTitle.getText()),
                String.valueOf(etEditYSDescription.getText()),
                start,
                end,
                String.valueOf(etEditYSAddress.getText()));
        Toast.makeText(this, "Updated Yard Sale", Toast.LENGTH_LONG).show();
        Intent data = new Intent();
        data.putExtra("title", String.valueOf(etEditYSTitle.getText()));
        data.putExtra("desc", String.valueOf(etEditYSDescription.getText()));
        data.putExtra("obj_id",yardSale.getObjectId());
        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancel(View view) {
        finish();
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        private int hour;
        private int minute;

        public TimePickerFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle data = getArguments();
            hour = data.getInt("hour");
            minute = data.getInt("minute");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (current_time == TIME_DIALOG_ID_START) {
                start.setHours(view.getCurrentHour());
                start.setMinutes(view.getCurrentMinute());
                tvEditYSStart.setText(format.format(start));
            } else if (current_time == TIME_DIALOG_ID_END) {
                end.setHours(view.getCurrentHour());
                end.setMinutes(view.getCurrentMinute());
                tvEditYSEnd.setText(format.format(end));
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        private int year, month, day;

        public DatePickerFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle data = getArguments();
            year = data.getInt("year");
            month = data.getInt("month");
            day = data.getInt("day");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            if (current_date == DATE_DIALOG_ID_START) {
                start = getDate(datePicker);
            } else if (current_date == DATE_DIALOG_ID_END) {
                end = getDate(datePicker);
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

    private void datePicker(String date, int year, int month, int day) {
        if (date.equals("start_date")) {
            current_date = DATE_DIALOG_ID_START;
        } else if (date.equals("end_date")) {
            current_date = DATE_DIALOG_ID_END;
        }

        Bundle data = new Bundle();
        data.putInt("year", year);
        data.putInt("month", month);
        data.putInt("day", day);

        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.setArguments(data);
        dateFragment.show(getSupportFragmentManager(), date);
    }

    private void timePicker(String time, int hour, int minute) {
        if (time.equals("start_time")) {
            current_time = TIME_DIALOG_ID_START;
        } else if (time.equals("end_time")) {
            current_time = TIME_DIALOG_ID_END;
        }

        Bundle data = new Bundle();
        data.putInt("hour", hour);
        data.putInt("minute", minute);

        DialogFragment timeFragment = new TimePickerFragment();
        timeFragment.setArguments(data);
        timeFragment.show(getSupportFragmentManager(), time);
    }
    //TODO code cleanup lot of redundant code extract this and clean it up
}
