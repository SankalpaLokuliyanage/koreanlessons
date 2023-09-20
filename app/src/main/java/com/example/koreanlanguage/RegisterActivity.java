package com.example.koreanlanguage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText rEmail, rPass;

    Button registerButton, toLogin;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        rEmail = findViewById(R.id.signup_email);
        rPass = findViewById(R.id.signup_password);

        registerButton = findViewById(R.id.signUpText);
        toLogin = findViewById(R.id.toLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = rEmail.getText().toString().trim();
                String password = rPass.getText().toString().trim();





                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    rEmail.setError("Invalid Email");
                    rEmail.setFocusable(true);
                }
                else if (password.length() < 6) {
                    rPass.setError("Password is must be more than 6 Characters");
                    rPass.setFocusable(true);
                }
                else if (password.isEmpty()) {
                    rPass.setError("Please enter the password");
                    rPass.setFocusable(true);
                }




                else {
                    registerUser (email, password);
                }
            }
        });

    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

//                            get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();


//                            need register realtime database too
                            HashMap<Object,String> hashMap = new HashMap<>();

//                            put information in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);





                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            DatabaseReference reference = firebaseDatabase.getReference("Users");


                            reference.child(uid).setValue(hashMap);


                            Toast.makeText(RegisterActivity.this, "Registered\n" + user.getEmail() , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}