package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.yardsale.R;
import com.android.yardsale.adapters.MyYardSaleAdapter;
import com.android.yardsale.models.YardSale;

import java.util.ArrayList;
import java.util.List;

public class SellStuffFragment extends Fragment {
    private List<YardSale> sales;
    private MyYardSaleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sales = new ArrayList<>();
        adapter = new MyYardSaleAdapter(getActivity(), sales);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_list, container, false);

        ListView lvMyYardSales = (ListView) view.findViewById(R.id.lvMyYardSales);
        lvMyYardSales.setAdapter(adapter);

        return view;
    }
}
