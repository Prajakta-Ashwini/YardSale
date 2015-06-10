package com.android.yardsale.fragments.add.steps;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.Calendar;
import java.util.Date;

public class Step2 extends WizardStep implements DatePickerDialog.OnDateSetListener {

    @ContextVariable
    private Date startTime;
    @ContextVariable
    private Date endTime;
    @ContextVariable
    private String address;
    @ContextVariable
    private String title;
    @ContextVariable
    private String description;

    TextView tvStartDate;

    public Step2() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_step_2, container, false);
        tvStartDate = (TextView) v.findViewById(R.id.tvStartDateTitle);
        return v;
    }

    @Override
    public void onExit(int exitCode) {
        switch (exitCode) {
            case WizardStep.EXIT_NEXT:
                bindDataFields();
                break;
            case WizardStep.EXIT_PREVIOUS:
                //Do nothing...
                break;
        }
    }

    private void bindDataFields() {
        //Do some work
        //...
        //The values of these fields will be automatically stored in the wizard context
        //and will be populated in the next steps only if the same field names are used.
        YardSaleApplication client = new YardSaleApplication(getActivity());
        client.createYardSale(title, description, startTime, endTime, address);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        startTime = cal.getTime();
        tvStartDate.setText(startTime.toString());
    }
}
