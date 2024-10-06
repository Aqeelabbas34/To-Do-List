package com.aqeel.to_do_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.aqeel.to_do_list.databinding.FragmentTaskBinding;

import java.util.ArrayList;


public class TaskFragment extends Fragment {

    FragmentTaskBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment for creating UI

        binding =FragmentTaskBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        binding.searchIcon.setOnClickListener(view1 ->showSearchBar());
        binding.cancelIcon.setOnClickListener(View -> hideSearchBar());
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView= binding.taskRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<ModelTask> taskArrayList= new ArrayList<>();
       taskArrayList.add(new ModelTask("Task 1"));
       taskArrayList.add(new ModelTask("Task 2"));
       taskArrayList.add(new ModelTask("Task 3"));
       taskArrayList.add(new ModelTask("Task 4"));
       taskArrayList.add(new ModelTask("Task 5"));
       taskArrayList.add(new ModelTask("Task 6"));
              taskArrayList.add(new ModelTask("Task 7"));
              taskArrayList.add(new ModelTask("Task 8"));
              taskArrayList.add(new ModelTask("Task 9"));
              taskArrayList.add(new ModelTask("Task 6"));
              taskArrayList.add(new ModelTask("Task 6"));

        AdapterTask adapterTask=new AdapterTask(taskArrayList);
        recyclerView.setAdapter(adapterTask);

    }

    private void showSearchBar(){
        binding.textTaskId.setVisibility(View.GONE);
        binding.searchIcon.setVisibility(View.GONE);
        binding.templateIcon.setVisibility(View.GONE);

        binding.searchView.setVisibility(View.VISIBLE);
        binding.cancelIcon.setVisibility(View.VISIBLE);
         binding.searchView.setIconified(false);
         binding.searchView.requestFocus();

   }
   private void hideSearchBar(){
        binding.textTaskId.setVisibility(View.VISIBLE);
        binding.searchIcon.setVisibility(View.VISIBLE);
        binding.templateIcon.setVisibility(View.VISIBLE);

        binding.searchView.setVisibility(View.GONE);
        binding.cancelIcon.setVisibility(View.GONE);
         binding.searchView.setQuery("",false);

   }
   
}