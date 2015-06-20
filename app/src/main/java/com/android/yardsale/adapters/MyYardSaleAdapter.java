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
import com.android.yardsale.activities.EditYardSaleActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;


public class MyYardSaleAdapter extends ParseQueryAdapter<YardSale> {

    private LayoutInflater inflater;
    private YardSaleApplication client;

    private static class ViewHolder {
        ImageView ivYardSaleCoverPic;
        TextView tvYardSaleDescription;
        TextView tvYardSaleLoction;
        TextView tvYardSaleTime;
        Button btnAddItem;
        Button btnEditYS;
        Button btnDeleteYS;
    }

    public MyYardSaleAdapter(Context context,
                             ParseQueryAdapter.QueryFactory<YardSale> queryFactory, LayoutInflater inflater) {
        super(context, queryFactory);
        this.inflater = inflater;
        client = new YardSaleApplication((Activity) getContext());
    }

    @Override
    public View getItemView(final YardSale yardSale, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.seller_yard_sale_row, parent, false);
            viewHolder.ivYardSaleCoverPic = (ImageView) view.findViewById(R.id.ivYardSaleCoverPic);
            viewHolder.tvYardSaleDescription = (TextView) view.findViewById(R.id.tvYardSaleDescription);
            viewHolder.tvYardSaleLoction = (TextView) view.findViewById(R.id.tvYardLocation);
            viewHolder.tvYardSaleTime = (TextView) view.findViewById(R.id.tvYardSaleTime);
            //viewHolder.btnAddItem = (Button) view.findViewById(R.id.btnAddItem);
            viewHolder.btnEditYS = (Button) view.findViewById(R.id.btnEditYS);
            viewHolder.btnDeleteYS = (Button) view.findViewById(R.id.btnDeleteYS);
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

//        viewHolder.btnAddItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Adding Item", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getContext(), AddItemActivity.class);
//                intent.putExtra("yard_sale_id", yardSale.getObjectId());
//                ((Activity) getContext()).startActivityForResult(intent, 1076);
//            }
//        });

        viewHolder.btnEditYS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "editing a YS", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), EditYardSaleActivity.class);
                intent.putExtra("edit_yard_sale_id", yardSale.getObjectId());
                getContext().startActivity(intent);
                //TODO reflect the changes in the listview immediately
            }
        });

        viewHolder.btnDeleteYS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "deleting a YS", Toast.LENGTH_LONG).show();
                client.deleteSale(yardSale);
                //TODO remove the item from the listview
            }
        });
        return view;
    }


    private String constructTime(Date start, Date end) {
        DateFormat format = DateFormat.getDateTimeInstance();
        return "Start: " + format.format(start) + " End: " + format.format(end);
    }

}
