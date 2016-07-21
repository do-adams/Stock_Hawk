/*
 * Copyright (C) 2016 Damián Adams
 */

package com.damian.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.damian.android.stockhawk.R;
import com.damian.android.stockhawk.ui.LineGraphActivity;

/**
 * An implementation of the AppWidgetProvider class for the Stock Hawk widget.
 */
public class StocksWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();
    public static final String UPDATE_STOCK_WIDGET_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_STOCK_WIDGET_ACTION)) {
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new
                    ComponentName(context, StocksWidgetProvider.class));
            Log.d(LOG_TAG, "Received: " + intent.getAction());
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, ListViewWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stocks_widget);
            remoteViews.setRemoteAdapter(R.id.list_view, intent);

            // Sets the pending intent for launching the LineGraphActivity when
            // an item (the stock name) of the ListView is tapped by the user.
            // Must be set separately in this class and the ListViewAdapter class.
            Intent clickIntent = new Intent(context, LineGraphActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context,
                    0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.list_view, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
