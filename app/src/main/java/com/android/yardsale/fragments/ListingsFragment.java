package com.android.yardsale.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.YardSaleDetailActivity;
import com.android.yardsale.adapters.MyYardSaleAdapter;
import com.android.yardsale.adapters.SalesAdapter;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    private MyYardSaleAdapter adapter;
    private YardSaleApplication client;
    private ParseUser currentUser;

    public ListingsFragment() {
        super();
    }

    public static ListingsFragment newInstance() {
        return new ListingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        adapter = new ThingsAdapter(getActivity(), factory, inflater);
        client = new YardSaleApplication(getActivity());
        currentUser = ParseUser.getCurrentUser();
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

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadObjects();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.loadObjects();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_fragment_list, container, false);

        View header = inflater.inflate(R.layout.header_profile, null);
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            client.makeMeRequest();
        }

        //TODO update the views now by getting the parse user object

        ListView lvMyYardSales = (ListView) view.findViewById(R.id.lvMyYardSales);
        lvMyYardSales.addHeaderView(header);
        lvMyYardSales.setAdapter(adapter);

        lvMyYardSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO launch detailed activity page
                Toast.makeText(getActivity(), "onItemClick", Toast.LENGTH_SHORT).show();
                getItemsInThisYS(adapter.getItem(position));
            }
        });
        return view;
    }

    public void addYardSale(YardSale row) {
        //TODO figure out how to add this to the listview
    }

    private ArrayList<CharSequence> getItemsInThisYS(final YardSale yardSale) {
        final ArrayList<CharSequence> itemIds = new ArrayList<>();
        ParseQuery<Item> searchQuery = ParseQuery.getQuery(Item.class);
        searchQuery.whereEqualTo("yardsale_id", yardSale);
        searchQuery.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> results, ParseException e) {
                if (e == null) {
                    for (Item item : results) {
                        itemIds.add(item.getObjectId());
                    }
                    Intent intent = new Intent(getActivity(), YardSaleDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putCharSequenceArrayListExtra("item_list", itemIds);
                    intent.putExtra("yardsale", yardSale.getObjectId());
                    getActivity().startActivity(intent);
                } else {
                }
            }
        });
        return itemIds;
    }
}
