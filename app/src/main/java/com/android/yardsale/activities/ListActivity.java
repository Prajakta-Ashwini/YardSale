package com.android.yardsale.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.adapters.BuySellPagerAdapter;
import com.android.yardsale.fragments.ListingsFragment;
import com.android.yardsale.fragments.SalesFragment;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class ListActivity extends ActionBarActivity {

    private ViewPager vpPager;
    private BuySellPagerAdapter vpAdapter;
    private YardSaleApplication client;
    private DrawerLayout mDrawerLayout;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private SalesFragment salesFragment;
    private ListingsFragment listingsFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        client = new YardSaleApplication(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        salesFragment = SalesFragment.newInstance();
        listingsFragment = ListingsFragment.newInstance();
//   PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//        tabStrip.setViewPager(vpPager);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.ruby)));

       //defaultDrawerItem(0);

//        List<CharSequence> yardSalesObjList = getIntent().getCharSequenceArrayListExtra("sale_list");
//
//        //TODO make this generic
//        for (CharSequence objId : yardSalesObjList) {
//            //client.queryYardSale(String objectId)
//            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
//            //       query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
//            query.getInBackground((String) objId, new GetCallback<YardSale>() {
//                @Override
//                public void done(YardSale yardSale, com.parse.ParseException e) {
//                    if (e == null) {
//                        saleListFragment.addYardSale(yardSale);
//                    }
//                }
//            });
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String desc = data.getStringExtra("desc");
            String objId = data.getStringExtra("obj_id");
            if (objId == null) {
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
            } else {
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
        //TODO clean this up
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.miFlip:
                vpAdapter.getFindStuffFragment().replace();
                return true;
            case R.id.home:
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void defaultDrawerItem(int position) {
        if (position == 0) {
           // FindStuffFragment fragment = FindStuffFragment.newInstance(0);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flContent, salesFragment);
            transaction.commit();


            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item, update the title, and close the drawer
            mDrawerLayout.closeDrawers();
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragment = salesFragment;
                break;
            case R.id.nav_second_fragment:
                fragment = listingsFragment;
                break;
            case R.id.nav_third_fragment:
                //TODO create yard sale
                // fragmentClass = ThirdFragment.class;
                break;
            default:
                fragment = salesFragment;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }
}
