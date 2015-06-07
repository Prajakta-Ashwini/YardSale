package com.android.yardsale.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.yardsale.fragments.FindStuffFragment;
import com.android.yardsale.fragments.SellStuffFragment;
import com.android.yardsale.models.YardSale;

import java.util.List;

//return the order of fragments in the view pager
public class BuySellPagerAdapter extends FragmentPagerAdapter {
    private String tabtitles[] = {"Find Stuff", "Sell Stuff"};  //to be replaced with icons
    FindStuffFragment hf;
    SellStuffFragment mf;
    List<YardSale> yardSalesList;

    public BuySellPagerAdapter(FragmentManager fm, List<YardSale> yardSalesList) {
        super(fm);
        this.yardSalesList = yardSalesList;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (hf == null)
                hf = FindStuffFragment.newInstance();
            return hf;
        } else if (position == 1) {
            if (mf == null)
                mf = new SellStuffFragment();
            return mf;
        } else
            return null;
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    public void addNewRow(YardSale row){
        hf.addYardSale(row);
        //this.notifyDataSetChanged();
    }

}