package com.android.yardsale.helpers;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.yardsale.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;
    YardSaleApplication client;
    Context context;
    String selectedMarkerMap ;
    FragmentManager fm;

    public CustomMapInfoWindowAdapter(FragmentManager fm, LayoutInflater i, Context context){
        mInflater = i;
        client = new YardSaleApplication();
        this.context = context;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.custom_info_window, null);
        // Populate fields
        TextView titleTextView = (TextView) v.findViewById(R.id.tv_info_window_title);
        String title = marker.getTitle();
        final String[] arr = title.split("::::");
        titleTextView.setText(arr[0]);
        String address = marker.getSnippet();
        TextView description = (TextView) v.findViewById(R.id.tv_info_window_description);
        description.setText(address);
        // Return info window contents
        ImageButton btSaleDetailView = (ImageButton)v.findViewById(R.id.btSaleDetailView);

        if(arr.length == 2) {
            selectedMarkerMap = arr[1];
            btSaleDetailView.setVisibility(View.VISIBLE);

        }else{
            btSaleDetailView.setVisibility(View.GONE);
        }
        return v;
    }

     // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    public void callDetailActivity() {
        if(selectedMarkerMap!=null)
            client.getItemsForYardSaleFromMap(fm, context,selectedMarkerMap);
    }
}
