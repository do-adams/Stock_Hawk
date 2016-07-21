/*
 * Copyright (C) 2016 Damián Adams
 */

package com.damian.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * An implementation of RemoteViewsService.
 * Initializes the ListViewWidgetAdapter for the Stock Hawk Widget.
 */
public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewWidgetAdapter(this.getApplicationContext(), intent);
    }
}
