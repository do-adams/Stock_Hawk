/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.rest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damian.android.stockhawk.R;
import com.damian.android.stockhawk.data.QuoteColumns;
import com.damian.android.stockhawk.data.QuoteProvider;
import com.damian.android.stockhawk.touch_helper.ItemTouchHelperAdapter;
import com.damian.android.stockhawk.touch_helper.ItemTouchHelperViewHolder;

/**
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    private static Typeface mRobotoLight;
    private boolean mIsPercent;

    public QuoteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mRobotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_quote, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        String symbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
        String bidPrice = cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE));

        viewHolder.symbol.setText(symbol);
        viewHolder.bidPrice.setText(bidPrice);

        int sdk = Build.VERSION.SDK_INT;
        if (cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            } else {
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }
        } else {
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else {
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }

        String itemContentDescription = mContext.getString(R.string.cnt_desc_list_item_stock) + " "
                + symbol + " " + mContext.getString(R.string.cnt_desc_list_item_bid_price) + " "
                + bidPrice + " " + mContext.getString(R.string.cnt_desc_list_item_change) + " ";
        String change;
        if (Utils.mShowPercent) {
            change = cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            viewHolder.change.setText(change);
        } else {
            change = cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE));
            viewHolder.change.setText(change);
        }
        itemContentDescription += change;
        viewHolder.getItemView().setContentDescription(itemContentDescription);
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        public final TextView symbol;
        public final TextView bidPrice;
        public final TextView change;

        public ViewHolder(View itemView) {
            super(itemView);
            symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
            symbol.setTypeface(mRobotoLight);
            bidPrice = (TextView) itemView.findViewById(R.id.bid_price);
            change = (TextView) itemView.findViewById(R.id.change);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
        }

        /**
         * Gets the item view from
         * the viewholder.
         */
        public View getItemView() {
            return itemView;
        }
    }
}
