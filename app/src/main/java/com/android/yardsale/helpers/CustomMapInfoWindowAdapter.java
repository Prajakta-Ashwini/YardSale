package com.android.yardsale.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.yardsale.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public CustomMapInfoWindowAdapter(LayoutInflater i){
        mInflater = i;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.custom_info_window, null);
        // Populate fields
        TextView titleTextView = (TextView) v.findViewById(R.id.tv_info_window_title);
        String title = marker.getTitle();
        String[] arr = title.split("::::");
        titleTextView.setText(arr[0]);
        String address = marker.getSnippet();
        TextView description = (TextView) v.findViewById(R.id.tv_info_window_description);
        description.setText(address);
        // Return info window contents
        ImageButton btSaleDetailView = (ImageButton)v.findViewById(R.id.btSaleDetailView);
        btSaleDetailView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //show detail activity
                //client.getItemsForYardSale(myContext, , arrIv[bkI]);
            }
        });
        return v;
    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
