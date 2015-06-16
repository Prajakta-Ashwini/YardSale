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
import android.widget.ImageView;
import android.widget.ListView;

import com.android.yardsale.R;
import com.android.yardsale.activities.YardSaleDetailActivity;
import com.android.yardsale.adapters.MyYardSaleAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ListingsFragment extends Fragment {

    private MyYardSaleAdapter adapter;
    private YardSaleApplication client;
    private ParseUser currentUser;
    private ProfilePictureView userProfilePicture;

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
        userProfilePicture = (ProfilePictureView) header.findViewById(R.id.userProfilePicture);
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            client.makeMeRequest(userProfilePicture);
        }
        ListView lvMyYardSales = (ListView) view.findViewById(R.id.lvMyYardSales);
        lvMyYardSales.addHeaderView(header);
        lvMyYardSales.setAdapter(adapter);


        lvMyYardSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO launch detailed activity page
                Intent intent = new Intent(getActivity(), YardSaleDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //item Object Ids
                //YardSaleId --> yardsale
                //TODO figure out how to send this info
                getActivity().startActivity(intent);

            }
        });
        return view;
    }

    public void addYardSale(YardSale row) {
        //TODO figure out how to add this to the listview
    }
}
