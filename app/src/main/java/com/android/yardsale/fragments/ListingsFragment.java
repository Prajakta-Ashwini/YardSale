package com.android.yardsale.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.yardsale.R;
import com.android.yardsale.activities.YardSaleDetailActivity;
import com.android.yardsale.adapters.MyYardSaleAdapter;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ListingsFragment extends SellStuffFragment {

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
        ListView lvMyYardSales = (ListView) view.findViewById(R.id.lvMyYardSales);
        lvMyYardSales.setAdapter(adapter);

        lvMyYardSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO launch detailed activity page
                Intent intent = new Intent(getActivity(), YardSaleDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //TODO figure out how to send this info
//                ArrayList<CharSequence> itemObjList = new ArrayList<>();
//                for (Item item : items) {
//                    itemObjList.add(item.getObjectId());
//                }
//                intent.putCharSequenceArrayListExtra("item_list", itemObjList);
//                intent.putExtra("yardsale", yardsale.getObjectId());
                getActivity().startActivity(intent);

            }
        });
        return view;
    }
}
