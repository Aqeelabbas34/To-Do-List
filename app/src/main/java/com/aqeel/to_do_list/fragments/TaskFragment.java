package com.aqeel.to_do_list.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aqeel.to_do_list.DataClasses.AdapterTask;
import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.databinding.FragmentTaskBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class TaskFragment extends Fragment implements AdapterTask.OnItemClickedListener {

    FragmentTaskBinding binding;
    AdapterTask taskAdapter;
    List<ModelTask> taskList;

    SharedPref sharedPref;
    View previousSelectedCard = null;
    MyViewModel myViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        AtomicReference<String> category = new AtomicReference<>("All");
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
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();}
        });
        myViewModel.getTaskLiveData().observe(getViewLifecycleOwner(),task->{
            if (isAdded() ){
                adapterTask.updateList(task);
            }
        });
        View.OnClickListener categoryClickListener = view1 -> {
            String selectedCategory = "All"; // Default category
            if (view.getId() == R.id.all_TV) {
                selectedCategory = "All";
            } else if (view.getId() == R.id.personal_TV) {
                selectedCategory = "personal";
            } else if (view.getId() == R.id.work_TV) {
                selectedCategory = "Work";
            } else if (view.getId() == R.id.wishList_TV) {
                selectedCategory = "Wishlist";
            } else if (view.getId() == R.id.birthday_TV) {
                selectedCategory = "Birthday";
            }
            category.set(selectedCategory);
            myViewModel.fetchUserTask(category.get(), userId);
        };

        binding.allTV.setOnClickListener(categoryClickListener);
        binding.personalTV.setOnClickListener(categoryClickListener);
        binding.workTV.setOnClickListener(categoryClickListener);
        binding.wishListTV.setOnClickListener(categoryClickListener);
        binding.birthdayTV.setOnClickListener(categoryClickListener);



        binding.templateIcon.setOnClickListener(view2 -> Toast.makeText(requireActivity(), "Template button clicked", Toast.LENGTH_SHORT).show());
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
    private void onCardClick(CardView selectedCard) {
        // If there was a previously selected card, reset its background color
        if (previousSelectedCard != null) {
            previousSelectedCard.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
        }

        // Set the background color of the currently clicked card
        selectedCard.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));

        // Update the previously selected card to the current one
        previousSelectedCard = selectedCard;


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
        binding.searchView.setQuery("", false); // Clear the search query
        // Re-fetch the task list when closing the search

    }

    @Override
    public void onTaskChecked(ModelTask modelTask, Boolean isChecked) {
        if (isChecked){
            modelTask.setStatus("complete");
            myViewModel.markComplete(modelTask);
        }
    }

    @Override
    public void onItemClicked(ModelTask task) {
    
    }
}