package com.aqeel.to_do_list.DataClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.singelton.TaskCounter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
      holder.taskChecked.setOnCheckedChangeListener(null);

      holder.timeStampTV.setText(formatTimestamp(currentTask.getTimeStamp()));
        holder.taskChecked.setChecked(currentTask.getStatus().equals("complete"));
      holder.taskChecked.setOnCheckedChangeListener((compoundButton, isChecked) -> {
       if (isChecked){

           TaskCounter.getInstance().completeTask();
//           holder.itemView.setVisibility(View.INVISIBLE);
           if (position!=RecyclerView.NO_POSITION){
           markComplete(currentTask,position);
           updateList(modelTaskList);
           }

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
   public void addTask(ModelTask task) {
       this.modelTaskList.add(task); // Add the new task to the list
       notifyItemInserted(modelTaskList.size() - 1); // Notify the adapter about the new task
   }
    private void markComplete(ModelTask task , int position){
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
    }

}
