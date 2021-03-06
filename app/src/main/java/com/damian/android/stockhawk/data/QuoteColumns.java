/*
 * Copyright (C) 2016 Udacity, Inc. and Damián Adams
 */

package com.damian.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public class QuoteColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SYMBOL = "symbol";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String PERCENT_CHANGE = "percent_change";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String CHANGE = "change";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String BIDPRICE = "bid_price";

    @DataType(DataType.Type.TEXT)
    public static final String CREATED = "created";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISUP = "is_up";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISCURRENT = "is_current";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String FIFTY_DAYS_PRICE_AVERAGE = "fifty_days_price_average";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String TWO_HUNDRED_DAYS_PRICE_AVERAGE = "two_hundred_days_price_average";

}
