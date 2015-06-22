package com.android.yardsale.adapters;


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
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Calendar;

public class PastHistoryFragment extends Fragment {
    private static final String TAG = "ListingsFragment";

    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    private ThingsAdapter adapter;

    public PastHistoryFragment() {
        super();
    }

    public static PastHistoryFragment newInstance() {
        return new PastHistoryFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.parseAdapter.loadObjects();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.parseAdapter.loadObjects();
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

        FloatingActionButton notRequiredFAB = (FloatingActionButton) view.findViewById(R.id.fab);
        notRequiredFAB.setVisibility(View.INVISIBLE);

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
                query.whereEqualTo("seller", ParseUser.getCurrentUser());
                Calendar c = Calendar.getInstance();
                query.whereLessThanOrEqualTo("end_time", c.getTime());
                query.orderByAscending("start_time");

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
