package com.android.yardsale.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditYardSaleActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SaleViewHolder> {

    private List<YardSale> salesList;
    private Context context;
    private YardSaleApplication client;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    HashMap<SaleViewHolder, AnimatorSet> likeAnimations = new HashMap<>();

    public SalesAdapter(Context context, List<YardSale> contactList) {
        this.salesList = contactList;
        this.context = context;
        client = new YardSaleApplication();
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    @Override
    public void onBindViewHolder(final SaleViewHolder saleViewHolder, final int position) {
        final YardSale sale = salesList.get(position);
        saleViewHolder.tvTitle.setText(sale.getTitle());
        try {
            String user = sale.getSeller().fetchIfNeeded().getUsername();
            saleViewHolder.tvAddedBy.setText("Added by " + user);
            if (sale.getSeller() == ParseUser.getCurrentUser()) {
                saleViewHolder.btDeleteSale.setVisibility(View.VISIBLE);
                saleViewHolder.btEditSale.setVisibility(View.VISIBLE);
            } else {
                saleViewHolder.btDeleteSale.setVisibility(View.INVISIBLE);
                saleViewHolder.btEditSale.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (sale.getCoverPic() != null)
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

        //TODO remove the edit and delete button

        saleViewHolder.btEditSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "edit sale!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EditYardSaleActivity.class);
                intent.putExtra("edit_yard_sale_id", sale.getObjectId());
                ((Activity) context).startActivityForResult(intent, 20);
            }
        });

        saleViewHolder.btShareSale.setOnClickListener(new View.OnClickListener() {
            YardSale s = salesList.get(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share sale!", Toast.LENGTH_SHORT).show();
                client.shareSale(context, s);
            }
        });

        saleViewHolder.ivCoverPic.setOnClickListener(new View.OnClickListener() {
            YardSale s = salesList.get(position);

            @Override
            public void onClick(View v) {
                client.getItemsForYardSale(context, s, saleViewHolder.ivCoverPic);
            }
        });

        client.setLikeForSale(sale, saleViewHolder.btLike, false);
        saleViewHolder.btLike.setOnClickListener(new View.OnClickListener() {
            YardSale s = salesList.get(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "btn_like sale!", Toast.LENGTH_SHORT).show();
                updateHeartButton(s, saleViewHolder, true);
                client.setLikeForSale(s, saleViewHolder.btLike, true);
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

    private void updateHeartButton(final YardSale sale, final SaleViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
//                bounceAnimY.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        if(sale.li)
//                        holder.btLike.setImageResource(R.drawable.ic_heart_red);
//                    }
//                });
                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //resetLikeAnimationState(holder);
                        likeAnimations.remove(holder);
                        //holder.vBgLike.setVisibility(View.GONE);
                        //holder.ivLike.setVisibility(View.GONE);
                    }
                });

                animatorSet.start();
            }
        }else {
            if (likeAnimations.containsKey(holder.getPosition())) {
                holder.btLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }

    }
}
