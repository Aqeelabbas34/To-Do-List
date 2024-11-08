package com.aqeel.to_do_list;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Person_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Person_fragment extends Fragment {

   TextView pendingCount,completeCount;
   BarChart barChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        pendingCount= view.findViewById(R.id.pendingTaskCount);
        completeCount=view.findViewById(R.id.completeTaskCount);
        barChart= view.findViewById(R.id.barChart);

        Calendar calendar= Calendar.getInstance();
        int todayIndex= calendar.get(Calendar.DAY_OF_WEEK)-1;
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i=0;i<7;i++){
            int taskCount= TaskCounter.getInstance().getCompletedTaskCountForDay(i);
            entries.add(new BarEntry(i,taskCount));
        }
        BarDataSet barDataSet= new BarDataSet(entries,"Daily Progress");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(10f);

        barDataSet.setBarShadowColor(ColorTemplate.getHoloBlue());

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
            }
        });
        barChart.getBarData().getDataSetByIndex(0).setHighlightEnabled(true);
        barChart.highlightValue(todayIndex,0);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getXAxis().setDrawLabels(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCount();
    }

    void updateCount()
    {
        int pending= TaskCounter.getInstance().getPendingTaskCount();
        int complete= TaskCounter.getInstance().getCompletedTaskCount();
        pendingCount.setText(String.valueOf(pending));
        completeCount.setText(String.valueOf(complete));
    }
}