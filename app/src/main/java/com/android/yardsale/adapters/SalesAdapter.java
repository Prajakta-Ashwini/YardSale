package com.android.yardsale.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditYardSaleActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SaleViewHolder> {

    private List<YardSale> salesList;
    private Context context;
    private YardSaleApplication client;

    public SalesAdapter(Context context,List<YardSale> contactList) {
        this.salesList = contactList;
        this.context = context;
        client = new YardSaleApplication();
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    @Override
    public void onBindViewHolder(SaleViewHolder saleViewHolder, final int position) {
        final YardSale sale = salesList.get(position);
        saleViewHolder.tvTitle.setText(sale.getTitle());
        try {
            saleViewHolder.tvAddedBy.setText(sale.getSeller().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(sale.getCoverPic() != null)
            Picasso.with(context).load(sale.getCoverPic().getUrl()).placeholder(R.drawable.placeholder).into(saleViewHolder.ivCoverPic);
        else
            Picasso.with(context).load(R.drawable.placeholder).into(saleViewHolder.ivCoverPic);

        saleViewHolder.btDeleteSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete sale!", Toast.LENGTH_SHORT).show();
                client.deleteSale(sale);
                salesList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, salesList.size());
            }
        });

        saleViewHolder.btEditSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "edit sale!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EditYardSaleActivity.class);
                intent.putExtra("edit_yard_sale_id", sale.getObjectId());
                context.startActivity(intent);
            }
        });

        saleViewHolder.ivCoverPic.setOnClickListener(new View.OnClickListener() {
            YardSale s = salesList.get(position);
            @Override
            public void onClick(View v) {
                client.getItemsForYardSale(s);
            }
        });


    }

    @Override
    public SaleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_yardsale, viewGroup, false);

        return new SaleViewHolder(itemView);
    }
}
