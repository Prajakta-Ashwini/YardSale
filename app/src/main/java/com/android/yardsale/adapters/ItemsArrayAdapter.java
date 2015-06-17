package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditItemActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsArrayAdapter extends ArrayAdapter<Item> {
    private YardSaleApplication client;
    private Context context;

    private static class ViewHolderSale {
        ImageView ivPic;
        TextView tvDescription;
        TextView tvPrice;
        Button btEditItem;
        Button btDeleteItem;
    }

    public ItemsArrayAdapter(Context context, List<Item> itemList) {
        super(context, R.layout.item_sale_item, itemList);
        client = new YardSaleApplication();
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position);
        final ViewHolderSale viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolderSale();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_sale_item, parent, false);
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.btDeleteItem = (Button) convertView.findViewById(R.id.btDeleteItem);
            viewHolder.btEditItem = (Button) convertView.findViewById(R.id.btEditItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSale) convertView.getTag();
        }
        viewHolder.tvDescription.setText(item.getDescription());
        viewHolder.tvPrice.setText(item.getPrice().toString());
        Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(viewHolder.ivPic);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseUser owner = item.getYardSale().getSeller();
        if(currentUser.getObjectId().equals(owner.getObjectId()))
        {
            viewHolder.btEditItem.setVisibility(View.VISIBLE);
            viewHolder.btDeleteItem.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btDeleteItem.setVisibility(View.INVISIBLE);
            viewHolder.btEditItem.setVisibility(View.INVISIBLE);
        }

        viewHolder.btDeleteItem.setOnClickListener(new View.OnClickListener() {
            Item item = getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "delete item!!!", Toast.LENGTH_SHORT).show();
                client.deleteItem(item);
                remove(item);
                notifyDataSetChanged();
            }
        });
        viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {
            Item item = getItem(position);

            @Override
            public void onClick(View v) {
                client.launchItemDetailActivity(getContext(), item, viewHolder.ivPic);
            }
        });

        viewHolder.btEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditItemActivity.class);
                intent.putExtra("item_id", item.getObjectId());
                ((Activity)getContext()).startActivityForResult(intent,21);
            }
        });
        return convertView;
    }
}
