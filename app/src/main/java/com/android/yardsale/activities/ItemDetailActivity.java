package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.yardsale.R;
import com.android.yardsale.helpers.image.TouchImageView;
import com.android.yardsale.models.Item;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

public class ItemDetailActivity extends ActionBarActivity {
    private TouchImageView ivImageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        final String objId = getIntent().getStringExtra("selected_item");
        Log.e("ITEM_DETAIL", objId);
        ivImageResult = (TouchImageView) findViewById(R.id.ivFullImage);

        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.getInBackground(objId, new GetCallback<Item>() {
            public void done(Item item, ParseException e) {
                if (e == null) {
                    //set values
                    Log.e("item_get", item.getDescription() + item.getPhoto().getUrl());
                    if (item.getPhoto().getUrl() != null) {
                        Picasso.with(getBaseContext())
                                .load(item.getPhoto().getUrl())
                                .into(ivImageResult);
                    } else {
                        Picasso.with(getBaseContext())
                                .load(R.drawable.placeholder)
                                .into(ivImageResult);
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
