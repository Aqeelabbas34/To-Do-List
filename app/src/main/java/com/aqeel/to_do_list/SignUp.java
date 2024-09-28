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
               ModelUser modelUser=new ModelUser(name,mail,password);

               db.collection("User").add(modelUser)
                       .addOnSuccessListener(documentReference -> {
                           Toast.makeText(SignUp.this,"Successful",Toast.LENGTH_LONG).show();
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(SignUp.this,"failed",Toast.LENGTH_SHORT).show();;
                           }
                       });
               startActivity(new Intent(SignUp.this,MainActivity.class));
           }

       });
    }
}