package com.android.yardsale.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.AddYardSaleActivity;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.helpers.CircularReveal;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ListingsFragment extends Fragment {

    private static final String TAG = "ListingsFragment";

    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    private ThingsAdapter adapter;
    private FloatingActionButton btCreateSale;

    public ListingsFragment() {
        super();
    }

    public static ListingsFragment newInstance() {
        return new ListingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();
        adapter = new ThingsAdapter(getActivity(), factory, container);
        // Set CustomAdapter as the adapter for RecyclerView.
        rvSales.setAdapter(adapter);

        btCreateSale = (FloatingActionButton) view.findViewById(R.id.fab);
        btCreateSale.attachToRecyclerView(rvSales);
        btCreateSale.setColorNormal(getResources().getColor(R.color.amber));
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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

        return view;
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


    public void editYardSale(YardSale sale) {
        adapter.editYardSale(sale);
    }

    public void addYardSale(YardSale yardSale) {
        adapter.addYardSale(yardSale);
    }
}
