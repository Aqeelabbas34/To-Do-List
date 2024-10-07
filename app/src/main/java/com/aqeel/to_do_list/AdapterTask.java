package com.aqeel.to_do_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ItemViewHolder> {
    private ArrayList<ModelTask> taskArrayList;

    public AdapterTask(ArrayList<ModelTask> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
    ModelTask currentTask=taskArrayList.get(position);
    holder.taskTV.setText(currentTask.getTaskName());
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }
    //item view holder holds the item
    public  static class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView taskTV;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskTV = itemView.findViewById(R.id.taskTitle);
        }
    }

}
