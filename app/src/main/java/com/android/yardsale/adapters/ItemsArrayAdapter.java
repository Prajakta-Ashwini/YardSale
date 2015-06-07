package com.android.yardsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.models.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Prajakta on 6/6/2015.
 */
public class ItemsArrayAdapter extends ArrayAdapter<Item> {
    private static class ViewHolderSale {
        ImageView ivPic;
        TextView tvDescription;
        TextView tvPrice;
    }

    public ItemsArrayAdapter(Context context, List<Item> itemList) {
        super(context, android.R.layout.simple_list_item_1, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        ViewHolderSale viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolderSale();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_sale_item, parent, false);
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSale) convertView.getTag();
        }
        viewHolder.tvDescription.setText(item.getDescription());
        viewHolder.tvPrice.setText(item.getPrice().toString());
        Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(viewHolder.ivPic);
        return convertView;
    }
}
