package com.android.yardsale.fragments.add.wizard;


import com.android.yardsale.fragments.add.steps.Step1;
import com.android.yardsale.fragments.add.steps.Step2;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

public class AddWizard extends BasicWizardLayout{
    @Override
    public WizardFlow onSetup() {

        return new WizardFlow.Builder()
                .addStep(Step1.class)           //Add your steps in the order you want them
                .addStep(Step2.class)           //to appear and eventually call create()
                .create();
    }

    @Override
    public void onWizardComplete() {
        super.onWizardComplete();
        getActivity().finish();
    }
}
