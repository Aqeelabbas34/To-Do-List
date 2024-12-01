package com.aqeel.to_do_list.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityLoginBinding binding;
    SharedPref sharedPref;
    MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        sharedPref=new SharedPref(this);
        setContentView(binding.getRoot());
        db=FirebaseFirestore.getInstance();
        myViewModel= new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getMessage().observe(this,message->{
            if (message!=null){
                Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
            }
        });
        myViewModel.getSuccess().observe(this,success->{
            if (success!= null && success){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                showLoading(false);
                sharedPref.setLoggedIn(true);

            }else {
                showLoading(false);
            }
        });
        binding.btnLogin.setOnClickListener(view -> {

            String mail= binding.EmailET.getText().toString();
            String pass=binding.paswordET.getText().toString();
            ModelUser user = new ModelUser("",mail,pass);

            sharedPref.saveData(user);
            if(!mail.isEmpty() && !pass.isEmpty()){
                showLoading(true);
                    myViewModel.login(mail,pass);

            } else if (mail.isEmpty()) {
                binding.EmailET.setError("Email is Empty");
            } else {
                Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();

            }
        });
        binding.btnSignUP.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUp.class));
            finish();
        });

    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.circularProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.circularProgressBar.setVisibility(View.GONE);
        }
    }
}