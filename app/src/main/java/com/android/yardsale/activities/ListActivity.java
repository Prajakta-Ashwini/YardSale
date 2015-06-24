package com.android.yardsale.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.fragments.ListingsFragment;
import com.android.yardsale.fragments.MyFavoritesFragment;
import com.android.yardsale.fragments.PastHistoryFragment;
import com.android.yardsale.fragments.SalesFragment;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.helpers.image.CircleTransformation;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListActivity extends ActionBarActivity {
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
        ab.setHomeAsUpIndicator(R.drawable.ic_profile);
        ab.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        salesFragment = SalesFragment.newInstance();
        listingsFragment = ListingsFragment.newInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));

        defaultDrawerItem(0);
        nvDrawer.addHeaderView(getHeaderForNavDrawer());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String desc = data.getStringExtra("desc");
            String objId = data.getStringExtra("obj_id");
            String price = data.getStringExtra("price");
            if (objId == null) {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereEqualTo("title", title);
                query.whereEqualTo("description", desc);
                query.whereEqualTo("seller", YardSaleApplication.getCurrentUser());
                query.findInBackground(new FindCallback<YardSale>() {
                    @Override
                    public void done(List<YardSale> yardSale, ParseException e) {
                        if (e == null) {
                            listingsFragment.addYardSale(yardSale.get(0));
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
                            listingsFragment.editYardSale(item);
                        } else {
                            // something went wrong
                        }
                    }
                });
            }
        } else if (resultCode == 143) {
            String price = data.getStringExtra("price");
            String desc = data.getStringExtra("desc");
            final String objId = data.getStringExtra("obj_id");
            if (objId == null) {
                ParseQuery<Item> query = Item.getQuery();
                //query.whereEqualTo("price", price);
                query.whereEqualTo("description", desc);
                //query.whereEqualTo("seller", YardSaleApplication.getCurrentUser());

                query.findInBackground(new FindCallback<Item>() {
                    @Override
                    public void done(List<Item> items, ParseException e) {
                        if (e == null) {
                            Item item = items.get(0);
                            listingsFragment.adapter.parseAdapter.loadObjects();
                            salesFragment.adapter.parseAdapter.loadObjects();
                            // itemlist add
                            YardSale s = item.getYardSale();
                            s.getItemsRelation().add(item);
                            s.saveInBackground();
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

            case R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
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
        Fragment fragment;

        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragment = salesFragment;
                break;
            case R.id.nav_second_fragment:
                fragment = listingsFragment;
                break;
            case R.id.nav_third_fragment:
                fragment = PastHistoryFragment.newInstance();
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

    private View getHeaderForNavDrawer() {
        //TODO work on the look and feel
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        View convertView = inflater.inflate(R.layout.nav_header, null);
        Picasso.with(this)
                .load(R.drawable.profile_background)
                .fit().centerInside()
                .skipMemoryCache()
                .transform(new jp.wasabeef.picasso.transformations.BlurTransformation(getBaseContext(), 25))
                .into((ImageView) convertView.findViewById(R.id.ivProfileBackground));

        ImageView ivUserProfilePic = (ImageView) convertView.findViewById(R.id.ivUserProfilePic);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.getParseFile("profile_pic") == null) {
            if (currentUser.getString("profile_pic_url") == null) {
                Picasso.with(this)
                        .load(R.drawable.com_facebook_profile_picture_blank_square)
                        .transform(new CircleTransformation())
                        .into(ivUserProfilePic);
            } else {
                Picasso.with(this)
                        .load(currentUser.getString("profile_pic_url"))
                        .transform(new CircleTransformation())
                        .into(ivUserProfilePic);
            }
        } else {
            Picasso.with(this)
                    .load(currentUser.getParseFile("profile_pic").getUrl())
                    .transform(new CircleTransformation())
                    .into(ivUserProfilePic);
        }

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setTextColor(Color.DKGRAY);
        tvUserName.setText(currentUser.getUsername());

        TextView tvWishList = (TextView) convertView.findViewById(R.id.tvWishList);
        tvWishList.setClickable(true);

        tvWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Wishlist clicked", Toast.LENGTH_SHORT).show();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.flContent, MyFavoritesFragment.newInstance());
                transaction.commit();
                //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                // Highlight the selected item, update the title, and close the drawer
                mDrawerLayout.closeDrawers();

            }
        });
        return convertView;
    }
}
