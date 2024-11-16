package com.aqeel.to_do_list.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.aqeel.to_do_list.R;
import com.aqeel.to_do_list.DataClasses.SharedPref;

public class SplashScreen extends AppCompatActivity {
   SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        sharedPref= new SharedPref(this);

         if (sharedPref.isLoggedIn()){
             startActivity(new Intent(SplashScreen.this, MainActivity.class));
             finish();
         }
         else {
             startActivity(new Intent(SplashScreen.this, LoginActivity.class));
             finish();
         }
    }
}