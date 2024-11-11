package com.aqeel.to_do_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aqeel.to_do_list.databinding.ActivityMainBinding;
import com.aqeel.to_do_list.databinding.CustomDialogeBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    BottomAppBar bottomAppBar;
    ActivityMainBinding binding;
    FirebaseFirestore db;
    List<ModelTask> modelTaskList;
    AdapterTask adapterTask;
    String selectedCategory;
    String selectedTime;
    String selectedDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Innitialize Firebase
        db= FirebaseFirestore.getInstance();
        selectedCategory="All";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDueDate = String.format("%04d-%02d-%02d", year, month, day);
        calendar.add(Calendar.HOUR_OF_DAY,24);
        int hour= calendar.get(Calendar.HOUR_OF_DAY);
        int minute= calendar.get(Calendar.MINUTE);
        selectedTime = String.format("%02d:%02d", hour, minute);
        bottomAppBar = findViewById(R.id.bottom_app_bar);

        setSupportActionBar(bottomAppBar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView drawerNavigationView= findViewById(R.id.drawerNavigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.syncState();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        //set selected fragment
        bottomNavigationView.setSelectedItemId(R.id.tasks_id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TaskFragment())
                .commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        /*bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.drawer_id) {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });*/
        drawerNavigationView.setNavigationItemSelectedListener(item -> {
            if (R.id.logOutID==item.getItemId()){
                logout();
            }
            return false;
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                findViewById(R.id.content_frame).setTranslationX(slideOffset * drawerView.getWidth());
                findViewById(R.id.content_frame2).setTranslationX(slideOffset * drawerView.getWidth());
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }

        });



        FloatingActionButton addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(view -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View dialogeView = LayoutInflater.from(this).inflate(R.layout.custom_dialoge, null);
            CustomDialogeBinding binding1;
            binding1 = CustomDialogeBinding.inflate(getLayoutInflater());

            //save task to firebase
            binding1.saveBtnId.setOnClickListener(view12 -> {
                String enteredTask= binding1.enterTaskET.getText().toString();
                //get id from signup through intent
                String ID = UserSession.getInstance().getUserID();
                String status= "pending";

                TaskCounter.getInstance().addTaskToPending();
                //save task with user id
//                Toast.makeText(this, "id:"+ID, Toast.LENGTH_SHORT).show();
                if (ID!=null)
                {
                    long currentTimeMillis= System.currentTimeMillis();
                    ModelTask modelTask= new ModelTask(enteredTask,ID,currentTimeMillis,status,selectedCategory,selectedDueDate,selectedTime);
                    db.collection("Task")
                            .add(modelTask)
                            .addOnSuccessListener(documentReference -> {
                                modelTaskList= new ArrayList<>();
                                modelTaskList.add(modelTask);
                                adapterTask= new AdapterTask(this,modelTaskList);
                                adapterTask.addTask(modelTask);
                                adapterTask.notifyItemInserted(modelTaskList.size()-1);

                              /* Bundle result = new Bundle();
                               result.putBoolean("taskAdd",true);
                               getSupportFragmentManager().setFragmentResult("requestKey",result);*/
                                Toast.makeText(MainActivity.this,"Task saved",Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this,"Failed to save task",Toast.LENGTH_SHORT).show();
                            });
                    binding1.enterTaskET.setText("");
                    dialog.dismiss();

                }
                else {
                    Toast.makeText(MainActivity.this,"Email is null",Toast.LENGTH_SHORT).show();
                }

            });

            binding1.cardViewCategoryId.setOnClickListener(view1 -> {
                chooseCategory();

            });
            binding1.calenderDialogeId.setOnClickListener(view2 -> {
                taskScheduling();
            });
            dialog.setContentView(binding1.getRoot());
            dialog.show();


        });

    }

    TaskFragment taskFragment = new TaskFragment();
    Person_fragment personFragment = new Person_fragment();
    Calender_fragment calenderFragment = new Calender_fragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (R.id.tasks_id == item.getItemId()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new TaskFragment())
                    .commit();
            return true;
        } else if (R.id.calender_id == item.getItemId()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new Calender_fragment())
                    .commit();
            return true;
        } else if (R.id.profile_id == item.getItemId()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new Person_fragment())
                    .commit();
            return true;
        } else if (R.id.drawer_id == item.getItemId()) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }

    }
    public void logout() {
        SharedPref sharedPref = new SharedPref(this);
        sharedPref.setLoggedIn(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void showTimePickerDialog() {
        // Inflate the custom layout
        Dialog dialog= new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.time_picker_dialoge, null);
        setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // Initialize the TimePicker and Save button
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        ImageView closeIcon = dialogView.findViewById(R.id.closeIcon);

        // Configure the time picker
        timePicker.setIs24HourView(true); // Set 24-hour format if needed

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            // Save or use the selected time as needed
            // For example, you could update a TextView with the selected time:
             selectedTime = String.format("%02d:%02d", hour, minute);


            dialog.dismiss();
        });


        closeIcon.setOnClickListener(v -> dialog.dismiss());
    }
