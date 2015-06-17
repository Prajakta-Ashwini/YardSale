package com.android.yardsale.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.adapters.BuySellPagerAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.astuetz.PagerSlidingTabStrip;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class ListActivity extends ActionBarActivity {

    private ViewPager vpPager;
    private BuySellPagerAdapter vpAdapter;
    private YardSaleApplication client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        client = new YardSaleApplication(this);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpAdapter = new BuySellPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(vpAdapter);

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.ruby)));

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String desc = data.getStringExtra("desc");
            String objId = data.getStringExtra("obj_id");
            if(objId == null) {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereEqualTo("title", title);
                query.whereEqualTo("description", desc);
                query.whereEqualTo("seller", YardSaleApplication.getCurrentUser());
                query.findInBackground(new FindCallback<YardSale>() {
                    @Override
                    public void done(List<YardSale> yardSale, ParseException e) {
                        if (e == null) {
                            vpAdapter.getFindStuffFragment().addYardSale(yardSale.get(0));
                        }
                    }
                });
            }else{
                ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
// Specify the object id
                query.getInBackground(objId, new GetCallback<YardSale>() {
                    public void done(YardSale item, ParseException e) {
                        if (e == null) {
//                            vpAdapter.getFindStuffFragment().removeYardSale(item);
//                            vpAdapter.getFindStuffFragment().addYardSale(item);
                            vpAdapter.getFindStuffFragment().editYardSale(item);
                        } else {
                            // something went wrong
                        }
                    }
                });
            }
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
        // int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.miFlip:
                vpAdapter.getFindStuffFragment().replace();
                return true;
            case R.id.miLogout:
                client.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (vpPager.getCurrentItem() == 1) {
                vpPager.setCurrentItem(0);
                return true;
            }
        }
        if (vpPager.getCurrentItem() == 0) {
            FragmentManager childFm = vpAdapter.getFindStuffFragment().getChildFragmentManager();
            if (childFm.getBackStackEntryCount() > 0) {
                childFm.popBackStack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
