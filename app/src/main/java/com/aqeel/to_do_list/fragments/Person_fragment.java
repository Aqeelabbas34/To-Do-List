package com.aqeel.to_do_list.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Person_fragment} factory method to
 * create an instance of this fragment.
 */
public class Person_fragment extends Fragment {

    TextView pendingCount, completeCount, userName;
    SharedPref sharedPref;
    FirebaseFirestore db;
    MyViewModel myViewModel;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        pendingCount = view.findViewById(R.id.pendingTaskCount);
        completeCount = view.findViewById(R.id.completeTaskCount);
        userName = view.findViewById(R.id.userNameText);
        barChart = view.findViewById(R.id.barChart);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        sharedPref = new SharedPref(requireActivity());
        db = FirebaseFirestore.getInstance();
        ModelUser user = sharedPref.getData();
         String mail= user.getEmail();
         if (mail!=null){
             userName.setText(mail);
         }
        String userId = user.getEmail();
//        createDummyBarChart();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start from Monday
        Date startOfWeek = calendar.getTime();
        if (userId != null) {
            Log.d("user Id", userId);
            myViewModel.fetchCompletedTasksForWeek(userId);
        }
        myViewModel.getPending(userId).observe(getViewLifecycleOwner(), count ->
                pendingCount.setText(count.toString())

        );
        myViewModel.getComplete(userId).observe(getViewLifecycleOwner(), count -> completeCount.setText(count.toString()));

        myViewModel.getCompletedTasksForWeek().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                Log.d("Task Count", "Received count: " + count);
                updateWeeklyBarChart(count, startOfWeek);
                Log.d("return Livedata", "taskWeekCount" + count);
            }

        });


        return view;
    }


    private void updateWeeklyBarChart(Map<String, Integer> taskCounts, Date startOfWeek) {
        // Log the taskCounts to ensure the map is being passed correctly
        Log.d("BarChart", "Task counts: " + taskCounts);

        // Get the dynamically generated week days
        List<String> weekDays = getWeekDays(startOfWeek);

        // Log the weekDays list
        Log.d("BarChart", "Week days: " + weekDays);

        // Initialize the list to hold the BarEntry objects
        List<BarEntry> entries = new ArrayList<>();

        // Map the week days to BarEntries and add them to the entries list
        int index = 0;
        for (String day : weekDays) {
            // Get the task count for this day (default to 0 if not found)
            Integer value = taskCounts.getOrDefault(day, 0);

            // Log each entry creation for debugging
            Log.d("BarChart", "Adding BarEntry - Index: " + index + ", Value: " + value + ", Label: " + day);

            // Add entry for each day, even if the value is 0 (this ensures every day is represented in the chart)
            entries.add(new BarEntry(index, value));
            index++;
        }

        // Log the entries to ensure they are created correctly
        Log.d("BarChart", "Entries: " + entries);

        // Create a BarDataSet
        BarDataSet barDataSet = new BarDataSet(entries, "Completed Tasks");
        barDataSet.setColor(Color.GRAY); // Set the color of the bars

        // Create BarData and set it to the chart
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Log to check if data has been set to the chart
        Log.d("BarChart", "Data set to chart");

        // Customize chart appearance
        barChart.setFitBars(true); // Make sure bars fit within the chart

        // Set x-axis labels to the weekDays
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekDays));

        // Log to check if the x-axis formatter is set
        Log.d("BarChart", "XAxis value formatter set");

        // Refresh the chart
        barChart.invalidate();
        barChart.getXAxis().setDrawGridLines(false);   // Disable grid lines on X-axis
        barChart.getAxisLeft().setDrawGridLines(false); // Disable grid lines on Y-axis
        barChart.getAxisRight().setDrawGridLines(false); // Disable grid lines on right Y-axis (optional)

        barChart.getXAxis().setDrawAxisLine(false);   // Remove the X-axis line
        barChart.getAxisLeft().setDrawAxisLine(false); // Remove the left Y-axis line
        barChart.getAxisRight().setDrawAxisLine(false); // Remove the ri

        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(false);

        barChart.setDrawValueAboveBar(true);
        YAxis axis = barChart.getAxisRight();
        axis.setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);
        DefaultValueFormatter defaultValueFormatter = new DefaultValueFormatter(0);
        barData.setValueFormatter(defaultValueFormatter);
        barDataSet.setValueTextSize(12f);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "";  // Return an empty string for bars with no data
                } else {
                    return String.valueOf((int) value); // Format other values as integers
                }
            }
        });
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularityEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


        // Log to confirm the chart is invalidated and should refresh
        Log.d("BarChart", "Chart invalidated and refreshed");
    }


    private List<String> getWeekDays(Date startOfWeek) {
        List<String> weekDays = new ArrayList<>();

        // Set up the calendar and format for the dates
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfWeek);

        // Loop through 7 days (from Monday to Sunday)
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            weekDays.add(sdf.format(calendar.getTime())); // Add formatted date to the list
            calendar.add(Calendar.DATE, 1); // Move to the next day
        }

        return weekDays;
    }


   /* private void getTaskCount(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Task")
                .whereEqualTo("userID", userID)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Count the number of documents retrieved (tasks)
                        int pending = task.getResult().size();
                        pendingCount.setText(String.valueOf(pending));
                    } else {
                        Log.w("count pending", "Error getting tasks", task.getException());
                    }
                });


        db.collection("Task")
                .whereEqualTo("userID", userID)
                .whereEqualTo("status", "complete")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int complete = task.getResult().size();
                        completeCount.setText(String.valueOf(complete));
                    } else {
                        Log.w("count complete", "Error getting tasks", task.getException());
                    }
                });
    }*/
}