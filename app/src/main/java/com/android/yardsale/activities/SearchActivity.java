package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private YardSaleApplication client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //TODO the list has to be changed to item
//        items = new ArrayList<>();
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
//        ListView lvSearchElements = (ListView) findViewById(R.id.lvSearchElements);
//        lvSearchElements.setAdapter(adapter);
//        client = new YardSaleApplication(this);
        String searchText = getIntent().getStringExtra("query");
       // search(searchText);

        Toast.makeText(this,searchText, Toast.LENGTH_LONG).show();
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

        return super.onOptionsItemSelected(item);
    }

    private void search(String query) {
        client.searchForItems(query);
    }
}
