package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ItemsArrayAdapter;
import com.android.yardsale.fragments.SaleMapFragment;
import com.android.yardsale.helpers.GridViewScrollable;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class YardSaleDetailActivity extends ActionBarActivity {
    private GridViewScrollable gvItems;
    private ItemsArrayAdapter aItems;
    private List<Item> itemList;
    private Button btCreateItem;
    private Button btDeleteItem;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDateTime;
    private TextView tvAddress;
    private TextView tvSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO back button
        final YardSaleApplication client = new YardSaleApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yard_sale_detail);
        gvItems = (GridViewScrollable) findViewById(R.id.gvItems);
        btCreateItem = (Button) findViewById(R.id.btCreateItem);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvSeller = (TextView) findViewById(R.id.tvSeller);
        btDeleteItem = (Button) findViewById(R.id.btDeleteItem);

        itemList = new ArrayList<>();
        final List<CharSequence> itemsObjList = getIntent().getCharSequenceArrayListExtra("item_list");
        final String yardsaleObj = getIntent().getStringExtra("yardsale");
        aItems = new ItemsArrayAdapter(this,itemList);
        gvItems.setExpanded(true);
        gvItems.setAdapter(aItems);

        for(CharSequence objId:itemsObjList) {
            ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
            //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
            query.getInBackground((String) objId, new GetCallback<Item>() {
                public void done(Item item, ParseException e) {
                    if (e == null) {
                        aItems.add(item);
                        aItems.notifyDataSetChanged();
                    }
                }
            });
        }
        ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.getInBackground(yardsaleObj, new GetCallback<YardSale>() {
            public void done(final YardSale sale, ParseException e) {
                if (e == null) {
                    tvTitle.setText(sale.getTitle());
                    tvDescription.setText(sale.getDescription());
                    tvDateTime.setText(sale.getStartTime().toString() + " to " + sale.getEndTime().toString());
                    tvAddress.setText("Location: " + sale.getAddress());
                    tvSeller.setText("Added By: " + sale.getSeller().getUsername());

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.flMap, SaleMapFragment.newInstance(sale));
                    ft.commit();
                    btCreateItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getBaseContext(), "creating item!!!", Toast.LENGTH_SHORT).show();
                            byte[] data = "Working at Parse is great!".getBytes();
                            ParseFile file = new ParseFile("filedata.txt", data);
                            try {
                                file.save();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            client.createItem("like new chair", 12, file, sale);
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yard_sale_detail, menu);
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
}
