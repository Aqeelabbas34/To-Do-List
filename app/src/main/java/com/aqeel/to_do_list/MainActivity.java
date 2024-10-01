package com.aqeel.to_do_list;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aqeel.to_do_list.databinding.ActivityMainBinding;
import com.aqeel.to_do_list.databinding.CustomDialogeBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    BottomAppBar bottomAppBar;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.syncState();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
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

            binding1.cardViewCategoryId.setOnClickListener(view1 -> {
                Dialog customDialog = new Dialog(this);
                View customDialogView = LayoutInflater.from(this).inflate(R.layout.category_choice, null);
                customDialog.setContentView(customDialogView);
                customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                customDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner));
                customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                customDialog.show();
                customDialog.setCancelable(true);


            });
            binding1.calenderDialogeId.setOnClickListener(view2 -> {
                Dialog dialog1 = new Dialog(this);
                View customDialogCalender = LayoutInflater.from(this).inflate(R.layout.custom_dialoge_calender, null);
                dialog1.setContentView(customDialogCalender);
                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog1.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner));
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.show();
                dialog1.setCancelable(true);

            });
            dialog.setContentView(binding1.getRoot());
            dialog.show();


        });

    }

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
}