package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.SalesArrayAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindStuffFragment extends Fragment {
    public static List<YardSale> yardSalesList ;
    public ListView lvYardSales;
    //private ProgressBar progressBarFooter;
    public SalesArrayAdapter aSales;
    private Button btCreateSale;

    public FindStuffFragment(){
        super();
    }
    public static FindStuffFragment newInstance(){
        FindStuffFragment fragmentDemo = new FindStuffFragment();
        yardSalesList = new ArrayList<>();
        //Bundle args = new Bundle();
        //args.putInt("sale_list", list);
        //fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public void addYardSale(YardSale row){
        aSales.add(row);
        aSales.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sale_list, parent, false);
        lvYardSales = (ListView) v.findViewById(R.id.lvYardSales);

        aSales = new SalesArrayAdapter(getActivity(),yardSalesList);
        lvYardSales.setAdapter(aSales);

        final YardSaleApplication client = new YardSaleApplication(getActivity());
        btCreateSale = (Button)v.findViewById(R.id.btCreateSale);
        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"creating!!!", Toast.LENGTH_SHORT).show();
                client.createYardSale("my yardsale","all items must go by fri",new Date(),new Date(),new ParseGeoPoint(37.42,-121.94));
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
