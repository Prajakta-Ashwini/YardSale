package com.android.yardsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.models.YardSale;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;


public class MyYardSaleAdapter extends ParseQueryAdapter<YardSale> {

    private LayoutInflater inflater;

    private static class ViewHolder {
        ImageView ivYardSaleCoverPic;
        TextView tvYardSaleDescription;
        TextView tvYardSaleTime;
    }

    public MyYardSaleAdapter(Context context,
                             ParseQueryAdapter.QueryFactory<YardSale> queryFactory, LayoutInflater inflater) {
        super(context, queryFactory);
        this.inflater = inflater;
    }

    @Override
    public View getItemView(YardSale yardSale, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.seller_item_row, parent, false);
            //TODO add the location element
            viewHolder.ivYardSaleCoverPic = (ImageView) view.findViewById(R.id.ivYardSaleCoverPic);
            viewHolder.tvYardSaleDescription = (TextView) view.findViewById(R.id.tvYardSaleDescription);
            viewHolder.tvYardSaleTime = (TextView) view.findViewById(R.id.tvYardSaleTime);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (yardSale.getCoverPic() != null && yardSale.getCoverPic().getUrl() != null) {
            Picasso.with(getContext())
                    .load(yardSale.getCoverPic().getUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.ivYardSaleCoverPic);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.placeholder)
                    .into(viewHolder.ivYardSaleCoverPic);
        }

        viewHolder.tvYardSaleDescription.setText(yardSale.getDescription());
        viewHolder.tvYardSaleTime.setText(yardSale.getLocation().toString());
        return view;
    }

}
