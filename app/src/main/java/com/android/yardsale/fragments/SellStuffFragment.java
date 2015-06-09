package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.yardsale.R;
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

        FrameLayout flSellStuff = (FrameLayout) view.findViewById(R.id.flSellStuff);

        if (getChildFragmentManager().findFragmentByTag("sellTag") == null) {
            ListingsFragment fragment = ListingsFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.flSellStuff, fragment, "sellTag").commit();
        }
        return flSellStuff;
    }

    public void addYardSale(YardSale row) {
        ListingsFragment fragment;
        if (getChildFragmentManager().findFragmentByTag("sellTag") != null) {
            fragment = (ListingsFragment) getChildFragmentManager().findFragmentByTag("sellTag");
            fragment.addYardSale(row);
        }
    }

}
