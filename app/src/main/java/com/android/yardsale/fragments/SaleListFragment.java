package com.android.yardsale.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.AddYardSaleActivity;
import com.android.yardsale.adapters.SalesAdapter;
import com.android.yardsale.models.YardSale;

import java.util.ArrayList;
import java.util.List;

public class SaleListFragment extends FindStuffFragment{
    public static List<YardSale> yardSalesList = new ArrayList<>();
    //private ProgressBar progressBarFooter;
    private Button btCreateSale;
    private RecyclerView rvSales;
    private RecyclerView.Adapter rAdapter;

    public SaleListFragment() {
        super();
    }

    public static SaleListFragment newInstance() {
        SaleListFragment fragmentDemo = new SaleListFragment();
        yardSalesList = new ArrayList<>();
        return fragmentDemo;
    }

    public void addYardSale(YardSale row) {
        yardSalesList.add(row);
        rAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        View v = inflater.inflate(R.layout.fragment_sale_list, parent, false);
        rvSales = (RecyclerView) v.findViewById(R.id.rvSales);
        rvSales.setHasFixedSize(true);
        rvSales.setLayoutManager(llm);
        rvSales.setItemAnimator(new DefaultItemAnimator());

        rAdapter = new SalesAdapter(getActivity(),yardSalesList);
        rvSales.setAdapter(rAdapter);

        btCreateSale = (Button) v.findViewById(R.id.btCreateSale);
        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddYardSaleActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
