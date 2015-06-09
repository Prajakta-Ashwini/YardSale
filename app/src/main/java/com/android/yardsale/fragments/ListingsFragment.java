package com.android.yardsale.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.yardsale.R;
import com.android.yardsale.adapters.MyYardSaleAdapter;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ListingsFragment extends SellStuffFragment {

    private ArrayList<YardSale> myYardSales;
    private MyYardSaleAdapter adapter;

    public ListingsFragment() {
        super();
    }

    public static ListingsFragment newInstance() {
        return new ListingsFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myYardSales = new ArrayList<>();


        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        adapter = new MyYardSaleAdapter(getActivity(), factory, inflater);
    }

    private ParseQueryAdapter.QueryFactory<YardSale> getYardSaleQueryFactory() {
        return new ParseQueryAdapter.QueryFactory<YardSale>() {
                public ParseQuery<YardSale> create() {
                    ParseQuery<YardSale> query = YardSale.getQuery();
                    query.whereEqualTo("seller", ParseUser.getCurrentUser());
                    try {
                        query.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return query;
                }
            };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_fragment_list, container, false);
        ListView lvMyYardSales = (ListView) view.findViewById(R.id.lvMyYardSales);
        lvMyYardSales.setAdapter(adapter);
        return view;
    }
}
