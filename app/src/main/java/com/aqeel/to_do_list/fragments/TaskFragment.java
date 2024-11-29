package com.aqeel.to_do_list.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aqeel.to_do_list.DataClasses.AdapterTask;
import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.UI.taskDetails;
import com.aqeel.to_do_list.databinding.FragmentTaskBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TaskFragment extends Fragment implements AdapterTask.OnItemClickedListener {

    FragmentTaskBinding binding;
    AdapterTask taskAdapter;
    List<ModelTask> taskList;

    SharedPref sharedPref;

    MyViewModel myViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment for creating UI
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        sharedPref = new SharedPref(requireContext());
        ModelUser user = sharedPref.getData();
        taskList = new ArrayList<>();

        String userId= user.getEmail();
        myViewModel= new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        binding.searchIcon.setOnClickListener(view1 -> showSearchBar());
        binding.cancelIcon.setOnClickListener(View -> hideSearchBar());

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                filterTask(text);
                return true;
            }
        });
        binding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterTask adapterTask= new AdapterTask(requireActivity(),taskList,this);
        binding.taskRecyclerView.setAdapter(adapterTask);
        myViewModel.fetchUserTask("All",userId);
        myViewModel.getMessage().observe(getViewLifecycleOwner(),message->{
            if (isAdded()){
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
            Log.e("Message","Toast");}
        });
        myViewModel.getTaskLiveData().observe(getViewLifecycleOwner(),task->{
            if (isAdded() ){
                adapterTask.updateList(task);
            }
        });

        binding.allTV.setOnClickListener(view1 ->
            myViewModel.fetchUserTask("All",userId)


        );
        binding.personalTV.setOnClickListener(view1 ->

            myViewModel.fetchUserTask("personal",userId)

        );
        binding.workTV.setOnClickListener(view1 ->
            myViewModel.fetchUserTask("Work",userId)

        );
        binding.wishListTV.setOnClickListener(view1 ->

            myViewModel.fetchUserTask("Wishlist",userId)

        );
        binding.birthdayTV.setOnClickListener(view1 ->

            myViewModel.fetchUserTask("Birthday",userId)

        );




        return view;

    }



    private void filterTask(String query){
        List<ModelTask> filteredList= new ArrayList<>();
        for (ModelTask task : taskList){
            if (task.getTaskName().toLowerCase( ).contains(query.toLowerCase()))
            {
                filteredList.add(task);
            }
        }
      updateTaskList(filteredList);
    }
    private void updateTaskList(List<ModelTask> filteredList) {
        if (taskAdapter == null) {
            taskAdapter = new AdapterTask(requireContext(), filteredList,this);
            binding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.taskRecyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateList(filteredList);
        }
    }


    private void showSearchBar() {
        binding.textTaskId.setVisibility(View.GONE);
        binding.searchIcon.setVisibility(View.GONE);


        binding.searchView.setVisibility(View.VISIBLE);
        binding.cancelIcon.setVisibility(View.VISIBLE);
        binding.searchView.setIconified(false);
        binding.searchView.requestFocus();

    }

    private void hideSearchBar() {
        binding.textTaskId.setVisibility(View.VISIBLE);
        binding.searchIcon.setVisibility(View.VISIBLE);


        binding.searchView.setVisibility(View.GONE);
        binding.cancelIcon.setVisibility(View.GONE);
        binding.searchView.setQuery("", false);
        binding.searchView.setQuery("", false); // Clear the search query
        // Re-fetch the task list when closing the search

    }

    @Override
    public void onTaskChecked(ModelTask modelTask, Boolean isChecked) {

        if (isChecked){
            modelTask.setStatus("complete");
            myViewModel.markComplete(modelTask);

            Log.d("AdapterTask", "Item checked: " + modelTask.getTaskName());
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
        Log.e("Activity","Paused");
        super.onPause();
    }
}