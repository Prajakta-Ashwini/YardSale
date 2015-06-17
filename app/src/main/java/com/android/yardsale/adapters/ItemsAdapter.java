package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditItemActivity;
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

    public ItemsAdapter(Context context,List<Item> contactList) {
        this.itemList = contactList;
        this.context = context;
        client = new YardSaleApplication();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder viewHolder, final int position) {
        final Item item = itemList.get(position);
        final YardSale sale = item.getYardSale();
        viewHolder.tvDescription.setText(item.getDescription());
        viewHolder.tvPrice.setText( String.valueOf(item.getPrice()));
        try {
            String user = sale.getSeller().fetchIfNeeded().getUsername() ;
            if(sale.getSeller() == ParseUser.getCurrentUser()){
                viewHolder.btDeleteItem.setVisibility(View.VISIBLE);
                viewHolder.btEditItem.setVisibility(View.VISIBLE);
            }else{
                viewHolder.btDeleteItem.setVisibility(View.INVISIBLE);
                viewHolder.btEditItem.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(item.getPhoto() != null)
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


        viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {
            Item i = itemList.get(position);
            @Override
            public void onClick(View v) {
                client.launchItemDetailActivity(context, i, viewHolder.ivPic);
            }
        });


    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_sale_item, viewGroup, false);

        return new ItemViewHolder(itemView);
    }


}

