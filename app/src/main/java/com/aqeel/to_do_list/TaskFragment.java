package com.aqeel.to_do_list;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.aqeel.to_do_list.databinding.FragmentTaskBinding;


public class TaskFragment extends Fragment {

    FragmentTaskBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding =FragmentTaskBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        binding.searchIcon.setOnClickListener(view1 ->showSearchBar());
        binding.cancelIcon.setOnClickListener(View -> hideSearchBar());
        return view;

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