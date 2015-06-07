package com.android.yardsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.yardsale.R;
import com.android.yardsale.models.YardSale;

import java.util.List;

public class SalesArrayAdapter  extends ArrayAdapter<YardSale> {
    private static class ViewHolderSale {

        TextView tvScreenName;

    }

    public SalesArrayAdapter(Context context, List<YardSale> yardSalesList) {
        super(context, android.R.layout.simple_list_item_1, yardSalesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YardSale sale = getItem(position);
        //find or inflate the template
//        if(convertView == null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
//        }
        ViewHolderSale viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolderSale();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_yardsale, parent, false);
//            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
//            viewHolder.tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
//            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSale) convertView.getTag();
        }
        //find the subviews to fill data in the template

        //populate data into subview

        viewHolder.tvScreenName.setText("@" + sale.getDescription());
        //Picasso.with(getContext()).load(user.getProfileImageUrl()).into(viewHolder.ivProfileImage);
        //return view to be inserted in the list
        return convertView;
    }
}
