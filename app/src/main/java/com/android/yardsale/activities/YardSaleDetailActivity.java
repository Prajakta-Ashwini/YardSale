package com.android.yardsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ItemsAdapter;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class YardSaleDetailActivity extends ActionBarActivity {
    private RecyclerView rvItems;
    private ItemsAdapter aItems;
    private List<Item> itemList;
    private FloatingActionButton btCreateItem;
    private YardSale sale;
//    private TextView tvTitle;
//    private TextView tvDescription;
//    private TextView tvDateTime;
//    private TextView tvAddress;
//    private TextView tvSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO back button
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yard_sale_detail);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        btCreateItem = (FloatingActionButton) findViewById(R.id.btCreateItem);
        btCreateItem.attachToRecyclerView(rvItems);
        btCreateItem.setColorNormal(getResources().getColor(R.color.ruby));
//        tvTitle = (TextView) findViewById(R.id.tvTitle);
//        tvDescription = (TextView) findViewById(R.id.tvDescription);
//        tvAddress = (TextView) findViewById(R.id.tvAddress);
//        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
//        tvSeller = (TextView) findViewById(R.id.tvSeller);

        itemList = new ArrayList<>();
        final List<CharSequence> itemsObjList = getIntent().getCharSequenceArrayListExtra("item_list");
        final String yardsaleObj = getIntent().getStringExtra("yardsale");
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvItems.setHasFixedSize(true);
        rvItems.setLayoutManager(llm);
        rvItems.setItemAnimator(new DefaultItemAnimator());

        aItems = new ItemsAdapter(this,itemList);
        rvItems.setAdapter(aItems);

//        aItems = new ItemsAdapter(this, itemList);
//        //gvItems.setExpanded(true);
//        rvItems.setAdapter(aItems);
        if(itemsObjList!=null) {
            for (CharSequence objId : itemsObjList) {
                ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
                //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
                query.getInBackground((String) objId, new GetCallback<Item>() {
                    public void done(Item item, ParseException e) {
                        if (e == null) {
                            itemList.add(item);
                            aItems.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
        if(yardsaleObj!=null) {
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
            //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
            query.getInBackground(yardsaleObj, new GetCallback<YardSale>() {
                public void done(final YardSale sale, ParseException e) {
                    if (e == null) {
                        //                    tvTitle.setText(sale.getTitle());
                        //                    tvDescription.setText(sale.getDescription());
                        //                    tvDateTime.setText(sale.getStartTime().toString() + " to " + sale.getEndTime().toString());
                        //                    tvAddress.setText("Location: " + sale.getAddress());
                        //                    tvSeller.setText("Added By: " + sale.getSeller().getUsername());

                        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        //ft.add(R.id.flMap, SaleMapFragment.newInstance(sale));
                        //ft.commit();
                        setSale(sale);
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        ParseUser owner = sale.getSeller();
                        if (currentUser.getObjectId().equals(owner.getObjectId())) {
                            btCreateItem.setVisibility(View.VISIBLE);
                        } else {
                            btCreateItem.setVisibility(View.INVISIBLE);
                        }

                        btCreateItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getBaseContext(), "creating item!!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(YardSaleDetailActivity.this, AddItemActivity.class);
                                intent.putExtra("yard_sale_id", sale.getObjectId());
                                startActivityForResult(intent, 22);
                            }
                        });

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String price = data.getStringExtra("price");
            String desc = data.getStringExtra("desc");
            final String objId = data.getStringExtra("obj_id");
            if(objId == null) {
                ParseQuery<Item> query = Item.getQuery();
                //query.whereEqualTo("price", price);
                query.whereEqualTo("description", desc);
                //query.whereEqualTo("seller", YardSaleApplication.getCurrentUser());

                query.findInBackground(new FindCallback<Item>() {
                   @Override
                   public void done(List<Item> items, ParseException e) {
                       if (e == null) {
                           itemList.add(items.get(0));
                           aItems.notifyDataSetChanged();
                       }
                   }
               });

            }else{
                ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
                query.getInBackground(objId, new GetCallback<Item>() {
                    public void done(Item item, ParseException e) {
                        if (e == null) {
                            for(Item i:itemList){
                                if(i.getObjectId().equals(objId)){
                                    i.setDescription(item.getDescription());
                                    i.setPhoto(item.getPhoto());
                                    i.setPrice(item.getPrice());
                                    aItems.notifyDataSetChanged();
                                }
                            }
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
        getMenuInflater().inflate(R.menu.menu_yard_sale_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.ic_info){
            Intent intent = new Intent(this,YardSaleInfoActivity.class);
            intent.putExtra("yardsale_id", sale.getObjectId());
            startActivity(intent);
        }
        //noinspection SimplifiableIfStatement
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

    public void setSale(YardSale sale) {
        this.sale = sale;
    }
}
