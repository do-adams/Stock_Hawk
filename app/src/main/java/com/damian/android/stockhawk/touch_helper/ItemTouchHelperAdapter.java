/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.touch_helper;

/**
 * credit to Paul Burke (ipaulpro)
 * Interface to enable swipe to delete
 */
public interface ItemTouchHelperAdapter {

    void onItemDismiss(int position);
}
