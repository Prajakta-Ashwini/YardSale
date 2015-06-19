package com.android.yardsale.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.AddYardSaleActivity;
import com.android.yardsale.helpers.CircularReveal;
import com.android.yardsale.models.YardSale;

public class SellStuffFragment extends Fragment {

    public static SellStuffFragment newInstance() {
        SellStuffFragment fragment = new SellStuffFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_list, container, false);

        //FrameLayout flSellStuff = (FrameLayout) view.findViewById(R.id.flSellStuff);

        final FloatingActionButton btnCreateYS = (FloatingActionButton) view.findViewById(R.id.btnCreateYS);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CircularReveal.enterReveal(btnCreateYS);
            }
        });

        btnCreateYS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddYardSaleActivity.class);
                startActivity(intent);
            }
        });

        if (getChildFragmentManager().findFragmentByTag("sellTag") == null) {
            ListingsFragment fragment = ListingsFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.flSellStuff, fragment, "sellTag").commit();
        }

        return view;
    }

    public void addYardSale(YardSale row) {
//        ListingsFragment fragment;
//        if (getChildFragmentManager().findFragmentByTag("sellTag") != null) {
//            fragment = (ListingsFragment) getChildFragmentManager().findFragmentByTag("sellTag");
//            fragment.addYardSale(row);
//        }
    }

}
