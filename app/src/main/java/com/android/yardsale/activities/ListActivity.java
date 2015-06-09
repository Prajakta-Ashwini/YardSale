package com.android.yardsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.BuySellPagerAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.astuetz.PagerSlidingTabStrip;
import com.parse.GetCallback;
import com.parse.ParseQuery;

import java.util.List;

public class ListActivity extends ActionBarActivity {

    private ViewPager vpPager;
    private BuySellPagerAdapter vpAdapter;
    private PagerSlidingTabStrip tabStrip;
    private YardSaleApplication client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        client = new YardSaleApplication(this);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpAdapter = new BuySellPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(vpAdapter);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

//        ArrayList<String> myYardSales = client.getMyYardSales();
//        Log.d("DEBUG 1:", myYardSales.size() + "");
//        //ArrayList<CharSequence> myYardSales = getIntent().getCharSequenceArrayListExtra("my_yard_sales");
//        if (!myYardSales.isEmpty()) {
//            for (String myYardSaleId : myYardSales) {
//                ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
//                query.getInBackground(myYardSaleId, new GetCallback<YardSale>() {
//                    @Override
//                    public void done(YardSale yardSale, ParseException e) {
//                        if (e == null) {
//                            vpAdapter.addNewRow(yardSale, BuySellPagerAdapter.Type.SELLER);
//                        }
//                    }
//                });
//            }
//        }

        List<CharSequence> yardSalesObjList = getIntent().getCharSequenceArrayListExtra("sale_list");

        //TODO make this generic
        for (CharSequence objId : yardSalesObjList) {
            //client.queryYardSale(String objectId)
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
     //       query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
            query.getInBackground((String) objId, new GetCallback<YardSale>() {
                @Override
                public void done(YardSale yardSale, com.parse.ParseException e) {
                    if (e == null) {
                        vpAdapter.addNewRow(yardSale, BuySellPagerAdapter.Type.BUYER);
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        MenuItem item = menu.findItem(R.id.miSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                client.searchForItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.miFlip) {
            Bundle args = new Bundle();
            vpAdapter.getFindStuffFragment().replace();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 200) {

            Toast.makeText(this, "200", Toast.LENGTH_LONG).show();
        } else if (resultCode == 201) {
            Toast.makeText(this, "201", Toast.LENGTH_LONG).show();
        }
    }
}
