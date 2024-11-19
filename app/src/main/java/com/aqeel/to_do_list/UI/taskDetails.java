package com.aqeel.to_do_list.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.R;

public class taskDetails extends AppCompatActivity {
   TextView taskNameTV,taskCategoryTV,dueDateTV,timeTV;
   MyViewModel myViewModel;
   ModelTask modelTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        taskNameTV= findViewById(R.id.taskName_id);
        taskCategoryTV=findViewById(R.id.taskCategory_id);
        dueDateTV=findViewById(R.id.dueDate_id);
        timeTV=findViewById(R.id.time_id);
        myViewModel= new ViewModelProvider(this).get(MyViewModel.class);
        Intent intent =getIntent();
         modelTask =(ModelTask) intent.getSerializableExtra("task");
        if (modelTask!=null){
            taskNameTV.setText(modelTask.getTaskName());
            taskCategoryTV.setText(modelTask.getCategory());
            dueDateTV.setText(modelTask.getDueDate());
            timeTV.setText(modelTask.getTime());

        }
        Toolbar toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.over_flow_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int Id = item.getItemId();
        if (Id==R.id.mark_done){
            myViewModel.markComplete(modelTask);

            return true;
        } else if (Id==R.id.delete_task) {
            myViewModel.deleteTask(modelTask);
            this.finish();
            return true;
        } else  {
        return super.onOptionsItemSelected(item);
        }
    }
}