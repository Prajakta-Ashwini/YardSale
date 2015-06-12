package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.SalesAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;

import java.util.ArrayList;
import java.util.List;

//import com.android.yardsale.helpers.GeopointUtils;
//import com.google.android.gms.maps.model.LatLng;
//import com.parse.ParseGeoPoint;

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

//        aSales = new SalesArrayAdapter(getActivity(), yardSalesList);
//        gvYardSales.setAdapter(aSales);
        final YardSaleApplication client = new YardSaleApplication(getActivity());
        btCreateSale = (Button) v.findViewById(R.id.btCreateSale);
        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                showAddYSDialog();
                //client.createYardSale("my yardsale", "all items must go by fri", new Date(), new Date(), new ParseGeoPoint(37.42, -121.94));
//                String address = "447 Great Mall Dr " +
//                        "Milpitas, CA 95035";
//                LatLng loc = GeopointUtils.getLocationFromAddress(getActivity(),address);
//                client.createYardSale("my yardsale", "all items must go by fri", new Date(), new Date(), address, new ParseGeoPoint(loc.latitude, loc.longitude));
            }
        });

//        rvSales.setOnItemClickListener(new AdapterViewCompat.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterViewCompat<?> parent, View view, int position, long id) {
//                YardSale s = yardSalesList.get(position);
//                client.getItemsForYardSale(s);
//           }
//        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void showAddYSDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddFragment add = AddFragment.newInstance();
        add.show(fm, "fragment_add");
    }

}
