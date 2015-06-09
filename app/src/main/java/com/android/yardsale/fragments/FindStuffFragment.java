package com.android.yardsale.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.yardsale.R;
import com.android.yardsale.listeners.ReplaceListener;
import com.android.yardsale.models.YardSale;

import java.util.ArrayList;
import java.util.List;

//http://stackoverflow.com/questions/13379194/how-to-add-a-fragment-inside-a-viewpager-using-nested-fragment-android-4-2
public class FindStuffFragment extends Fragment implements
        ReplaceListener {
    private static int currentFragment;
    private static List<YardSale> yardSaleList;

    public static FindStuffFragment newInstance(int position) {
        FindStuffFragment wp = new FindStuffFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        wp.setArguments(args);
        currentFragment = 1;
        yardSaleList = new ArrayList<>();
        return wp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_stuff, container, false);
        FrameLayout fl = (FrameLayout) v.findViewById(R.id.flPage);
        if (getChildFragmentManager().findFragmentByTag("initialTag") == null) {
            SaleListFragment iif = new SaleListFragment();
            Bundle args = new Bundle();
            args.putInt("position", 1);
            iif.setArguments(args);
            getChildFragmentManager().beginTransaction()
                                    .add(R.id.flPage, iif, "initialTag")
                                    .addToBackStack(null)
                                    .commit();

            currentFragment = 1;
        }
        return fl;
    }

    // required because it seems the getChildFragmentManager only "sees"
    // containers in the View of the parent Fragment.
    @Override
    public void onReplace(Bundle args) {

        if(currentFragment == 1) {
            SaleMapFragment iif;
            if (getChildFragmentManager().findFragmentByTag("afterTag") == null) {
                iif = SaleMapFragment.newInstance();
                args.putInt("position", 2);
                iif.setArguments(args);
            }else{
                iif = (SaleMapFragment) getChildFragmentManager().findFragmentByTag("afterTag");
                iif.getArguments().putInt("position", 2);;
            }

            getChildFragmentManager().beginTransaction().replace(R.id.flPage, iif, "afterTag")
                                                        .addToBackStack("afterTag")
                                                        .commit();

            currentFragment = 2;
        }else{
            SaleListFragment iif;
            if (getChildFragmentManager().findFragmentByTag("initialTag") == null) {
                iif = SaleListFragment.newInstance();
                iif.setArguments(args);
            }else{
                iif = (SaleListFragment) getChildFragmentManager().findFragmentByTag("initialTag");
                iif.getArguments().putAll(args);
            }

            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.flPage, iif, "initialTag")
                                    .addToBackStack("initialTag")
                                    .commit();
            currentFragment = 1;

        }
    }


    public void addYardSale(YardSale row) {
        SaleListFragment iif;
        if (getChildFragmentManager().findFragmentByTag("initialTag") != null) {

            iif = (SaleListFragment) getChildFragmentManager().findFragmentByTag("initialTag");

            iif.addYardSale(row);
            yardSaleList.add(row);
            //this.notifyDataSetChanged();
        }
    }

    public List<YardSale> getYardSaleList(){
        return yardSaleList;
    }

    public void replace() {
        Bundle b = new Bundle();
        //ReplaceListener mListener = (ReplaceListener) this.getParentFragment();
        onReplace(b);

    }


}