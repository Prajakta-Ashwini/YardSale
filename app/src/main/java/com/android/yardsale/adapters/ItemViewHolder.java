package com.android.yardsale.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yardsale.R;

public class ItemViewHolder extends RecyclerView.ViewHolder{
    ImageView ivPic;
    TextView tvPrice;
    Button btEditItem;
    Button btDeleteItem;
    Context context;
    View v;

    public ItemViewHolder(View v,final Context context) {
        super(v);
        ivPic = (ImageView) v.findViewById(R.id.ivPic);
        tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        btEditItem = (Button) v.findViewById(R.id.btEditItem);
        btDeleteItem = (Button) v.findViewById(R.id.btDeleteItem);
        this.context = context;
        this.v = v;
        //v.setOnCreateContextMenuListener(this);
    }

    public void showMenu() {
       v.showContextMenu();
    }
}
