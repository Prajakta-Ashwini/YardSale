package com.android.yardsale.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalesFragment extends Fragment {

    private static final String TAG = "SalesFragment";

    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    public ThingsAdapter adapter;
    public  List<YardSale> yardSales = new ArrayList<>();
    private FloatingActionButton btFlip;

    public SalesFragment() {
        super();
    }

    public static SalesFragment newInstance() {
        return new SalesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yardSales = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_fragment, container, false);
        view.setTag(TAG);

        rvSales = (RecyclerView) view.findViewById(R.id.rvSales);
        // Setup layout manager for items
        mLayoutManager = new LinearLayoutManager(getActivity());
        // Control orientation of the items
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        // Attach layout manager
        rvSales.setLayoutManager(mLayoutManager);
        rvSales.setHasFixedSize(true);
        rvSales.setLayoutManager(mLayoutManager);
        rvSales.setItemAnimator(new DefaultItemAnimator());

        ParseQueryAdapter.QueryFactory<YardSale> factory = getYardSaleQueryFactory();
        adapter = new ThingsAdapter(getActivity(), factory, container);
        // Set CustomAdapter as the adapter for RecyclerView.
        rvSales.setAdapter(adapter);
        registerForContextMenu(rvSales);
        btFlip = (FloatingActionButton) view.findViewById(R.id.fab);
        btFlip.setImageDrawable((getResources().getDrawable(R.drawable.map)));
        btFlip.setColorNormal(R.color.accent_color);
        btFlip.setColorPressed(R.color.accent_color);
        //btFlip.setColorRipple(R.color.amber);
        btFlip.attachToRecyclerView(rvSales);
        btFlip.setColorNormal(getResources().getColor(R.color.accent_color));
        btFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                SaleMapFragment frag;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentByTag("map_frag") == null) {
                    List<YardSale> list = new ArrayList<YardSale>();
                    frag = SaleMapFragment.newInstance(list, getActivity());
                } else {
                    frag = (SaleMapFragment) fragmentManager.findFragmentByTag("map_frag");
                }

                //transaction.setCustomAnimations(R.animator.abc_slide_in_bottom, R.anim.abc_slide_out_top);
//                Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.flip_in);
//                anim.setDuration(1000);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                }
                transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
//                ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
//                        R.animator.fragment_slide_left_exit,
//                        R.animator.fragment_slide_right_enter,
//                        R.animator.fragment_slide_right_exit);
                //transaction.setCustomAnimations(R.anim.flip_in, R.anim.flip_in);
//                frag.setEnterTransition(new com.daimajia.androidanimations.library.flippers.FlipInXAnimator());//R.anim.new Slide(Gravity.RIGHT));
//                    frag.setExitTransition(new com.daimajia.androidanimations.library.flippers.FlipOutYAnimator());
//                ViewFlipper viewFlipper = new ViewFlipper(getActivity());
//                AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                transaction.replace(R.id.flContent, frag).addToBackStack("map_frag").commit();
                YardSaleApplication client = new YardSaleApplication();
                client.addYardSalesToMap(getActivity(), frag, false);

            }
        });

        return view;
    }

    private ParseQueryAdapter.QueryFactory<YardSale> getYardSaleQueryFactory() {
        return new ParseQueryAdapter.QueryFactory<YardSale>() {
            public ParseQuery<YardSale> create() {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereNotEqualTo("seller", ParseUser.getCurrentUser());
                Calendar c = Calendar.getInstance();
                query.whereGreaterThanOrEqualTo("end_time", c.getTime());
                query.orderByAscending("start_time");
                try {
                    query.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return query;
            }
        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //MyView.RecyclerContextMenuInfo info = (MyView.RecyclerContextMenuInfo) item.getMenuInfo();
        int position = -1;
        try {
            position = adapter.getPosition() ;

        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.share_sale:
                Toast.makeText(getActivity(), "share sale!", Toast.LENGTH_SHORT).show();
                adapter.fireShare(getActivity(), position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // inflate menu here
        menu.setHeaderTitle("Select action:");
        //since we only see sales not created by us here so only share
        menu.add(Menu.NONE, R.id.share_sale, Menu.NONE, "Share Sale");
    }
}
