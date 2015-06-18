package com.android.yardsale.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SalesFragment extends Fragment {

    private static final String TAG = "SalesFragment";

    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    private ThingsAdapter adapter;
    public  List<YardSale> yardSales = new ArrayList<>();
    private YardSaleApplication client;

    public SalesFragment() {
        super();
    }

    public static SalesFragment newInstance() {
        return new SalesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yardSales = new ArrayList<>();

        ParseQuery<YardSale> get = YardSale.getQuery();
       // get.whereNotEqualTo("seller", ParseUser.getCurrentUser());
        get.findInBackground(new FindCallback<YardSale>() {
            @Override
            public void done(List<YardSale> list, ParseException e) {
                yardSales = list;
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_fragment, container, false);
        view.setTag(TAG);

        rvSales = (RecyclerView) view.findViewById(R.id.rvSales);
        // Setup layout manager for items
        mLayoutManager = new LinearLayoutManager(getActivity());
        // Control orientation of the items
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        // Attach layout manager
        rvSales.setLayoutManager(mLayoutManager);
        rvSales.setHasFixedSize(true);
        rvSales.setLayoutManager(mLayoutManager);
        rvSales.setItemAnimator(new DefaultItemAnimator());

        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();
        adapter = new ThingsAdapter(getActivity(), factory, container);
        // Set CustomAdapter as the adapter for RecyclerView.
        rvSales.setAdapter(adapter);

        return view;
    }

    private ParseQueryAdapter.QueryFactory<YardSale> getYardSaleQueryFactory() {
        return new ParseQueryAdapter.QueryFactory<YardSale>() {
            public ParseQuery<YardSale> create() {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereNotEqualTo("seller", ParseUser.getCurrentUser());
                try {
                    query.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return query;
            }
        };
    }

}
