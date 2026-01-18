package com.example.onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SubjectActivity extends AppCompatActivity {

    Button btnJava, btnIsd, btnMicro, btnLogout;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        auth = FirebaseAuth.getInstance();

        btnJava = findViewById(R.id.btnJava);
        btnIsd = findViewById(R.id.btnIsd);
        btnMicro = findViewById(R.id.btnMicro);
        btnLogout = findViewById(R.id.btnLogout);

        btnJava.setOnClickListener(v -> openQuiz("java"));
        btnIsd.setOnClickListener(v -> openQuiz("isd"));
        btnMicro.setOnClickListener(v -> openQuiz("micro"));

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(SubjectActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void openQuiz(String subject) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}