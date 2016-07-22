/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.damian.android.stockhawk.data.QuoteColumns;
import com.damian.android.stockhawk.data.QuoteProvider;
import com.damian.android.stockhawk.service.StockTaskService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Utils {

    // JSON keys for the Yahoo Finance API request.
    private static final String QUERY_OBJECT = "query";
    private static final String COUNT_VALUE = "count";
    private static final String RESULTS_OBJECT = "results";
    private static final String QUOTE_ARRAY = "quote";
    private static final String ASK_VALUE = "Ask";
    private static final String CHANGE_VALUE = "Change";
    private static final String SYMBOL_VALUE = "symbol";
    private static final String BID_VALUE = "Bid";
    private static final String CHANGE_IN_PERCENT_VALUE = "ChangeinPercent";
    private static final String FIFTYDAY_MOVING_AVERAGE_VALUE = "FiftydayMovingAverage";
    private static final String TWO_HUNDREDDAY_MOVING_AVERAGE_VALUE = "TwoHundreddayMovingAverage";

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean mShowPercent = true;

    /**
     * Performs a check on the online server API response
     * to determine whether it contains valid stock data.
     * Returns true if valid or false if invalid.
     */
    public static boolean isValidResponse(String JSON) {
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;

        // Queries the "Ask" price key value to determine if the stock is valid.
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject(QUERY_OBJECT);
                int count = Integer.parseInt(jsonObject.getString(COUNT_VALUE));

                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject(RESULTS_OBJECT)
                            .getJSONObject(QUOTE_ARRAY);
                    if (jsonObject.getString(ASK_VALUE).equals("null")) {
                        return false;
                    }
                } else {
                    resultsArray = jsonObject.getJSONObject(RESULTS_OBJECT).getJSONArray(QUOTE_ARRAY);

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            if (jsonObject.getString(ASK_VALUE).equals("null")) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
            StockTaskService.setIsBadResponse(true);
            return false;
        }
        return true;
    }

    public static ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject(QUERY_OBJECT);
                int count = Integer.parseInt(jsonObject.getString(COUNT_VALUE));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject(RESULTS_OBJECT)
                            .getJSONObject(QUOTE_ARRAY);
                    batchOperations.add(buildBatchOperation(jsonObject));
                } else {
                    resultsArray = jsonObject.getJSONObject(RESULTS_OBJECT).getJSONArray(QUOTE_ARRAY);

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString(CHANGE_VALUE);
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(SYMBOL_VALUE));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString(BID_VALUE)));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString(CHANGE_IN_PERCENT_VALUE), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }
            builder.withValue(QuoteColumns.FIFTY_DAYS_PRICE_AVERAGE,
                    truncateBidPrice(jsonObject.getString(FIFTYDAY_MOVING_AVERAGE_VALUE)));
            builder.withValue(QuoteColumns.TWO_HUNDRED_DAYS_PRICE_AVERAGE,
                    truncateBidPrice(jsonObject.getString(TWO_HUNDREDDAY_MOVING_AVERAGE_VALUE)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }
}
