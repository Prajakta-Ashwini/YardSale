package com.android.yardsale.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.yardsale.R;

public class MapFragment extends FindStuffFragment {
    public static MapFragment newInstance(){
        MapFragment fragmentDemo = new MapFragment();
        //Bundle args = new Bundle();
        //args.putInt("sale_list", list);
        //fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
