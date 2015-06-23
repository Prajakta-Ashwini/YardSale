package com.android.yardsale.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.android.yardsale.activities.AddYardSaleActivity;
import com.android.yardsale.adapters.ThingsAdapter;
import com.android.yardsale.helpers.CircularReveal;
import com.android.yardsale.models.YardSale;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ListingsFragment extends Fragment {

    private static final String TAG = "ListingsFragment";

    private RecyclerView rvSales;
    private LinearLayoutManager mLayoutManager;
    private ThingsAdapter adapter;
    private FloatingActionButton btCreateSale;

    public ListingsFragment() {
        super();
    }

    public static ListingsFragment newInstance() {
        return new ListingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.parseAdapter.loadObjects();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.parseAdapter.loadObjects();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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

        btCreateSale = (FloatingActionButton) view.findViewById(R.id.fab);
        btCreateSale.setImageDrawable((getResources().getDrawable(R.drawable.ic_action_content_add)));
//        btFlip.setColorNormal(R.color.amber);
//        btFlip.setColorPressed(R.color.amber);
//
//        btFlip.setColorRipple(R.color.amber);
        btCreateSale.attachToRecyclerView(rvSales);
        btCreateSale.setColorNormal(getResources().getColor(R.color.accent_color));
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CircularReveal.enterReveal(btCreateSale);
            }
        });

        btCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "creating!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddYardSaleActivity.class);
                startActivityForResult(intent, 20);
            }
        });

        return view;
    }


    private ParseQueryAdapter.QueryFactory<YardSale> getYardSaleQueryFactory() {
        return new ParseQueryAdapter.QueryFactory<YardSale>() {
            public ParseQuery<YardSale> create() {
                ParseQuery<YardSale> query = YardSale.getQuery();
                query.whereEqualTo("seller", ParseUser.getCurrentUser());

                try {
                    query.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return query;
            }
        };
    }

    public void editYardSale(YardSale sale) {
        adapter.editYardSale(sale);
    }

    public void addYardSale(YardSale yardSale) {
        adapter.addYardSale(yardSale);
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
            case R.id.delete_sale:
                Toast.makeText(getActivity(), "delete sale!", Toast.LENGTH_SHORT).show();
                adapter.fireDelete(getActivity(), position);
                break;
            case R.id.edit_sale:
                Toast.makeText(getActivity(), "edit sale!", Toast.LENGTH_SHORT).show();
                adapter.fireEdit(getActivity(), position);
                break;

            case R.id.share_sale:
                Toast.makeText(getActivity(), "share sale!", Toast.LENGTH_SHORT).show();
                adapter.fireShare(getActivity(), position);
                break;
        }
        return super.onContextItemSelected(item);
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mRecyclerView = view.findViewById(R.id.recyclerview);
//        registerForContextMenu(mRecyclerView);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // inflate menu here
        menu.setHeaderTitle("Select action:");
        menu.add(Menu.NONE, R.id.edit_sale, Menu.NONE, "Edit Sale");
        menu.add(Menu.NONE, R.id.delete_sale, Menu.NONE, "Delete Sale");
        menu.add(Menu.NONE, R.id.share_sale, Menu.NONE, "Share Sale");
    }

}
