package com.aqeel.to_do_list.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.aqeel.to_do_list.DataClasses.AdapterTask;
import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.UI.taskDetails;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class Completed_fragment extends Fragment implements AdapterTask.OnItemClickedListener {
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

        return inflater.inflate(R.layout.fragment_completed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.task_recyclerView);
        calendar= view.findViewById(R.id.calender_id);
        db= FirebaseFirestore.getInstance();
        myViewModel= new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        sharedPref = new SharedPref(requireContext());
        ModelUser user = sharedPref.getData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTask=new AdapterTask(getContext(),taskList,this);
        recyclerView.setAdapter(adapterTask);
        myViewModel.fetchCompletedTask(user.getEmail());
        myViewModel.getCompletedTaskLiveData().observe(getViewLifecycleOwner(),task->{
            ModelTask modelTask= new ModelTask();
            if (isAdded()){
                Log.d("Task List", "task received in observer:"+task+modelTask.getTaskName());
                adapterTask.updateList(task);

            } });



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



}