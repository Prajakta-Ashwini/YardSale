package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.yardsale.R;

/**
 * Created by ashwini on 6/19/15.
 */
public class NoFavoritesFragment extends Fragment {

    private static final String TAG = "NoFavoritesFragment";
    public NoFavoritesFragment() {
    }

    public static NoFavoritesFragment newInstance()
    {
        return new NoFavoritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_favortie_fragment_layout, container, false);
        view.setTag(TAG);
        return view;
    }

}
