package com.damian.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.damian.android.stockhawk.R;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;

public class LineGraphActivity extends AppCompatActivity {

    private LineChartView mLineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        int chartColor = ContextCompat.getColor(this, android.R.color.white);

        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        mLineChartView.setLabelsColor(chartColor);

        LineSet dataSet = new LineSet();
        dataSet.setColor(chartColor);
        dataSet.setDotsColor(chartColor);
        dataSet.setDotsStrokeColor(chartColor);

        dataSet.addPoint("Value 1", 1);
        dataSet.addPoint("Value 2", 2);
        dataSet.addPoint("Value 3", 3);

        mLineChartView.addData(dataSet);
        mLineChartView.show();
    }
}
