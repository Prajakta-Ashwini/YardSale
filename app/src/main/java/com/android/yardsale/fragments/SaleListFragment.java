package com.android.yardsale.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.SalesArrayAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.etsy.android.grid.StaggeredGridView;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleListFragment extends FindStuffFragment {
    public static List<YardSale> yardSalesList= new ArrayList<>() ;
    //public ListView lvYardSales;
    //private ProgressBar progressBarFooter;
    public SalesArrayAdapter aSales;
    private Button btCreateSale;
    private StaggeredGridView gvYardSales;

    public SaleListFragment(){
        super();
    }

    public static SaleListFragment newInstance(){
        SaleListFragment fragmentDemo = new SaleListFragment();
        yardSalesList = new ArrayList<>();
        return fragmentDemo;
    }

    public void addYardSale(YardSale row){
        aSales.add(row);
        aSales.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sale_list, parent, false);
       // lvYardSales = (ListView) v.findViewById(R.id.lvYardSales);
        gvYardSales = (StaggeredGridView) v.findViewById(R.id.gvYardSales);

        aSales = new SalesArrayAdapter(getActivity(),yardSalesList);
        gvYardSales.setAdapter(aSales);
        final YardSaleApplication client = new YardSaleApplication(getActivity());
        btCreateSale = (Button)v.findViewById(R.id.btCreateSale);
        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                client.createYardSale("my yardsale", "all items must go by fri", new Date(), new Date(), new ParseGeoPoint(37.42, -121.94));
            }
        });

        gvYardSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YardSale s = yardSalesList.get(position);
                client.getItemsForYardSale(s);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
