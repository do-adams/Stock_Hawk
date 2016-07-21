/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.damian.android.stockhawk.R;
import com.damian.android.stockhawk.data.QuoteColumns;
import com.damian.android.stockhawk.data.QuoteProvider;
import com.damian.android.stockhawk.rest.Utils;
import com.damian.android.stockhawk.ui.MyStocksActivity;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService {
    private String LOG_TAG = StockTaskService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;

    // Flag in case of rare malformed API server response.
    public static boolean isBadResponse;

    public static void setIsBadResponse(boolean value) {
        isBadResponse = value;
    }

    public StockTaskService() {
    }

    public StockTaskService(Context context) {
        mContext = context;
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams params) {
        Cursor initQueryCursor;
        if (mContext == null) {
            mContext = this;
        }
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
                    + "in (", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (params.getTag().equals(MyStocksActivity.STOCK_SERVICE_INIT_VALUE)
                || params.getTag().equals(MyStocksActivity.PERIODIC_TASK_TAG)) {
            isUpdate = true;
            initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                    null, null);
            if (initQueryCursor.getCount() == 0 || initQueryCursor == null) {
                // Init task. Populates DB with quotes for the symbols seen below
                try {
                    urlStringBuilder.append(
                            URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (initQueryCursor != null) {
                DatabaseUtils.dumpCursor(initQueryCursor);
                initQueryCursor.moveToFirst();
                for (int i = 0; i < initQueryCursor.getCount(); i++) {
                    mStoredSymbols.append("\"" +
                            initQueryCursor.getString(initQueryCursor.getColumnIndex(QuoteColumns.SYMBOL)) + "\",");
                    initQueryCursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
                try {
                    urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (params.getTag().equals(MyStocksActivity.STOCK_SERVICE_ADD_VALUE)) {
            isUpdate = false;
            // get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString(MyStocksActivity.STOCK_SERVICE_SYMBOL_KEY);
            try {
                urlStringBuilder.append(URLEncoder.encode("\"" + stockInput + "\")", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        // finalize the URL for the API query.
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=");

        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;

        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();
            Log.d(LOG_TAG, urlString);
            try {
                getResponse = fetchData(urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;

                if (Utils.isValidStock(getResponse)) {
                    // If stock name(s) is/are valid, update and cache stock data.
                    try {
                        ContentValues contentValues = new ContentValues();
                        // update ISCURRENT to 0 (false) so new data is current
                        if (isUpdate) {
                            contentValues.put(QuoteColumns.ISCURRENT, 0);
                            mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                                    null, null);
                        }
                        mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                                Utils.quoteJsonToContentVals(getResponse));
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.e(LOG_TAG, "Error applying batch insert", e);
                    }
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    if (isBadResponse) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getString(R.string.toast_bad_response),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        isBadResponse = false; // reset the server response flag for new requests.
                    } else {
                        // Grabs the UI thread to post "invalid stock" msg.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getString(R.string.toast_stock_not_found),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
