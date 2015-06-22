package com.android.yardsale.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;

public class SaleViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitle;

    private ImageView ivPic1;
    private ImageView ivPic2;
    private ImageView ivPic3;
    private ImageView ivPic4;
    public ImageButton btLike;
    ImageView ivUserPic;
    TextView tvSeller;
    TextView tvPostedAt;
//    Button btEditSale;
//    Button btDeleteSale;
//    Button btShareSale;
    View view;

    public SaleViewHolder(View v) {
        super(v);
        //ivCoverPic = (ImageView) v.findViewById(R.id.ivCoverPic);
//        tvAddedBy = (TextView) v.findViewById(R.id.tvAddedBy);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
//        btEditSale = (Button) v.findViewById(R.id.btEditSale);
//        btDeleteSale = (Button) v.findViewById(R.id.btDeleteSale);
//        btShareSale = (Button) v.findViewById(R.id.btShareSale);
        btLike = (ImageButton) v.findViewById(R.id.btLike);
        ivPic1 = (ImageView) v.findViewById(R.id.ivPic1);
        ivPic2 = (ImageView) v.findViewById(R.id.ivPic2);
        ivPic3 = (ImageView) v.findViewById(R.id.ivPic3);
        ivPic4 = (ImageView) v.findViewById(R.id.ivPic4);
        ivUserPic = (ImageView) v.findViewById(R.id.ivUserPic);
        tvSeller = (TextView) v.findViewById(R.id.tvSeller);
        tvPostedAt = (TextView) v.findViewById(R.id.tvPostedAt);
        view = v;
    }

    public void showMenu() {
        view.showContextMenu();
    }
}