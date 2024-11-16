package com.aqeel.to_do_list.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.singelton.TaskCounter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Person_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Person_fragment extends Fragment  {

   TextView pendingCount,completeCount,userName;
   BarChart barChart;
   SharedPref sharedPref;
   FirebaseFirestore db ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        pendingCount= view.findViewById(R.id.pendingTaskCount);
        completeCount=view.findViewById(R.id.completeTaskCount);
        barChart= view.findViewById(R.id.barChart);
        userName= view.findViewById(R.id.userNameText);
        sharedPref = new SharedPref(requireActivity());
        db= FirebaseFirestore.getInstance();


      setChartData();
        ModelUser user = sharedPref.getData();
        String userId= user.getEmail();
      getTaskCount(userId);
        return view;
    }



    void updateCount()
    {
        int pending= TaskCounter.getInstance().getPendingTaskCount();
        int complete= TaskCounter.getInstance().getCompletedTaskCount();
        pendingCount.setText(String.valueOf(pending));
        completeCount.setText(String.valueOf(complete));
    }

    private void setChartData() {
        // Define explicit values for each day of the week (for example)
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Manually define the BarEntries (each entry takes an x-value (day) and a y-value (task count))
        entries.add(new BarEntry(0, 3)); // Sunday, 3 tasks
        entries.add(new BarEntry(1, 5)); // Monday, 5 tasks
        entries.add(new BarEntry(2, 2)); // Tuesday, 2 tasks
        entries.add(new BarEntry(3, 7)); // Wednesday, 7 tasks
        entries.add(new BarEntry(4, 4)); // Thursday, 4 tasks
        entries.add(new BarEntry(5, 6)); // Friday, 6 tasks
        entries.add(new BarEntry(6, 1)); // Saturday, 1 task

        // Create a BarDataSet from the entries
        BarDataSet barDataSet = new BarDataSet(entries, "Daily Progress");

        // Customize the appearance of the chart
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);  // Set color for the bars
        barDataSet.setValueTextSize(10);  // Set value text size on bars
        barDataSet.setBarShadowColor(ColorTemplate.getHoloBlue());  // Optional shadow color

        // Set the data to the BarChart
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Refresh the chart to show the updated data
        barChart.invalidate();

        // Set custom labels for X-axis (Days of the week)
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
            }
        });

        // Set other chart properties
        barChart.getDescription().setEnabled(false);  // Disable the description label
        barChart.getLegend().setEnabled(false);  // Disable the legend
        barChart.getXAxis().setDrawAxisLine(false);  // Optional, to hide the axis line
        barChart.getXAxis().setDrawLabels(true);  // Optional, to show X-axis labels
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLinesBehindData(false);
        barChart.getXAxis().setDrawLimitLinesBehindData(false);
    }

    private void getTaskCount(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for all pending tasks for the specific user
        db.collection("Task")
                .whereEqualTo("userID", userID) // Filter tasks by userID
                .whereEqualTo("status", "pending") // Optional: Filter by pending status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Count the number of documents retrieved (tasks)
                        int pending = task.getResult().size();
                        pendingCount.setText(String.valueOf(pending));
                    } else {
                        Log.w("YourFragment", "Error getting tasks", task.getException());
                    }
                });

        // Query for all completed tasks for the specific user
        db.collection("Task")
                .whereEqualTo("userID", userID) // Filter tasks by userID
                .whereEqualTo("status", "complete") // Filter by completed status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Count the number of documents retrieved (completed tasks)
                        int complete = task.getResult().size();
                        completeCount.setText(String.valueOf(complete));
                    } else {
                        Log.w("YourFragment", "Error getting tasks", task.getException());
                    }
                });
    }
}