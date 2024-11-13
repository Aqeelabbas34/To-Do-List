package com.aqeel.to_do_list;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Calender_fragment extends Fragment {
    CalendarView calendar;
    RecyclerView recyclerView;
    AdapterTask adapterTask;
    List<ModelTask> taskList = new ArrayList<>();
    FirebaseFirestore db;
    SharedPref sharedPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_calender, container, false);
        recyclerView = view.findViewById(R.id.task_recyclerView);
        calendar= view.findViewById(R.id.calender_id);
        db= FirebaseFirestore.getInstance();
        sharedPref = new SharedPref(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTask=new AdapterTask(getContext(),taskList);
        recyclerView.setAdapter(adapterTask);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
            String selectedDate = year +"-"+(month+1)+"-"+ date;
                Toast.makeText(requireActivity(),selectedDate,Toast.LENGTH_SHORT).show();
                ModelUser user = sharedPref.getData();
                String userId= user.getEmail();
            db.collection("Task")
                    .whereEqualTo("userID",userId)
                    .whereEqualTo("dueDate",selectedDate)
                    .whereEqualTo("status","pending")
                    .addSnapshotListener((querySnapshot, error) -> {
                       if (isAdded()){
                           if (error!= null){
                               Toast.makeText(requireActivity(),"Failed to get Task",Toast.LENGTH_LONG).show();
                           }
                       }
                        taskList.clear();

                          if (querySnapshot!= null && !querySnapshot.isEmpty()) {
                              for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                  String taskID= documentSnapshot.getId();
                                  ModelTask userTask = documentSnapshot.toObject(ModelTask.class);
                                  userTask.setTasKID(taskID);
                                  taskList.add(userTask);
                              }

                             if (isAdded()){
                                 adapterTask = new AdapterTask(requireActivity(), taskList);
                                 recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                 recyclerView.setAdapter(adapterTask);
                             }


                        }
                          else {
                              Toast.makeText(requireActivity(),"Task Not found",Toast.LENGTH_SHORT).show();
                          }
                    });
            }
        });
        return  view;
    }
}