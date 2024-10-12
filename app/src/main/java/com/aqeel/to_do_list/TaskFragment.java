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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        List<ModelTask> taskList = new ArrayList<>();
        FirebaseFirestore db;
        db=FirebaseFirestore.getInstance();


        db.collection("Task")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()){
                            // get matching user
                            for (QueryDocumentSnapshot document: querySnapshot){
                                ModelTask userTask =document.toObject(ModelTask.class);
                                taskList.add(userTask);
                             AdapterTask   taskAdapter = new AdapterTask(taskList);
                                recyclerView.setAdapter(taskAdapter);

                            }

                        }
                    }
                });


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