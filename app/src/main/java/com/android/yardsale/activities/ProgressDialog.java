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
        screenWidth = pointSize.y;
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

//        ObjectAnimator movePost = ObjectAnimator.ofFloat(post, "translationY", -screenWidth/2, 0);
//        movePost.setInterpolator(new BounceInterpolator());
//        movePost.setDuration(500);

        //bounce
        ObjectAnimator rotatePost = ObjectAnimator.ofFloat(post ,
                "rotation", 0f, 360f);//ObjectAnimator.ofFloat(post, "translationX", 0, screenWidth/2);
        rotatePost.setInterpolator(new AccelerateInterpolator());
        rotatePost.setDuration(2000);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(post, "scaleX", 0.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(post, "scaleY", 0.5f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);
        scaleDownX.setInterpolator(new AccelerateInterpolator());
        scaleDownY.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(post, "scaleX", 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(post, "scaleY", 1f);
        scaleUpX.setDuration(1000);
        scaleUpY.setDuration(1000);
        scaleUpX.setInterpolator(new AccelerateInterpolator());
        scaleUpY.setInterpolator(new AccelerateInterpolator());


//        ObjectAnimator movePostBack = ObjectAnimator.ofFloat(post, "translationX", 0, screenWidth);
//        movePostBack.setInterpolator(new AccelerateInterpolator());
//        movePostBack.setDuration(1000);
        final AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.play(scaleDownX).with(scaleDownY);

        final AnimatorSet scaleSetUp = new AnimatorSet();
        scaleSetUp.play(scaleUpX).with(scaleUpY);

        final AnimatorSet moveSet = new AnimatorSet();
        moveSet.playSequentially( rotatePost, scaleSet, scaleSetUp);
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