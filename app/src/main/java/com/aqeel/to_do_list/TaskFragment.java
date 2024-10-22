package com.aqeel.to_do_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aqeel.to_do_list.databinding.FragmentTaskBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment {

    FragmentTaskBinding binding;
    AdapterTask taskAdapter;
    List<ModelTask> taskList;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment for creating UI
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.searchIcon.setOnClickListener(view1 -> showSearchBar());
        binding.cancelIcon.setOnClickListener(View -> hideSearchBar());
        binding.templateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(), "Template button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.task_recyclerView);
        taskList = new ArrayList<>();




       /* taskList.add(new ModelTask("Test task 1"));
        taskList.add(new ModelTask("Test task 2"));*/

//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userId = UserSession.getInstance().getUserID();
        fetchTask(userId);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
         boolean taskAdded= result.getBoolean("taskAdd");
         if (taskAdded){
             fetchTask(userId);
         }
        });
    }

    private void fetchTask(String userid) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //fetching tasks from firebase
        db.collection("Task")
                .whereEqualTo("userID",userid)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(requireActivity(), "Failed to get task", Toast.LENGTH_SHORT).show();
                    }
                    taskList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String taskID= documentSnapshot.getId();
                            ModelTask userTask = documentSnapshot.toObject(ModelTask.class);
                            userTask.setTasKID(taskID);
                            taskList.add(userTask);
                        }
                        if (taskAdapter == null) {
                            taskAdapter = new AdapterTask(requireContext(), taskList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(taskAdapter);
                        } else {
//                            taskAdapter.updateList(taskList);
                            taskAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Activity activity= getActivity();
                        if (activity!= null){
                            Toast.makeText(activity, "Task not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

          /*  .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot!=null && !querySnapshot.isEmpty() ) {
                            // get matching task id
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                ModelTask userTask = document.toObject(ModelTask.class);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(taskAdapter);
                                taskList.add(userTask);
                                Toast.makeText(requireContext(), "task"+taskList.size(), Toast.LENGTH_SHORT).show();
                                taskAdapter= new AdapterTask(requireContext(),taskList);

                                Log.d("Firebase_Data", "Task retrieved: " + userTask.getTaskName()); // Log each task retrieved

                            }
                            Log.d("FirebaseData", "Tasks retrieved: " + taskList.size());
                            }


                        }
                        else {
                            Log.e("FirestoreError", "Error getting documents.", task.getException());
                            Toast.makeText(requireActivity(), "No task found", Toast.LENGTH_SHORT).show();
                        }



                });*/

    }

    private void showSearchBar() {
        binding.textTaskId.setVisibility(View.GONE);
        binding.searchIcon.setVisibility(View.GONE);
        binding.templateIcon.setVisibility(View.GONE);

        binding.searchView.setVisibility(View.VISIBLE);
        binding.cancelIcon.setVisibility(View.VISIBLE);
        binding.searchView.setIconified(false);
        binding.searchView.requestFocus();

    }

    private void hideSearchBar() {
        binding.textTaskId.setVisibility(View.VISIBLE);
        binding.searchIcon.setVisibility(View.VISIBLE);
        binding.templateIcon.setVisibility(View.VISIBLE);

        binding.searchView.setVisibility(View.GONE);
        binding.cancelIcon.setVisibility(View.GONE);
        binding.searchView.setQuery("", false);

    }

}