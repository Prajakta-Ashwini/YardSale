package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

public class MyFavoritesFragment extends Fragment {

    private static final String TAG = "MyFavoritesFragment";
    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    private ThingsAdapter adapter;

    public MyFavoritesFragment() {

    }

    public static MyFavoritesFragment newInstance()
    {
        return new MyFavoritesFragment();
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();
        adapter = new ThingsAdapter(getActivity(), factory, container);
        // Set CustomAdapter as the adapter for RecyclerView.
        rvSales.setAdapter(adapter);
        registerForContextMenu(rvSales);
        return view;
    }

    private ParseQueryAdapter.QueryFactory<YardSale> getYardSaleQueryFactory() {
        return new ParseQueryAdapter.QueryFactory<YardSale>() {
            public ParseQuery<YardSale> create() {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereEqualTo("user_likes", ParseUser.getCurrentUser());

                try {
                    query.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return query;
            }
        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //MyView.RecyclerContextMenuInfo info = (MyView.RecyclerContextMenuInfo) item.getMenuInfo();
        int position = -1;
        try {
            position = adapter.getPosition() ;

        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.delete_sale:
                Toast.makeText(getActivity(), "delete sale!", Toast.LENGTH_SHORT).show();
                adapter.fireDelete(getActivity(), position);
                break;
            case R.id.edit_sale:
                Toast.makeText(getActivity(), "edit sale!", Toast.LENGTH_SHORT).show();
                adapter.fireEdit(getActivity(), position);
                break;

            case R.id.share_sale:
                Toast.makeText(getActivity(), "share sale!", Toast.LENGTH_SHORT).show();
                adapter.fireShare(getActivity(), position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int position = -1;
        try {
            position = adapter.getPosition() ;

        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage(), e);
            super.onCreateContextMenu(menu, v, menuInfo);
            return;
        }
        // inflate menu here
        menu.setHeaderTitle("Select action:");
        List<YardSale> list = adapter.getListSales();
        if(list!=null) {
            YardSale s = list.get(position);
            if (s.getSeller() == ParseUser.getCurrentUser()) {
                menu.add(Menu.NONE, R.id.edit_sale, Menu.NONE, "Edit Sale");
                menu.add(Menu.NONE, R.id.delete_sale, Menu.NONE, "Delete Sale");
            }
        }
        menu.add(Menu.NONE, R.id.share_sale, Menu.NONE, "Share Sale");
    }
}
