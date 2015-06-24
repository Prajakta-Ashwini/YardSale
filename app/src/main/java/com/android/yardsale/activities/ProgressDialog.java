package com.android.yardsale.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.android.yardsale.R;

public class ProgressDialog extends DialogFragment {
    int screenWidth;
    View waitDialogLayout;

    public ProgressDialog() {
        super();
    }

    public static ProgressDialog newInstance() {
        ProgressDialog frag = new ProgressDialog();
//        Bundle args = new Bundle();
//        args.putString("tag", tag);
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        waitDialogLayout = inflater.inflate(R.layout.dialog_progress, null);
        Point pointSize = new Point();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(pointSize);
        screenWidth = pointSize.x;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(waitDialogLayout);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        doAnimation();
        return dialog;
    }

    void doAnimation() {

        ImageView post = (ImageView) waitDialogLayout.findViewById(R.id.ivPost);

        ObjectAnimator movePost = ObjectAnimator.ofFloat(post, "translationX", -screenWidth/2, 0);
        movePost.setInterpolator(new AccelerateInterpolator());
        movePost.setDuration(1000);

        //bounce
        ObjectAnimator rotatePost = ObjectAnimator.ofFloat(post ,
                "rotation", 0f, 360f);//ObjectAnimator.ofFloat(post, "translationX", 0, screenWidth/2);
        movePost.setInterpolator(new AccelerateInterpolator());
        movePost.setDuration(2000);

        ObjectAnimator movePostBack = ObjectAnimator.ofFloat(post, "translationX", 0, screenWidth);
        movePostBack.setInterpolator(new AccelerateInterpolator());
        movePostBack.setDuration(1000);

        final AnimatorSet moveSet = new AnimatorSet();
        moveSet.playSequentially(movePost, rotatePost, movePostBack);
        moveSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                moveSet.start();
            }
        });

        moveSet.start();
    }
}