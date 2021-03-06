/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.damian.android.stockhawk.ui.MyStocksActivity;
import com.google.android.gms.gcm.TaskParams;


public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");

        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();

        if (intent.getStringExtra(MyStocksActivity.STOCK_SERVICE_TAG_KEY)
                .equals(MyStocksActivity.STOCK_SERVICE_ADD_VALUE)) {

            args.putString(MyStocksActivity.STOCK_SERVICE_SYMBOL_KEY,
                    intent.getStringExtra(MyStocksActivity.STOCK_SERVICE_SYMBOL_KEY));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(
                intent.getStringExtra(MyStocksActivity.STOCK_SERVICE_TAG_KEY), args));
    }
}
