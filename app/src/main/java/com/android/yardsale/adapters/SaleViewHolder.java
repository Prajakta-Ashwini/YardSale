package com.android.yardsale.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;

public class SaleViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitle;
    TextView tvAddedBy;
    ImageView ivCoverPic;
    Button btEditSale;
    Button btDeleteSale;
    Button btShareSale;

    public SaleViewHolder(View v) {
        super(v);
        ivCoverPic = (ImageView) v.findViewById(R.id.ivCoverPic);
        tvAddedBy = (TextView) v.findViewById(R.id.tvAddedBy);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        btEditSale = (Button) v.findViewById(R.id.btEditSale);
        btDeleteSale = (Button) v.findViewById(R.id.btDeleteSale);
        btShareSale = (Button) v.findViewById(R.id.btShareSale);
    }
}