package com.aqeel.to_do_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ItemViewHolder> {
    private Context context;
    private  List<ModelTask>  modelTaskList ;



    public AdapterTask( Context context,List<ModelTask> taskList) {
        this.context=context;
        this.modelTaskList=taskList;
    }
  // inflate layout with item
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item,parent,false);
        return new ItemViewHolder(view);
    }
  // works as loop creates new item
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

      ModelTask currentTask= modelTaskList.get(position);
        Log.d("AdapterTask", "Binding task: " + currentTask.taskName);
      holder.taskTV.setText(currentTask.taskName);


    }
  // gets the size of list
    @Override
    public int getItemCount() {
        return modelTaskList.size();
    }

    //item view holder holds the item
    public  static class ItemViewHolder extends RecyclerView.ViewHolder{

        public  TextView taskTV;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

         taskTV=itemView.findViewById(R.id.taskTitle);
        }
    }
    public void updateList(List<ModelTask> newTask){
        this.modelTaskList.clear();
        this.modelTaskList.addAll(newTask);
    }

}
