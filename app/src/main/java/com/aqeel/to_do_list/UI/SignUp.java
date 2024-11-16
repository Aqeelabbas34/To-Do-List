package com.aqeel.to_do_list.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.aqeel.to_do_list.DataClasses.SharedPref;
import com.aqeel.to_do_list.MVVM.MyViewModel;
import com.aqeel.to_do_list.singelton.UserSession;
import com.aqeel.to_do_list.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {
    FirebaseFirestore db;
    ActivitySignUpBinding binding;
    SharedPref sharedPref;
    MyViewModel myViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db=FirebaseFirestore.getInstance();
        sharedPref= new SharedPref(this);
        myViewModel= new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getMessage().observe(this,message->{
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        });


       binding.btnSignUP.setOnClickListener(view -> {
           String name=binding.nameET.getText().toString();
           String mail=binding.EmailET.getText().toString();
           String password=binding.paswordET.getText().toString();
           if(name.isEmpty()){
               binding.nameET.setError("enter name");
           } else if (mail.isEmpty()) {
               binding.EmailET.setError("enter email");
           } else if (password.isEmpty()) {
               binding.paswordET.setError("enter password");
           }



              else {
               ModelUser modelUser = new ModelUser(name, mail, password);
                      myViewModel.signUpHandler(mail,modelUser);
                      sharedPref.saveData(modelUser);
               sharedPref.setLoggedIn(true);
               startActivity(new Intent(SignUp.this,MainActivity.class));
               finish();

           }
           });
       binding.btnLogin.setOnClickListener(view -> {
           startActivity(new Intent(SignUp.this, LoginActivity.class));
           finish();
       });

    }
}