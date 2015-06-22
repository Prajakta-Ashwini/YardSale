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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.EditYardSaleActivity;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ThingsAdapter extends RecyclerView.Adapter<SaleViewHolder> {

    public ParseQueryAdapter<YardSale> parseAdapter;
    private ViewGroup parseParent;
    public ThingsAdapter thingsAdapter = this;
    private YardSaleApplication client;
    private YardSale yardSale;
    private ImageView ivPic1;
    private ImageView ivPic2;
    private ImageView ivPic3;
    private ImageView ivPic4;
    private ImageButton btLike;
    private Button btShareSale;
    private Button btDeleteSale;
    private Button btEditSale;
    private Context myContext;
    ImageView[] arrIv = {ivPic1,ivPic2,ivPic3,ivPic4};
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    HashMap<SaleViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    long then;

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
                ivPic1 = (ImageView) v.findViewById(R.id.ivPic1);
                ivPic2 = (ImageView) v.findViewById(R.id.ivPic2);
                ivPic3 = (ImageView) v.findViewById(R.id.ivPic3);
                ivPic4 = (ImageView) v.findViewById(R.id.ivPic4);

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

                Picasso.with(getContext()).load(R.drawable.placeholder).into(ivPic1);
                Picasso.with(getContext()).load(R.drawable.placeholder).into(ivPic2);
                Picasso.with(getContext()).load(R.drawable.placeholder).into(ivPic3);
                Picasso.with(getContext()).load(R.drawable.placeholder).into(ivPic4);
                int i=0;
                ParseRelation<Item> rel = sale.getItemsRelation();
                try {
                    List<Item> itemList = rel.getQuery().find();
                    for(Item item:itemList) {
                        if(i==4)
                            break;
                        if (item.getPhoto() != null) {
                            if (i == 0) {
                                Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(ivPic1);
                            }
                            if (i == 1) {
                                Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(ivPic2);
                            }
                            if (i == 2) {
                                Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(ivPic3);
                            }
                            if (i == 3) {
                                Picasso.with(getContext()).load(item.getPhoto().getUrl()).placeholder(R.drawable.placeholder).into(ivPic4);
                            }
                            i++;
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
//



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
    public void onBindViewHolder(final SaleViewHolder holder, final int position) {
        parseAdapter.getView(position, holder.itemView, parseParent);
        client.setLikeForSale(yardSale, btLike, false);
        btLike.setOnClickListener(new View.OnClickListener() {
            YardSale s = parseAdapter.getItem(position);

            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "btn_like sale!", Toast.LENGTH_SHORT).show();
                updateHeartButton(s, holder, true);
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

//        ivCoverPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                YardSale s = parseAdapter.getItem(position);
//                client.getItemsForYardSale(myContext, s, ivCoverPic);
//            }
//        });

        for(int i=0;i<arrIv.length;i++) {
            final int bkI = i;
            if(i==0)
                arrIv[i] = ivPic1;
            if(i==1)
                arrIv[i] = ivPic2;
            if(i==2)
                arrIv[i] = ivPic3;
            if(i==3)
                arrIv[i] = ivPic4;
            arrIv[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        then = (long) System.currentTimeMillis();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if ((System.currentTimeMillis() - then) > 500) {
                /* Implement long click behavior here */
                            System.out.println("Long Click has happened!");
                            return false;
                        } else {
                /* Implement short click behavior here or do nothing */
                            System.out.println("Short Click has happened...");
                            YardSale s = parseAdapter.getItem(position);
                            client.getItemsForYardSale(myContext, s, arrIv[bkI]);
                            return true;
                        }
                    }
                    return true;
                }
            });
        }

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