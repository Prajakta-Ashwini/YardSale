package com.android.yardsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.models.Item;

import java.util.List;

/**
 * Created by Prajakta on 6/6/2015.
 */
public class ItemsArrayAdapter extends ArrayAdapter<Item> {
    private static class ViewHolderSale {

        TextView tvDescription;

    }

    public ItemsArrayAdapter(Context context, List<Item> itemList) {
        super(context, android.R.layout.simple_list_item_1, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        //find or inflate the template
//        if(convertView == null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
//        }
        ViewHolderSale viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolderSale();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_sale_item, parent, false);
//            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
//            viewHolder.tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
//            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSale) convertView.getTag();
        }
        //find the subviews to fill data in the template

        //populate data into subview

        viewHolder.tvDescription.setText("@" + item.getDescription());
        //Picasso.with(getContext()).load(user.getProfileImageUrl()).into(viewHolder.ivProfileImage);
        //return view to be inserted in the list
        return convertView;
    }
}
