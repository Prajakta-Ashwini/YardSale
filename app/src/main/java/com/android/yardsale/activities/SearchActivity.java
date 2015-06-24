package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ItemsAdapter;
import com.android.yardsale.models.Item;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private ItemsAdapter adapter;
    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //TODO customize the toolbar later - back button

        items = new ArrayList<>();

        ArrayList<String> seachObjectIds = getIntent().getStringArrayListExtra("search");

        RecyclerView rvItems = (RecyclerView) findViewById(R.id.rvItems);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvItems.setHasFixedSize(true);
        rvItems.setLayoutManager(llm);
        rvItems.setItemAnimator(new DefaultItemAnimator());
        adapter = new ItemsAdapter(getSupportFragmentManager(), this, items);
        rvItems.setAdapter(adapter);


        if (seachObjectIds != null) {
            for (String objId : seachObjectIds) {
                ParseQuery<Item> query = Item.getQuery();
                query.getInBackground(objId, new GetCallback<Item>() {
                    public void done(Item item, ParseException e) {
                        if (e == null) {
                            items.add(item);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
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
