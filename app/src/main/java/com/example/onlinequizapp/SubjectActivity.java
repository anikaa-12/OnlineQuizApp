package com.example.onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SubjectActivity extends AppCompatActivity {

    Button btnJava, btnIsd, btnMicro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        btnJava = findViewById(R.id.btnJava);
        btnIsd = findViewById(R.id.btnIsd);
        btnMicro = findViewById(R.id.btnMicro);

        btnJava.setOnClickListener(v -> openQuiz("java"));
        btnIsd.setOnClickListener(v -> openQuiz("isd"));
        btnMicro.setOnClickListener(v -> openQuiz("micro"));
    }

    private void openQuiz(String subject) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}