private void chooseCategory(){
    Dialog customDialog = new Dialog(this);
    View customDialogView = LayoutInflater.from(this).inflate(R.layout.category_choice, null);
    customDialog.setContentView(customDialogView);
    customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    customDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner));
    customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    RadioButton radioButtonALL =customDialogView.findViewById(R.id.radioALL);
    RadioButton radioButtonWork = customDialogView.findViewById(R.id.radioWork);
    RadioButton radioButtonPersonal = customDialogView.findViewById(R.id.radioPersonal);
    RadioButton radioButtonWish = customDialogView.findViewById(R.id.wishList);
    RadioButton radioButtonBirthday = customDialogView.findViewById(R.id.radioBirthday);
    Button saveBtn = customDialogView.findViewById(R.id.save_catg_btn);
    radioButtonALL.setOnCheckedChangeListener((button, isChecked) -> {
        if(isChecked){
            selectedCategory="All";
        }
    }); radioButtonWork.setOnCheckedChangeListener((button, isChecked) -> {
        if (isChecked){
            selectedCategory="Work";}
    }); radioButtonPersonal.setOnCheckedChangeListener((button, isChecked) -> {
        if (isChecked){
            selectedCategory="personal";}
    }); radioButtonWish.setOnCheckedChangeListener((button, isChecked) -> {
        if (isChecked){
            selectedCategory="Wishlist";
        }

    });radioButtonBirthday.setOnCheckedChangeListener((button, isChecked) -> {
        if (isChecked){
            selectedCategory="Birthday";}
    });

    customDialog.show();
    customDialog.setCancelable(false);
    saveBtn.setOnClickListener(View->{
        customDialog.dismiss();
        Toast.makeText(MainActivity.this,"category :"+ selectedCategory,Toast.LENGTH_SHORT).show();
    });
}
private  void taskScheduling(){
    Dialog dialog1 = new Dialog(this);
    View customDialogCalender = LayoutInflater.from(this).inflate(R.layout.custom_dialoge_calender, null);
    dialog1.setContentView(customDialogCalender);
    dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    dialog1.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner));
    dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog1.show();

    CalendarView calendarView= customDialogCalender.findViewById(R.id.calender_view);
    TextView noDateTV = customDialogCalender.findViewById(R.id.noDateId);
    TextView todayTV = customDialogCalender.findViewById(R.id.todayTVId);
    TextView tomorrowTv= customDialogCalender.findViewById(R.id.tomorrowTVId);
    TextView nextWeekTv=customDialogCalender.findViewById(R.id.nextWeekTVId);
    Button saveBtn = customDialogCalender.findViewById(R.id.saveBTn);
    Button cancelBtn =customDialogCalender.findViewById(R.id.cancelBTN);

    CardView timePickerView = customDialogCalender.findViewById(R.id.timePicker);
    if (calendarView!=null){
        calendarView.setOnDateChangeListener((calendarView1, year, month, dayOfMonth) -> {
            selectedDueDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });
    } else {
        Log.e("MainActivity", "CalendarView is null");
    }

    todayTV.setOnClickListener(view -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDueDate= LocalDate.now().toString();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            selectedDueDate = String.format("%04d-%02d-%02d", year, month, day);
        }
    });


    tomorrowTv.setOnClickListener(view -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDueDate= LocalDate.now().plusDays(1).toString();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            selectedDueDate = String.format("%04d-%02d-%02d", year, month, day);
        }
    });

    nextWeekTv.setOnClickListener(view -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDueDate= LocalDate.now().plusWeeks(1).toString();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,6);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            selectedDueDate = String.format("%04d-%02d-%02d", year, month, day);
        }
    });
    timePickerView.setOnClickListener(view -> {
        showTimePickerDialog();
    });
    saveBtn.setOnClickListener(view ->{
                Toast.makeText(this,selectedDueDate+"--" +selectedTime,Toast.LENGTH_LONG).show();
                dialog1.dismiss();
            }
            );
    cancelBtn.setOnClickListener(view -> dialog1.dismiss());

    dialog1.setCancelable(true);
}

}