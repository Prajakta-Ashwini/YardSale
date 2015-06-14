package com.android.yardsale.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.yardsale.fragments.FindStuffFragment;
import com.android.yardsale.fragments.SellStuffFragment;
import com.android.yardsale.models.YardSale;

//return the order of fragments in the view pager
public class BuySellPagerAdapter extends FragmentPagerAdapter {
    private String tabtitles[] = {"Find Stuff", "Sell Stuff"};  //to be replaced with icons
    private FindStuffFragment findStuffFragment;
    private SellStuffFragment sellStuffFragment;

    public enum Type {
        SELLER, BUYER
    }


    public BuySellPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (findStuffFragment == null) {
                    findStuffFragment = FindStuffFragment.newInstance(0);
                }
                return findStuffFragment;
            case 1:
                if (sellStuffFragment == null)
                    sellStuffFragment = SellStuffFragment.newInstance();
                return sellStuffFragment;
            default:
                if (findStuffFragment == null) {
                    findStuffFragment = FindStuffFragment.newInstance(0);
                }
                return findStuffFragment;
        }
    }

    public FindStuffFragment getFindStuffFragment() {
        return findStuffFragment;
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    public void addNewRow(YardSale row, Type type) {
        switch (type) {
            case BUYER:
                findStuffFragment.addYardSale(row);
                break;
            case SELLER:
                sellStuffFragment.addYardSale(row);
                //break;
        }

    }


}