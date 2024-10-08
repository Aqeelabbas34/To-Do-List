package com.aqeel.to_do_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ItemViewHolder> {
    private  List<ModelUser> userList ;

    public AdapterTask(List<ModelUser> userList) {
        this.userList = userList;
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
   ModelUser currentUser=userList.get(position);
  holder.nameTextView.setText(currentUser.name);
  holder.emailTextView.setText(currentUser.email);
  holder.passwordTextView.setText(currentUser.password);


    }
  // gets the size of list
    @Override
    public int getItemCount() {
        return userList.size();
    }
    //item view holder holds the item
    public  static class ItemViewHolder extends RecyclerView.ViewHolder{

        public  TextView nameTextView;
        public TextView  emailTextView;
        public  TextView passwordTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

          emailTextView =itemView.findViewById(R.id.emailET);
          passwordTextView=itemView.findViewById(R.id.passwordET);
          nameTextView=itemView.findViewById(R.id.userName);
        }
    }

}
