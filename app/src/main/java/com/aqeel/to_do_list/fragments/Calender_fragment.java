package com.aqeel.to_do_list.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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
       View view= inflater.inflate(R.layout.fragment_calender, container, false);
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.task_recyclerView);
        calendar= view.findViewById(R.id.calender_id);
        db= FirebaseFirestore.getInstance();
        myViewModel= new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        sharedPref = new SharedPref(requireContext());
        ModelUser user = sharedPref.getData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTask=new AdapterTask(getContext(),taskList,this);
        recyclerView.setAdapter(adapterTask);
        String userId= user.getEmail();
        Calendar calendar1 = Calendar.getInstance();
        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
       String dateToday= dateFormat.format(calendar1.getTime());
       /* myViewModel.fetchTaskOnDate(userId,dateToday);*/


        calendar.setOnDateChangeListener((calendarView, year, month, date) -> {
            String selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", date);

            Log.d("Date", "Date in fragment :" +selectedDate);
//                Toast.makeText(requireActivity(),selectedDate,Toast.LENGTH_SHORT).show();


            Log.d("user ID calender frag","User Id called VM function from fragment:"+ userId);
            myViewModel.getOnDateTaskLiveData(selectedDate,userId).observe(getViewLifecycleOwner(),task->{

                if (isAdded()&& task!=null){
                    Log.d("Task List", "task received in observer:"+task);
                    adapterTask.updateList(task);

                } });




        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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