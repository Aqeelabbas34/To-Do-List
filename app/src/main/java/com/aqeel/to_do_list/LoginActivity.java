package com.aqeel.to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aqeel.to_do_list.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityLoginBinding binding;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        sharedPref=new SharedPref(this);
        setContentView(binding.getRoot());
        db=FirebaseFirestore.getInstance();
        binding.btnLogin.setOnClickListener(view -> {

            String mail= binding.EmailET.getText().toString();
            String pass=binding.paswordET.getText().toString();
            ModelUser user = new ModelUser("",mail,pass);
            sharedPref.saveData(user);
            if(!mail.isEmpty() && !pass.isEmpty()){
                    loginHandler(mail,pass);
            } else if (mail.isEmpty()) {
                binding.EmailET.setError("Email is Empty");
            } else {
                Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();

            }
        });
        binding.btnSignUP.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,SignUp.class));
        });

    }
    private void loginHandler(String enteredEmail,String enteredPassword){
        db.collection("User")
                .whereEqualTo("email",enteredEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot= task.getResult();
                        if (!querySnapshot.isEmpty()){
                            for(QueryDocumentSnapshot document : querySnapshot){
                                ModelUser modelUser=document.toObject(ModelUser.class);
                                if (modelUser.getPassword().equals(enteredPassword)){
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    sharedPref.setLoggedIn(true);
                                    Intent intent= new Intent(LoginActivity.this,MainActivity.class);
//                                    intent.putExtra("userID",enteredEmail);
                                    UserSession.getInstance().setUserID(enteredEmail);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"No user found",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Error while fetching data" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });
    }
}