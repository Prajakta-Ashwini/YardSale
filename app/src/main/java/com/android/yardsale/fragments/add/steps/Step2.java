package com.android.yardsale.fragments.add.steps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.yardsale.R;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.Date;

public class Step2 extends WizardStep {

    @ContextVariable
    private Date startTime;
    @ContextVariable
    private Date endTime;
    @ContextVariable
    private String location;

    DatePicker dpAddStartDate;
    EditText etAddEndDate;

    public Step2() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_step_2, container, false);
        dpAddStartDate = (DatePicker) v.findViewById(R.id.dpAddStartDate);
        etAddEndDate = (EditText) v.findViewById(R.id.etAddEndDate);

        //WizarDroid will automatically inject the values for these fields
        //so we can simply set the text views
//        firstnameTv.setText(firstname);
//        lastnameTv.setText(lastname);

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
        //startTime = new Date(String.valueOf(dpAddStartDate.()));
        endTime  = new Date(String.valueOf(etAddEndDate.getText()));
    }
}
