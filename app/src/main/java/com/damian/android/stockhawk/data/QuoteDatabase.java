/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
    public static final int VERSION = 7;

    private QuoteDatabase() {
    }

    @Table(QuoteColumns.class)
    public static final String QUOTES = "quotes";
}
