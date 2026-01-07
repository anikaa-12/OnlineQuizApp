package com.example.onlinequizapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    EditText etUsername,etPassword;
    Button btnLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth =
                firebaseAuth.getlnstance();
        etUsername = findViewByld(R.id.etUsername);
        etPassword = findViewByld(R.id.etPassword);
        btnLogin =
                findViewByld(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }
    private void loginUser(){
    String email = etUsername.getText().toString().trim();
    String password= etPassword.getText().toString().trim();
    if(email.isEmpty() || password.isEmpty()) 
}




