package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.fragments.FindStuffFragment;
import com.android.yardsale.fragments.SellStuffFragment;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.astuetz.PagerSlidingTabStrip;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends ActionBarActivity {

    private ViewPager vpPager;
    private BuySellPagerAdapter vpAdapter;
    private PagerSlidingTabStrip tabStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final YardSaleApplication client = new YardSaleApplication(this);

        String username = getIntent().getStringExtra("test_user_name");
        final List<CharSequence> yardSalesObjList = getIntent().getCharSequenceArrayListExtra("sale_list");
        final List<YardSale> yardSalesList = new ArrayList<>();
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpAdapter = new BuySellPagerAdapter(getSupportFragmentManager(), yardSalesList);
        vpPager.setAdapter(vpAdapter);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
        for(CharSequence objId:yardSalesObjList) {
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
            query.getInBackground((String) objId, new GetCallback<YardSale>() {
                public void done(YardSale item, ParseException e) {
                    if (e == null) {
                        // item was found
                        //yardSalesList.add(item);
                        vpAdapter.addNewRow(item);

                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //return the order of fragments in the view pager
    public class BuySellPagerAdapter extends FragmentPagerAdapter {
        private String tabtitles[] = {"Find Stuff", "Sell Stuff"};  //to be replaced with icons
        FindStuffFragment hf;
        SellStuffFragment mf;

        public BuySellPagerAdapter(FragmentManager fm, List<YardSale> yardSalesList) {
            super(fm);
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
}
