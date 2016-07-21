package com.damian.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.damian.android.stockhawk.R;
import com.damian.android.stockhawk.data.QuoteColumns;
import com.damian.android.stockhawk.data.QuoteProvider;
import com.damian.android.stockhawk.rest.Utils;

public class ListViewWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = ListViewWidgetAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataIsValid;
    private int mRowIdColumn;

    public ListViewWidgetAdapter(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        mDataIsValid = mCursor != null;
        mRowIdColumn = mDataIsValid ? mCursor.getColumnIndex("_id") : -1;
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mDataIsValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Cursor must be moved manually since it is not moved automatically by getItemId method.
        mCursor.moveToPosition(position);
        RemoteViews listViewItem = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);

        String symbol = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));
        String bidPrice = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));

        listViewItem.setTextViewText(R.id.stock_symbol, symbol);
        listViewItem.setTextViewText(R.id.bid_price, bidPrice);

        String itemContentDescription = mContext.getString(R.string.cnt_desc_list_item_stock) + " "
                + symbol + " " + mContext.getString(R.string.cnt_desc_list_item_bid_price) + " "
                + bidPrice + " " + mContext.getString(R.string.cnt_desc_list_item_change) + " ";
        String change;
        if (Utils.showPercent) {
            change = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            listViewItem.setTextViewText(R.id.change, change);
        } else {
            change = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE));
            listViewItem.setTextViewText(R.id.change, change);
        }
        itemContentDescription += change;
        listViewItem.setContentDescription(R.layout.list_item_quote, itemContentDescription);

        return listViewItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mDataIsValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
