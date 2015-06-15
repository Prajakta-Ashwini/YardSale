package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ItemsArrayAdapter;
import com.android.yardsale.helpers.GridViewScrollable;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private ItemsArrayAdapter adapter;
    private ArrayList<Item> items;
    private YardSaleApplication client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //TODO customize the toolbar later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        items = new ArrayList<>();
        adapter = new ItemsArrayAdapter(this, items);
        client = new YardSaleApplication(this);

        GridViewScrollable gvSearchItems = (GridViewScrollable) findViewById(R.id.gvSearchItems);
        gvSearchItems.setExpanded(true);
        gvSearchItems.setAdapter(adapter);

        ArrayList<String> itemIds = getIntent().getStringArrayListExtra("search");
        for(String itemId : itemIds)
        {
            items.add(client.getItem(itemId));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
        if (id == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
