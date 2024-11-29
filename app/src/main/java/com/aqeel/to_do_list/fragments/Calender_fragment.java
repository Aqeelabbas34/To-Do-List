package com.aqeel.to_do_list.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.aqeel.to_do_list.DataClasses.AdapterTask;
import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.UI.taskDetails;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class Calender_fragment extends Fragment implements AdapterTask.OnItemClickedListener {
    CalendarView calendar;
    RecyclerView recyclerView;
    AdapterTask adapterTask;
    List<ModelTask> taskList = new ArrayList<>();
    FirebaseFirestore db;
    SharedPref sharedPref;
    MyViewModel myViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_calender, container, false);
        recyclerView = view.findViewById(R.id.task_recyclerView);
        calendar= view.findViewById(R.id.calender_id);
        db= FirebaseFirestore.getInstance();
        myViewModel= new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        sharedPref = new SharedPref(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTask=new AdapterTask(getContext(),taskList,this);
        recyclerView.setAdapter(adapterTask);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
            String selectedDate = year +"-"+(month+1)+"-"+ date;
//                Toast.makeText(requireActivity(),selectedDate,Toast.LENGTH_SHORT).show();
                ModelUser user = sharedPref.getData();
                String userId= user.getEmail();
                myViewModel.fetchTaskOnDate(selectedDate,userId);
                myViewModel.getTaskLiveData().observe(getViewLifecycleOwner(),task->{
                    if (isAdded()){
                        adapterTask.updateList(task );
                    }
                });


            }
        });
        return  view;
    }

    @Override
    public void onTaskChecked(ModelTask modelTask, Boolean isChecked) {
        if (isChecked){
            modelTask.setStatus("complete");
            myViewModel.markComplete(modelTask);
        }
    }

    @Override
    public void onItemClicked(ModelTask modelTask) {
        Intent intent = new Intent(requireActivity(), taskDetails.class);
        intent.putExtra("task",modelTask);
        startActivity(intent);

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}