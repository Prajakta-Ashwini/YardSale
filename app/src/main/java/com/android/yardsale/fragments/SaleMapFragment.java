package com.android.yardsale.fragments;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

public class SaleMapFragment extends SupportMapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static YardSale yardSale;
    private static List<YardSale> yardSaleList;
    private static BitmapDescriptor defaultMarker ;
    private FloatingActionButton btFlip;
    static Context context;
    FrameLayout flMap;
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static SaleMapFragment newInstance(YardSale sale,Context c){

        SaleMapFragment fragmentDemo = new SaleMapFragment();
        yardSale = sale;
        //Bundle args = new Bundle();
        //args.putInt("sale_list", list);
        //fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public static SaleMapFragment newInstance(List<YardSale> saleList,Context c){

        SaleMapFragment fragmentDemo = new SaleMapFragment();
        yardSaleList = saleList;
        //Bundle args = new Bundle();
        //args.putInt("sale_list", list);
        //fragmentDemo.setArguments(args);
        context = c;
        return fragmentDemo;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
       View v;
//
        if(yardSaleList!=null ) {
            //btFlip = new FloatingActionButton(context);
            v = inflater.inflate(R.layout.fragment_map, parent, false);
            btFlip = (FloatingActionButton) v.findViewById(R.id.fab);
            flMap = (FrameLayout) v.findViewById(R.id.flMap);

            View map = super.onCreateView(inflater, parent, savedInstanceState);
            flMap.addView(map);

            btFlip.setImageDrawable((getResources().getDrawable(R.drawable.list_bulleted)));
            btFlip.setColorNormal(R.color.amber);
            btFlip.setColorPressed(R.color.amber);
            btFlip.setColorNormal(getResources().getColor(R.color.amber));
            btFlip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    SalesFragment frag = SalesFragment.newInstance();
                    transaction.replace(R.id.flContent, frag).commit();
                }
            });
        }else{
             v = super.onCreateView(inflater, parent, savedInstanceState);
        }
        initMap();
        return v;
    }

    private void initMap(){
        UiSettings settings = getMap().getUiSettings();

        getMap().setMyLocationEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

            if (yardSaleList!=null && yardSaleList.size() >0 ) {
                //List<YardSale> sales = ((FindStuffFragment) getParentFragment()).getYardSaleList();

                for (YardSale s : yardSaleList) {
                    addYardSale(s);
                }
            } else if(yardSale!=null){
                addYardSale(yardSale);
                settings.setAllGesturesEnabled(false);
                settings.setMyLocationButtonEnabled(false);
            }

    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            Log.d("Location Updates", "Google Play services is not available.");
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addYardSale(YardSale row){
        if(row.getLocation() == null)
            return;
        LatLng loc = new LatLng(row.getLocation().getLatitude(),row.getLocation().getLongitude());
        //getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
        Marker marker = getMap().addMarker(new MarkerOptions()
                    .position(loc)
                    .title(row.getTitle())
                    .icon(defaultMarker));

    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            CameraUpdate cameraUpdate;
            //Todo If loc for the selected sale is null then just display current loca?
            if(yardSale == null || yardSale.getTitle()==null || yardSale.getLocation() == null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, YardSaleApplication.MAP_ZOOM);
            } else {
                LatLng latLng = new LatLng(yardSale.getLocation().getLatitude(), yardSale.getLocation().getLongitude());
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, YardSaleApplication.MAP_ZOOM);
            }
            getMap().animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Toast.makeText(getActivity(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(getActivity(), "Long Press", Toast.LENGTH_LONG).show();
        // Custom code here...
     //   showAlertDialogForPoint(latLng);

    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
       // Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the location com.client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    public void addMarker(YardSale ys){
        yardSale = ys;
        addYardSale(ys);
    }

    public void addSaleToList(YardSale s){
        yardSaleList.add(s);
        addYardSale(s);
    }
}
