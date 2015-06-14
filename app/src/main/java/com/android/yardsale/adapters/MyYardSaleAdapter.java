package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.AddItemActivity;
import com.android.yardsale.models.YardSale;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;


public class MyYardSaleAdapter extends ParseQueryAdapter<YardSale> {

    private LayoutInflater inflater;

    private static class ViewHolder {
        ImageView ivYardSaleCoverPic;
        TextView tvYardSaleDescription;
        TextView tvYardSaleLoction;
        TextView tvYardSaleTime;
        Button btnAddItem;
        Button btnEditItem;
    }

    public MyYardSaleAdapter(Context context,
                             ParseQueryAdapter.QueryFactory<YardSale> queryFactory, LayoutInflater inflater) {
        super(context, queryFactory);
        this.inflater = inflater;
    }

    @Override
    public View getItemView(final YardSale yardSale, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.seller_item_row, parent, false);
            viewHolder.ivYardSaleCoverPic = (ImageView) view.findViewById(R.id.ivYardSaleCoverPic);
            viewHolder.tvYardSaleDescription = (TextView) view.findViewById(R.id.tvYardSaleDescription);
            viewHolder.tvYardSaleLoction = (TextView) view.findViewById(R.id.tvYardLocation);
            viewHolder.tvYardSaleTime = (TextView) view.findViewById(R.id.tvYardSaleTime);
            viewHolder.btnAddItem = (Button) view.findViewById(R.id.btnAddItem);
            viewHolder.btnEditItem = (Button) view.findViewById(R.id.btEditItem);
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
        if (yardSale.getLocation() != null)
            viewHolder.tvYardSaleLoction.setText(yardSale.getAddress().toString());

        viewHolder.tvYardSaleTime.setText(constructTime(yardSale.getStartTime(), yardSale.getEndTime()));

        viewHolder.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Adding Item", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), AddItemActivity.class);
                intent.putExtra("yard_sale_id", yardSale.getObjectId());
                ((Activity) getContext()).startActivityForResult(intent, 1076);
            }
        });

//        viewHolder.btnEditItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(),"Editing Item", Toast.LENGTH_LONG).show();
//                //TODO Editing items in a yard sale
//            }
//        });

        return view;
    }


    private String constructTime(Date start, Date end) {
        DateFormat format = DateFormat.getDateTimeInstance();
        return "Start: " + format.format(start) + " End: " + format.format(end);
    }

}
