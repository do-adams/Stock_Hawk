package com.damian.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewWidgetAdapter(this.getApplicationContext(), intent);
    }
}
