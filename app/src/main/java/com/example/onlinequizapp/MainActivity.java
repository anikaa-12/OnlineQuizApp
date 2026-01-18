package com.example.onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView, questionTextView, timerText, progressText;
    Button ansA, ansB, ansC, ansD, submitBtn, btnLogout;

    String selectedAnswer = "";
    Button selectedButton = null;

    int score = 0;
    int currentQuestionIndex = 0;
    int totalQuestion;
    int timer = 10;

    String subject;
    String[] questions;
    String[][] choices;
    String[] correctAnswer;

    CountDownTimer countDownTimer;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        subject = getIntent().getStringExtra("subject");
        if (subject == null) {
            finish();
            return;
        }

        questions = QuestionAnswer.getQuestions(subject);
        choices = QuestionAnswer.getChoices(subject);
        correctAnswer = QuestionAnswer.getCorrectAnswers(subject);

        if (questions.length == 0) {
            finish();
            return;
        }

        totalQuestion = questions.length;

        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        timerText = findViewById(R.id.timerText);
        progressText = findViewById(R.id.progress_text);

        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);
        btnLogout = findViewById(R.id.btnLogout);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        totalQuestionsTextView.setText("Total Questions : " + totalQuestion);
        loadNewQuestion();
    }

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;

        if (clickedButton.getId() != R.id.submit_btn) {
            selectedAnswer = clickedButton.getText().toString();
            selectedButton = clickedButton;

            resetOptionColors();
            clickedButton.setBackgroundColor(getResources().getColor(R.color.pink));
        } else {
            handleSubmit();
        }
    }

    private void handleSubmit() {
        if (countDownTimer != null) countDownTimer.cancel();

        ansA.setEnabled(false);
        ansB.setEnabled(false);
        ansC.setEnabled(false);
        ansD.setEnabled(false);

        Button correctButton = null;
        if (correctAnswer[currentQuestionIndex].equals(ansA.getText().toString()))
            correctButton = ansA;
        else if (correctAnswer[currentQuestionIndex].equals(ansB.getText().toString()))
            correctButton = ansB;
        else if (correctAnswer[currentQuestionIndex].equals(ansC.getText().toString()))
            correctButton = ansC;
        else if (correctAnswer[currentQuestionIndex].equals(ansD.getText().toString()))
            correctButton = ansD;

        if (selectedAnswer.equals(correctAnswer[currentQuestionIndex])) {
            score++;
        } else if (selectedButton != null) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.wrong_red));
        }

        if (correctButton != null)
            correctButton.setBackgroundColor(getResources().getColor(R.color.correct_green));

        submitBtn.postDelayed(() -> {
            currentQuestionIndex++;
            loadNewQuestion();
        }, 1000);
    }

    private void resetOptionColors() {
        ansA.setBackgroundColor(getResources().getColor(R.color.white));
        ansB.setBackgroundColor(getResources().getColor(R.color.white));
        ansC.setBackgroundColor(getResources().getColor(R.color.white));
        ansD.setBackgroundColor(getResources().getColor(R.color.white));

        ansA.setEnabled(true);
        ansB.setEnabled(true);
        ansC.setEnabled(true);
        ansD.setEnabled(true);
    }

    private void loadNewQuestion() {
        resetOptionColors();

        if (currentQuestionIndex == totalQuestion) {
            finishQuiz();
            return;
        }

        if (countDownTimer != null) countDownTimer.cancel();

        progressText.setText("Question " + (currentQuestionIndex + 1) + " / " + totalQuestion);
        questionTextView.setText(questions[currentQuestionIndex]);
        ansA.setText(choices[currentQuestionIndex][0]);
        ansB.setText(choices[currentQuestionIndex][1]);
        ansC.setText(choices[currentQuestionIndex][2]);
        ansD.setText(choices[currentQuestionIndex][3]);

        selectedAnswer = "";
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                handleSubmit();
            }
        }.start();
    }

    private void finishQuiz() {
        String status = (score >= totalQuestion * 0.6) ? "Passed" : "Failed";

        Map<String, Object> result = new HashMap<>();
        result.put("subject", subject);
        result.put("score", score);
        result.put("total", totalQuestion);
        result.put("status", status);

        db.collection("quiz_results")
                .document(auth.getCurrentUser().getUid() + "_" + subject)
                .set(result);

        new AlertDialog.Builder(this)
                .setTitle(status)
                .setMessage("Score: " + score + " / " + totalQuestion)
                .setCancelable(false)
                .setPositiveButton("Back to Subjects", (d, i) -> {
                    Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}