package com.android.yardsale.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.fragments.SaleMapFragment;
import com.android.yardsale.models.YardSale;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class YardSaleInfoActivity extends ActionBarActivity {
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDateTime;
    private TextView tvAddress;
    private TextView tvSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yard_sale_info);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvSeller = (TextView) findViewById(R.id.tvSeller);
        final String yardsaleObj = getIntent().getStringExtra("yardsale_id");
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
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yard_sale_info, menu);
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
