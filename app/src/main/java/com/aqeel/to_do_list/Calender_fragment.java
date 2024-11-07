package com.aqeel.to_do_list;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class Calender_fragment extends Fragment {
    CalendarView calendar;
    RecyclerView recyclerView;
    AdapterTask adapterTask;
    List<ModelTask> taskList = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_calender, container, false);
        recyclerView = view.findViewById(R.id.task_recyclerView);
        calendar= view.findViewById(R.id.calender_view);
        db= FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTask=new AdapterTask(getContext(),taskList);
        recyclerView.setAdapter(adapterTask);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
            String selectedDate = year +"-"+(month+1)+"-"+ date;
            }
        });
        return  view;
    }
}