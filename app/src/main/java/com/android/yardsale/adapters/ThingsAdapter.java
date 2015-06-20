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

    public ParseQueryAdapter<YardSale> parseAdapter;
    private ViewGroup parseParent;
    public ThingsAdapter thingsAdapter = this;
    private YardSaleApplication client;
    private YardSale yardSale;
    private ImageView ivCoverPic;
    private ImageButton btLike;
    private Button btShareSale;
    private Button btDeleteSale;
    private Button btEditSale;
    private Context myContext;

    public ThingsAdapter(final Context context, ParseQueryAdapter.QueryFactory<YardSale> queryFactory, ViewGroup parentIn) {

        parseParent = parentIn;
        client = new YardSaleApplication();
        parseAdapter = new ParseQueryAdapter<YardSale>(context, queryFactory) {

            @Override
            public View getItemView(YardSale sale, View v, ViewGroup parent) {
                if (v == null) {
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yardsale, parent, false);
                }
                super.getItemView(sale, v, parent);

                myContext = context;
                yardSale = sale;
                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                TextView tvAddedBy = (TextView) v.findViewById(R.id.tvAddedBy);
                btDeleteSale = (Button) v.findViewById(R.id.btDeleteSale);
                btEditSale = (Button) v.findViewById(R.id.btEditSale);
                btShareSale = (Button) v.findViewById(R.id.btShareSale);
                btLike = (ImageButton) v.findViewById(R.id.btLike);
                ivCoverPic = (ImageView) v.findViewById(R.id.ivCoverPic);


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
    public void onBindViewHolder(SaleViewHolder holder, final int position) {
        parseAdapter.getView(position, holder.itemView, parseParent);
        client.setLikeForSale(yardSale, btLike, false);
        btLike.setOnClickListener(new View.OnClickListener() {
            YardSale s = parseAdapter.getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "btn_like sale!", Toast.LENGTH_SHORT).show();
                client.setLikeForSale(s, btLike, true);
                parseAdapter.loadObjects();
                thingsAdapter.notifyDataSetChanged();
            }
        });

        btShareSale.setOnClickListener(new View.OnClickListener() {
            YardSale s = parseAdapter.getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "share sale!", Toast.LENGTH_SHORT).show();
                client.shareSale(myContext, s);
                parseAdapter.loadObjects();
                thingsAdapter.notifyDataSetChanged();
            }
        });

        ivCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YardSale s = parseAdapter.getItem(position);
                client.getItemsForYardSale(myContext, s, ivCoverPic);
            }
        });

        btDeleteSale.setOnClickListener(new View.OnClickListener() {
            YardSale s = parseAdapter.getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "delete sale!", Toast.LENGTH_SHORT).show();
                client.deleteSale(s);
                thingsAdapter.parseAdapter.loadObjects();
                thingsAdapter.notifyDataSetChanged();
            }
        });

        btEditSale.setOnClickListener(new View.OnClickListener() {
            YardSale s = parseAdapter.getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "edit sale!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(myContext, EditYardSaleActivity.class);
                intent.putExtra("edit_yard_sale_id", s.getObjectId());
                editYardSale(s);
                ((Activity) myContext).startActivityForResult(intent, 20);
                parseAdapter.loadObjects();
                thingsAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return parseAdapter.getCount();
    }

    public class OnQueryLoadListener implements ParseQueryAdapter.OnQueryLoadListener<YardSale> {

        public void onLoading() {

        }

        public void onLoaded(List<YardSale> objects, Exception e) {

            if (parseAdapter.getCount() == 0) {
                //TODO may be over ride this to show a message no wishlist present
                //http://stackoverflow.com/questions/27414173/equivalent-of-listview-setemptyview-in-recyclerview
            }

            thingsAdapter.notifyDataSetChanged();
        }
    }

    public void editYardSale(YardSale row) {
        for (int i = 0; i < parseAdapter.getCount(); i++) {
            if (yardSale.getObjectId().equals(row.getObjectId())) {
                yardSale.setTitle(row.getTitle());
                yardSale.setDescription(row.getDescription());
                yardSale.setAddress(row.getAddress());
                if (row.getCoverPic() != null)
                    yardSale.setCoverPic(row.getCoverPic());
                //sale.setLocation(row.getLocation());
                yardSale.setStartTime(row.getStartTime());
                yardSale.setEndTime(row.getEndTime());
                parseAdapter.loadObjects();
                thingsAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void addYardSale(YardSale addedYardSale) {
        boolean added = false;
        if (parseAdapter.getCount() > 0) {
            added = true;
            parseAdapter.notifyDataSetChanged();
        }
        if (!added)
            parseAdapter.notifyDataSetChanged();

        parseAdapter.notifyDataSetChanged();
    }

    public void removeYardSale(YardSale row) {
        parseAdapter.notifyDataSetChanged();
    }
}
