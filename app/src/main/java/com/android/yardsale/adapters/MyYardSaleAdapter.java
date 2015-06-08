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
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyYardSaleAdapter extends ArrayAdapter<YardSale> {


    public MyYardSaleAdapter(Context context, List<YardSale> objects) {
        super(context, R.layout.seller_item_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YardSale yardSale = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.seller_item_row, parent, false);
        }
        ImageView ivYardSaleCoverPic = (ImageView) convertView.findViewById(R.id.ivYardSaleCoverPic);
        TextView tvYardSaleDescription = (TextView) convertView.findViewById(R.id.tvYardSaleDescription);
        TextView tvYardSaleLocation = (TextView) convertView.findViewById(R.id.tvYardSaleLocation);

        Picasso.with(getContext())
                .load(yardSale.getCoverPic().getUrl())
                .into(ivYardSaleCoverPic);
        tvYardSaleDescription.setText(yardSale.getDescription());
        tvYardSaleLocation.setText(yardSale.getLocation().toString());

        return super.getView(position, convertView, parent);
    }
}
