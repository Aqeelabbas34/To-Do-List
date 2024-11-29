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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

       myViewModel.getSuccess().observe(this,success->
       {
           if (success) {
               startActivity(new Intent(SignUp.this,MainActivity.class));
               finish();

           }
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
           }else if(!isValidEmail(mail)){
               binding.EmailET.setError("Invalid Email");
           } else if (password.length()<6) {
               Toast.makeText(this, "Password must be 6 digits", Toast.LENGTH_SHORT).show();

           } else {
               ModelUser modelUser = new ModelUser(name, mail, password);
                      myViewModel.signUpHandler(mail,modelUser);
                      sharedPref.setLoggedIn(true);
                      if (Boolean.TRUE.equals(myViewModel.getSuccess().getValue())){
                          sharedPref.saveData(modelUser);
                      }
           }
           });
       binding.btnLogin.setOnClickListener(view -> {
           startActivity(new Intent(SignUp.this, LoginActivity.class));
           finish();
       });

    }
    private boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailPattern = "^[a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,}$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}