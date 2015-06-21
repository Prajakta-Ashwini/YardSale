package com.android.yardsale.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderRecyclerViewAdapterV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADERS_START = Integer.MIN_VALUE;
    private static final int FOOTERS_START = Integer.MIN_VALUE + 10;
    private static final int ITEMS_START = Integer.MIN_VALUE + 20;
    private static final int ADAPTER_MAX_TYPES = 100;

    private RecyclerView.Adapter mWrappedAdapter;
    private List<View> mHeaderViews, mFooterViews;
    private Map<Class, Integer> mItemTypesOffset;

    /**
     * Construct a new item_list_header view recycler adapter
     * @param adapter The underlying adapter to wrap
     */
    public HeaderRecyclerViewAdapterV2(RecyclerView.Adapter adapter) {
        mHeaderViews = new ArrayList<View>();
        mFooterViews = new ArrayList<View>();
        mItemTypesOffset = new HashMap<Class, Integer>();
        setWrappedAdapter(adapter);
    }

    /**
     * Replaces the underlying adapter, notifying RecyclerView of changes
     * @param adapter The new adapter to wrap
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if(mWrappedAdapter != null && mWrappedAdapter.getItemCount() > 0) {
            notifyItemRangeRemoved(getHeaderCount(), mWrappedAdapter.getItemCount());
        }
        setWrappedAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), mWrappedAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        int hCount = getHeaderCount();
        if (position < hCount) return HEADERS_START + position;
        else {
            int itemCount = mWrappedAdapter.getItemCount();
            if (position < hCount + itemCount) {
                return getAdapterTypeOffset() + mWrappedAdapter.getItemViewType(position - hCount);
            }
            else return FOOTERS_START  + position - hCount - itemCount;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount())
            return new StaticViewHolder(mHeaderViews.get(viewType - HEADERS_START));
        else if (viewType < FOOTERS_START + getFooterCount())
            return new StaticViewHolder(mFooterViews.get(viewType - FOOTERS_START));
        else {
            return mWrappedAdapter.onCreateViewHolder(viewGroup, viewType - getAdapterTypeOffset());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int hCount = getHeaderCount();
        if (position >= hCount && position < hCount + mWrappedAdapter.getItemCount())
            mWrappedAdapter.onBindViewHolder(viewHolder, position - hCount);
//        if(position == hCount) {
//            HeaderViewHolder v = (HeaderViewHolder) viewHolder;
//            v.tvDescription.setText("Prajkata");
////        viewHolder.tvPrice.setText(String.valueOf(item.getPrice()));
//        }
    }

    /**
     * Add a static view to appear at the start of the RecyclerView. Headers are displayed in the
     * order they were added.
     * @param view The item_list_header view to add
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    /**
     * Add a static view to appear at the end of the RecyclerView. Footers are displayed in the
     * order they were added.
     * @param view The footer view to add
     */
    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    /**
     * @return The item count in the underlying adapter
     */
    public int getWrappedItemCount() {
        return mWrappedAdapter.getItemCount();
    }

    /**
     * @return The number of item_list_header views added
     */
    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    /**
     * @return The number of footer views added
     */
    public int getFooterCount() {
        return mFooterViews.size();
    }

    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null) mWrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
        mWrappedAdapter = adapter;
        Class adapterClass = mWrappedAdapter.getClass();
        if(!mItemTypesOffset.containsKey(adapterClass)) putAdapterTypeOffset(adapterClass);
        mWrappedAdapter.registerAdapterDataObserver(mDataObserver);
    }

    private void putAdapterTypeOffset(Class adapterClass) {
        mItemTypesOffset.put(adapterClass, ITEMS_START + mItemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }

    private int getAdapterTypeOffset() {
        return mItemTypesOffset.get(mWrappedAdapter.getClass());
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int hCount = getHeaderCount();
            // TODO: No notifyItemRangeMoved method?
            notifyItemRangeChanged(fromPosition + hCount, toPosition + hCount + itemCount);
        }
    };

    private static class StaticViewHolder extends RecyclerView.ViewHolder {

        public StaticViewHolder(View itemView) {
            super(itemView);
        }
    }
}