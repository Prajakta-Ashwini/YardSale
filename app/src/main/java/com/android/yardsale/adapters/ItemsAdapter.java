package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditItemActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {


    private List<Item> itemList;
    private Context context;
    private YardSaleApplication client;
    long then;

    public ItemsAdapter(Context context, List<Item> contactList) {
        this.itemList = contactList;
        this.context = context;
        client = new YardSaleApplication();
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder viewHolder, final int position) {
        final Item item = itemList.get(position);
        Log.d("SEARCH", item.getDescription() + "\t" + item.getObjectId() + "\t" + item.getYardSale().getSeller());
        final YardSale sale = item.getYardSale();
        viewHolder.tvPrice.setTextColor(Color.WHITE);
        viewHolder.tvPrice.setText("$" + String.valueOf(item.getPrice()));

        if (sale.getSeller() == ParseUser.getCurrentUser()) {
            viewHolder.btDeleteItem.setVisibility(View.VISIBLE);
            viewHolder.btEditItem.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btDeleteItem.setVisibility(View.INVISIBLE);
            viewHolder.btEditItem.setVisibility(View.INVISIBLE);
        }

        if (item.getPhoto() != null)
            Picasso.with(context).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(viewHolder.ivPic);
        else
            Picasso.with(context).load(R.drawable.placeholder).into(viewHolder.ivPic);


        viewHolder.btDeleteItem.setOnClickListener(new View.OnClickListener() {
            Item i = itemList.get(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete item!", Toast.LENGTH_SHORT).show();
                client.deleteItem(i);
                itemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
            }
        });

        //TODO remove the edit and delete button

        viewHolder.btEditItem.setOnClickListener(new View.OnClickListener() {
            Item i = itemList.get(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "edit item!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EditItemActivity.class);
                intent.putExtra("item_id", i.getObjectId());
                ((Activity) context).startActivityForResult(intent, 21);
            }
        });


//        viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {
//            Item i = itemList.get(position);
//
//            @Override
//            public void onClick(View v) {
//                client.launchItemDetailActivity(context, i, viewHolder.ivPic);
//            }
//        });

        viewHolder.ivPic.setOnTouchListener(new View.OnTouchListener() {
            Item i = itemList.get(position);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = (long) System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > 500) {
            /* Implement long click behavior here */
                        System.out.println("Long Click has happened!");
                        setPosition(viewHolder.getPosition());
                        viewHolder.showMenu();
                        return false;
                    } else {
            /* Implement short click behavior here or do nothing */
                        System.out.println("Short Click has happened...");
                        client.launchItemDetailActivity(context, i, viewHolder.ivPic);
                        return true;
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
        client.deleteItem(i);
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }

}



