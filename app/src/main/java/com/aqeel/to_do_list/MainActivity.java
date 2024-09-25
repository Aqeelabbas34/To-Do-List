package com.aqeel.to_do_list;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
   DrawerLayout drawerLayout;
   ActionBarDrawerToggle actionBarDrawerToggle;
   BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
       bottomAppBar= findViewById(R.id.bottom_app_bar);
       setSupportActionBar(bottomAppBar);
       drawerLayout=findViewById(R.id.drawer_layout);
       actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
       actionBarDrawerToggle.syncState();
       BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
       bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
        int id=item.getItemId();
        if(id==R.id.drawer_id){
            drawerLayout.openDrawer(GravityCompat.START);

        }
       });
 FloatingActionButton showDialogeButton= findViewById(R.id.add_btn);
        showDialogeButton.setOnClickListener(view -> {
            BottomSheetDialog dialog= new BottomSheetDialog(this);
            View dialogeView= LayoutInflater.from(this).inflate(R.layout.custom_dialoge,null);
            dialog.setContentView(dialogeView);
            dialog.show();
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                findViewById(R.id.content_frame).setTranslationX(slideOffset * drawerView.getWidth());
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

    }


}