/*
 * Copyright (C) 2016 Damián Adams
 */

package com.damian.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.damian.android.stockhawk.R;
import com.damian.android.stockhawk.data.QuoteColumns;
import com.damian.android.stockhawk.data.QuoteProvider;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity in charge of displaying Line Chart stock data to the user.
 */
public class LineGraphActivity extends AppCompatActivity {

    private static final String LOG_TAG = LineGraphActivity.class.getSimpleName();
    private static final int CHART_AXIS_OFFSET = 5;

    private TextView mTitleLineChart;
    private LineChartView mLineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        mTitleLineChart = (TextView) findViewById(R.id.title_linechart);

        int chartColor = ContextCompat.getColor(this, android.R.color.white);
        int lineColor = ContextCompat.getColor(this, R.color.material_blue_500);

        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        mLineChartView.setLabelsColor(chartColor);

        // Create data set for the Line Chart.
        LineSet dataSet = new LineSet();
        dataSet.setColor(lineColor);
        dataSet.setDotsColor(lineColor);
        dataSet.setDotsStrokeColor(lineColor);

        Intent intent = getIntent();

        // We can query the cursor in the UI thread since this activity has one UI purpose only:
        // presenting the cursor data to the user.
        if (intent != null && intent.hasExtra(MyStocksActivity.STOCK_NAME_KEY)) {
            String stockName = intent.getExtras().getString(MyStocksActivity.STOCK_NAME_KEY);

            String chartTitle = stockName + " " + getString(R.string.chart_title_substr);
            String chartTitleDescription = getString(R.string.cnt_desc_chart_title) + " " + chartTitle;
            mTitleLineChart.setText(chartTitle);
            mTitleLineChart.setContentDescription(chartTitleDescription);

            Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    null, null,
                    null, null);

            if (cursor != null) {
                // Get proper db row for the stock we want.
                cursor.moveToLast();
                while (!cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)).equals(stockName)) {
                    cursor.moveToPrevious();
                }

                Log.d(LOG_TAG, "Name of Stock being displayed: "
                        + cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));

                // Get stock price values from the db.
                String todaysBidPrice = cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                String fiftyDaysAverage = cursor.getString(cursor.getColumnIndex(QuoteColumns.FIFTY_DAYS_PRICE_AVERAGE));
                String twoHundredDaysAverage = cursor.getString(cursor.getColumnIndex(QuoteColumns.TWO_HUNDRED_DAYS_PRICE_AVERAGE));
                cursor.close();

                // Add values to data set.
                dataSet.addPoint(getString(R.string.chart_200days_label), Float.parseFloat(twoHundredDaysAverage));
                dataSet.addPoint(getString(R.string.chart_50days_label), Float.parseFloat(fiftyDaysAverage));
                dataSet.addPoint(getString(R.string.chart_today_label), Float.parseFloat(todaysBidPrice));

                // Set the appropriate Y axis for the line chart (with a small offset).
                List<Integer> stockPrices = new ArrayList<>();
                stockPrices.add((int) Double.parseDouble(twoHundredDaysAverage));
                stockPrices.add((int) Double.parseDouble(fiftyDaysAverage));
                stockPrices.add((int) Double.parseDouble(todaysBidPrice));
                int max;
                int min = max = stockPrices.get(0);
                for (Integer n : stockPrices) {
                    if (n < min)
                        min = n;
                    if (n > max)
                        max = n;
                }
                mLineChartView.setAxisBorderValues(min - CHART_AXIS_OFFSET, max + CHART_AXIS_OFFSET);

                // Set the content description.
                mLineChartView.setContentDescription(getString(R.string.cnt_desc_chart_overview) + " "
                        + getString(R.string.cnt_desc_chart_200days_label) + " "
                        + twoHundredDaysAverage + ". " + getString(R.string.cnt_desc_chart_50days_label)
                        + " " + fiftyDaysAverage + ". " + getString(R.string.cnt_desc_chart_today_label) + " "
                        + todaysBidPrice);

                // Prep and display the Line Chart.
                mLineChartView.addData(dataSet);
                mLineChartView.show();
            }
        }
    }
}
