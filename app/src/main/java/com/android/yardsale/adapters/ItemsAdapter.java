package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditItemActivity;
import com.android.yardsale.activities.ItemDetailActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private YardSaleApplication client;
    long then;
    FragmentManager fm;

    public ItemsAdapter(FragmentManager supportFragmentManager, Context context, List<Item> items) {
        this.itemList = items;
        this.context = context;
        this.fm = supportFragmentManager;
        client = new YardSaleApplication();
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder viewHolder, final int position) {
        final Item item = itemList.get(position);
        Log.d("SEARCH", item.getDescription() + "\t" + item.getObjectId() + "\t" + item.getYardSale().getSeller());
        final YardSale sale = item.getYardSale();
        viewHolder.tvPrice.setTextColor(Color.WHITE);
        viewHolder.tvPrice.setText("$" + String.valueOf(item.getPrice()));

        if (item.getPhoto() != null)
            Picasso.with(context).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder_loading).into(viewHolder.ivPic);
        else
            Picasso.with(context).load(R.drawable.placeholder).into(viewHolder.ivPic);

        viewHolder.ivPic.setOnTouchListener(new View.OnTouchListener() {
            Item i = itemList.get(position);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > 500) {
                        /* Implement long click behavior here */
                        System.out.println("Long Click has happened!");
                        if (sale.getSeller() == ParseUser.getCurrentUser()) {
                            setPosition(viewHolder.getPosition());
                            viewHolder.showMenu();
                        } else {
                            Toast.makeText(context, "You are not the owner of this sale!", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    } else {
                        /* Implement short click behavior here or do nothing */
                        Log.e("ITEM VIEW", "Short Click has happened...");
//                        YardSaleApplication.launchItemDetailActivity(context, i);
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("selected_item", i.getObjectId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context.getApplicationContext()., viewHolder.ivPic, "itemDetail");
                        context.startActivity(intent);
                        return false;
                    }
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_sale_item, viewGroup, false);

        return new ItemViewHolder(itemView, context);
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onViewRecycled(ItemViewHolder holder) {
        //holder.ivPic.setOn(null);
        super.onViewRecycled(holder);
    }

    //fire edit intent, this does not refresh view
    public void edit(Activity newContext, int position) {
        Item i = itemList.get(position);
        Toast.makeText(context, "edit item!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra("item_id", i.getObjectId());
        newContext.startActivityForResult(intent, 21);
    }

    public void delete(Activity newContext, int position) {
        Item i = itemList.get(position);
        Toast.makeText(newContext, "delete item!", Toast.LENGTH_SHORT).show();
        client.deleteItem(fm, i);
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
        try {
            YardSale s = i.getYardSale().fetchIfNeeded();
            s.getItemsRelation().remove(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}



