package com.android.yardsale.fragments.add.steps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.yardsale.R;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

public class Step1 extends WizardStep{

    @ContextVariable
    private String title;
    @ContextVariable
    private String description;

    EditText etAddTitle;
    EditText etAddDescription;

    public Step1() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_step_1, container, false);
        //Get reference to the textboxes
        etAddTitle = (EditText) v.findViewById(R.id.etAddTitle);
        etAddDescription = (EditText) v.findViewById(R.id.etAddDescription);

        //and set default values by using Context Variables
       // etAddTitle.setText(etAddTitle);
        //etAddDescription.setText(etAddDescription);

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
        title = etAddTitle.getText().toString();
        description = etAddDescription.getText().toString();
    }
}
