package com.android.yardsale.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditYardSaleActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ThingsAdapter extends RecyclerView.Adapter<SaleViewHolder> {

    private ParseQueryAdapter<YardSale> parseAdapter;
    private ViewGroup parseParent;
    private ThingsAdapter thingsAdapter = this;
    private YardSaleApplication client;

    public ThingsAdapter(Context context, ParseQueryAdapter.QueryFactory<YardSale> queryFactory, ViewGroup parentIn) {

        parseParent = parentIn;
        client = new YardSaleApplication();
        parseAdapter = new ParseQueryAdapter<YardSale>(context, queryFactory) {

            @Override
            public View getItemView(YardSale sale, View v, ViewGroup parent) {
                if (v == null) {
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yardsale, parent, false);
                }
                super.getItemView(sale, v, parent);

                final YardSale yardSale = sale;
                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                TextView tvAddedBy = (TextView) v.findViewById(R.id.tvAddedBy);
                Button btDeleteSale = (Button) v.findViewById(R.id.btDeleteSale);
                Button btEditSale = (Button) v.findViewById(R.id.btEditSale);
                Button btShareSale = (Button) v.findViewById(R.id.btShareSale);
                final ImageButton btLike = (ImageButton) v.findViewById(R.id.btLike);
                ImageView ivCoverPic = (ImageView) v.findViewById(R.id.ivCoverPic);


                tvTitle.setText(sale.getTitle());
                try {
                    String user = sale.getSeller().fetchIfNeeded().getUsername();

                    tvAddedBy.setText("Added by " + user);
                    if (sale.getSeller() == ParseUser.getCurrentUser()) {
                        btDeleteSale.setVisibility(View.VISIBLE);
                        btEditSale.setVisibility(View.VISIBLE);
                    } else {
                        btDeleteSale.setVisibility(View.INVISIBLE);
                        btEditSale.setVisibility(View.INVISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (sale.getCoverPic() != null)
                    Picasso.with(getContext()).load(sale.getCoverPic().getUrl()).placeholder(R.drawable.placeholder).into(ivCoverPic);
                else
                    Picasso.with(getContext()).load(R.drawable.placeholder).into(ivCoverPic);


                btDeleteSale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "delete sale!", Toast.LENGTH_SHORT).show();
                        client.deleteSale(yardSale);
                        thingsAdapter.parseAdapter.loadObjects();
//                        salesList.remove(position);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, salesList.size());
                    }
                });

                //TODO remove the edit and delete button

                btEditSale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "edit sale!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), EditYardSaleActivity.class);
                        intent.putExtra("edit_yard_sale_id", yardSale.getObjectId());
                        ((Activity) getContext()).startActivityForResult(intent, 20);
                        thingsAdapter.parseAdapter.loadObjects();
                    }
                });

                btShareSale.setOnClickListener(new View.OnClickListener() {
                    // YardSale s = salesList.get(position);

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "share sale!", Toast.LENGTH_SHORT).show();
                        client.shareSale(getContext(), yardSale);
                        thingsAdapter.parseAdapter.loadObjects();
                        thingsAdapter.notifyDataSetChanged();
                    }
                });

                ivCoverPic.setOnClickListener(new View.OnClickListener() {
                    // YardSale s = salesList.get(position);

                    @Override
                    public void onClick(View v) {
                        //client.getItemsForYardSale(getContext(), s, ivCoverPic);
                    }
                });

                client.setLikeForSale(yardSale, btLike, false);
                btLike.setOnClickListener(new View.OnClickListener() {
                    //   YardSale s = salesList.get(position);

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "btn_like sale!", Toast.LENGTH_SHORT).show();
                        client.setLikeForSale(yardSale, btLike, true);
                        thingsAdapter.notifyDataSetChanged();
                    }
                });
                return v;
            }
        };
        parseAdapter.addOnQueryLoadListener(new OnQueryLoadListener());
        parseAdapter.loadObjects();
    }

    @Override
    public SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yardsale, parent, false);
        SaleViewHolder vh = new SaleViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SaleViewHolder holder, int position) {
        parseAdapter.getView(position, holder.itemView, parseParent);
    }

    @Override
    public int getItemCount() {
        return parseAdapter.getCount();
    }

    public class OnQueryLoadListener implements ParseQueryAdapter.OnQueryLoadListener<YardSale> {

        public void onLoading() {

        }

        public void onLoaded(List<YardSale> objects, Exception e) {
            thingsAdapter.notifyDataSetChanged();
        }
    }
}
