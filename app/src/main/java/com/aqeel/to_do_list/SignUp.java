package com.aqeel.to_do_list;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aqeel.to_do_list.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {
    FirebaseFirestore db;
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db=FirebaseFirestore.getInstance();


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
                   db.collection("User")
                           .whereEqualTo("email", mail)
                           .get()
                           .addOnCompleteListener(task -> {
                               if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                   // Email already exists
                                   Toast.makeText(SignUp.this, "Email already registered", Toast.LENGTH_LONG).show();
                               } else {
                                   ModelUser modelUser = new ModelUser(name, mail, password);
                                   db.collection("User").add(modelUser)
                                           .addOnSuccessListener(documentReference -> {
                                               Toast.makeText(SignUp.this, "Signup successful", Toast.LENGTH_LONG).show();
                                               String userId= documentReference.getId();
                                               Intent intent= new Intent(SignUp.this,MainActivity.class);
                                               intent.putExtra("userID",userId);
                                               startActivity(intent);
                                           })
                                           .addOnFailureListener(e -> {
                                               Toast.makeText(SignUp.this, "Signup failed", Toast.LENGTH_SHORT).show();
                                           });
                               }
                           })
                           .addOnFailureListener(e -> {
                               Toast.makeText(SignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                           });
               }
           });
       binding.btnLogin.setOnClickListener(view -> startActivity(new Intent(SignUp.this,LoginActivity.class)));
    }
}