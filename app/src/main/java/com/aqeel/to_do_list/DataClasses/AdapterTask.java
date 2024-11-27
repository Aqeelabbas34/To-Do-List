package com.aqeel.to_do_list.DataClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.singelton.TaskCounter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ItemViewHolder> {
    private Context context;
    private final List<ModelTask>  modelTaskList ;
    private final OnItemClickedListener itemClickedListener;

    public AdapterTask(Context context, List<ModelTask> taskList, OnItemClickedListener clickedListener) {
        this.context=context;
        this.modelTaskList=taskList;
        this.itemClickedListener=clickedListener;
    }
    public  interface OnItemClickedListener{
        void onTaskChecked(ModelTask modelTask,Boolean isChecked);
        void onItemClicked(ModelTask task);
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
      holder.taskChecked.setOnCheckedChangeListener(null);

      holder.timeStampTV.setText(formatTimestamp(currentTask.getTimeStamp()));
        boolean isTaskCompleted = currentTask.getStatus().equals("complete");
        holder.taskChecked.setChecked(isTaskCompleted);

        // Disable checkbox if task is completed
        // Enable checkbox for incomplete tasks
        holder.taskChecked.setEnabled(!isTaskCompleted); // Disable checkbox for completed tasks
      holder.taskChecked.setOnCheckedChangeListener((compoundButton, isChecked) -> {

          if (itemClickedListener != null){
          itemClickedListener.onTaskChecked(currentTask,isChecked);
      }

      });
        holder.itemView.setOnClickListener(view -> {
            Log.d("AdapterTask", "Item bind: " + currentTask.getTaskName());
            if (itemClickedListener!=null){
                itemClickedListener.onItemClicked(currentTask);
            }
        });

    }
  // gets the size of list
    @Override
    public int getItemCount() {
        return modelTaskList.size();
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM  hh:mm", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
    //item view holder holds the item
    public  static class ItemViewHolder extends RecyclerView.ViewHolder{

        public  TextView taskTV;
        public CheckBox taskChecked;
        public  TextView timeStampTV;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

         taskTV=itemView.findViewById(R.id.taskTitle);
         taskChecked= itemView.findViewById(R.id.task_checkBox);
         timeStampTV= itemView.findViewById(R.id.timeStamp);
        }
    }
    public void updateList(List<ModelTask> newTask){
        this.modelTaskList.clear();
        this.modelTaskList.addAll(newTask);
        this.notifyDataSetChanged();
    }
 
   /* private void markComplete(ModelTask task , int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Task")
                .document(task.getTasKID())
                .update("status","complete")
                .addOnSuccessListener(unused -> {
                    if(position>=0 && position< modelTaskList.size()){
                        task.setStatus("pending");
                        modelTaskList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,modelTaskList.size());
                    }else {
                        Log.e("AdapterTask", "Invalid index: " + position + " for list size: " + modelTaskList.size());
                    }

                }).addOnFailureListener(e -> Log.w("AdapterTask","Error deleting task",e));
    }*/

}
