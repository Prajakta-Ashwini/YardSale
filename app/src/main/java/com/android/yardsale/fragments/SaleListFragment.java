package com.android.yardsale.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.AddYardSaleActivity;
import com.android.yardsale.adapters.SalesAdapter;
import com.android.yardsale.helpers.CircularReveal;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SaleListFragment extends FindStuffFragment{
    public static List<YardSale> yardSalesList = new ArrayList<>();
    //private ProgressBar progressBarFooter;
    private FloatingActionButton btCreateSale;
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
        boolean added =false;
        if(yardSalesList.size() > 0) {
            for(int i=0; i< yardSalesList.size(); i++) {
                if (yardSalesList.get(i).getCreatedAt().before(row.getCreatedAt())) {
                    yardSalesList.add(i, row);
                    added= true;
                    break;
                }
            }
        }
        if(!added)
            yardSalesList.add(row);

        rAdapter.notifyDataSetChanged();
    }

    public void removeYardSale(YardSale row) {
        yardSalesList.remove(row);
        rAdapter.notifyDataSetChanged();
    }

    public void editYardSale(YardSale row) {
        for(YardSale sale:yardSalesList){
            if(sale.getObjectId() == row.getObjectId()){
                sale.setTitle(row.getTitle());
                sale.setDescription(row.getDescription());
                sale.setAddress(row.getAddress());
                if(row.getCoverPic()!=null)
                    sale.setCoverPic(row.getCoverPic());
                //sale.setLocation(row.getLocation());
                sale.setStartTime(row.getStartTime());
                sale.setEndTime(row.getEndTime());
                rAdapter.notifyDataSetChanged();
                break;
            }
        }

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

        btCreateSale = (FloatingActionButton) v.findViewById(R.id.btCreateSale);
        btCreateSale.attachToRecyclerView(rvSales);
        btCreateSale.setColorNormal(getResources().getColor(R.color.ruby));
        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CircularReveal.enterReveal(btCreateSale);
            }
        });

        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddYardSaleActivity.class);
                startActivityForResult(intent, 20);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
