package com.android.yardsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Prajakta on 6/6/2015.
 */
public class SalesArrayAdapter  extends ArrayAdapter<YardSale> {
    private static class ViewHolderSale {
        TextView tvTitle;
        TextView tvAddedBy;
        ImageView ivCoverPic;
    }

    public SalesArrayAdapter(Context context, List<YardSale> yardSalesList) {
        super(context, android.R.layout.simple_list_item_1, yardSalesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YardSale sale = getItem(position);
        ViewHolderSale viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolderSale();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_yardsale, parent, false);
            viewHolder.ivCoverPic = (ImageView) convertView.findViewById(R.id.ivCoverPic);
            viewHolder.tvAddedBy = (TextView) convertView.findViewById(R.id.tvAddedBy);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSale) convertView.getTag();
        }
        viewHolder.tvTitle.setText(sale.getTitle());
        try {
            viewHolder.tvAddedBy.setText("Added by " + sale.getSeller().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Picasso.with(getContext()).load(R.drawable.placeholder).into(viewHolder.ivCoverPic);
        //return view to be inserted in the list
        return convertView;
    }
}
