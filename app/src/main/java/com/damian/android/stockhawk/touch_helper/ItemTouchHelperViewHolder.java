/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.touch_helper;

/**
 * credit to Paul Burke (ipaulpro)
 * Interface for enabling swiping to delete
 */
public interface ItemTouchHelperViewHolder {
    void onItemSelected();

    void onItemClear();
}
