package com.aqeel.to_do_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.Toast;
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
    AdapterTask taskAdapter;
    List<ModelTask> taskList ;
    RecyclerView recyclerView;

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


        recyclerView= view.findViewById(R.id.task_recyclerView);
        taskList = new ArrayList<>();
        taskAdapter= new AdapterTask(taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

       /* taskList.add(new ModelTask("Test task 1"));
        taskList.add(new ModelTask("Test task 2"));*/
        taskAdapter.notifyDataSetChanged();
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId= bundle.getString("userID");
            fetchTask(userId);
        }
    }
private void fetchTask(String userid) {
    FirebaseFirestore db;
    db = FirebaseFirestore.getInstance();


    db.collection("User")
                .document(userid)
                .collection("Task")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot!=null && !querySnapshot.isEmpty() ) {
                            // get matching task id
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                ModelTask userTask = document.toObject(ModelTask.class);
                                taskList.add(userTask);
                                Log.d("Firebase_Data", "Task retrieved: " + userTask.getTaskName()); // Log each task retrieved

                            }
                            Log.d("FirebaseData", "Tasks retrieved: " + taskList.size());
                            taskAdapter.notifyDataSetChanged();
                            }


                        }
                        else {
                            Log.e("FirestoreError", "Error getting documents.", task.getException());
                            Toast.makeText(this.getActivity(), "No task found", Toast.LENGTH_SHORT).show();
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