package com.aqeel.to_do_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

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
      holder.taskChecked.setOnCheckedChangeListener(null);
      holder.taskChecked.setChecked(currentTask.isComplete());
      holder.taskChecked.setOnCheckedChangeListener((compoundButton, isChecked) -> {
       if (isChecked){
           removeTask(currentTask,position);
       }
      });


    }
  // gets the size of list
    @Override
    public int getItemCount() {
        return modelTaskList.size();
    }

    //item view holder holds the item
    public  static class ItemViewHolder extends RecyclerView.ViewHolder{

        public  TextView taskTV;
        public CheckBox taskChecked;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

         taskTV=itemView.findViewById(R.id.taskTitle);
         taskChecked= itemView.findViewById(R.id.task_checkBox);
        }
    }
    public void updateList(List<ModelTask> newTask){
        this.modelTaskList.clear();
        this.modelTaskList.addAll(newTask);
    }
    private void removeTask(ModelTask task , int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Task")
                .document(task.getTasKID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        modelTaskList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,modelTaskList.size());
                    }
                }).addOnFailureListener(e -> Log.w("AdapterTask","Error deleting task",e));
    }

}
