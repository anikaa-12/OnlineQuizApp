package com.example.onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
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

    TextView totalQuestionsTextView;
    TextView questionTextView;
    TextView timerText;

    Button ansA, ansB, ansC, ansD;
    Button submitBtn;

    int score = 0;
    int totalQuestion = QuestionAnswer.question.length;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    int timer = 10;
    CountDownTimer countDownTimer;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Views
        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        timerText = findViewById(R.id.timer_text);

        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        totalQuestionsTextView.setText("Total Questions : " + totalQuestion);

        loadNewQuestion();
    }

    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) view;

        if (clickedButton.getId() == R.id.submit_btn) {

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            if (selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])) {
                score++;
            }

            currentQuestionIndex++;
            loadNewQuestion();

        } else {
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(getResources().getcolor(R.color.pink));
        }
    }
    void loadNewQuestion() {

        if (currentQuestionIndex == totalQuestion) {
            finishQuiz();
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
        ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
        ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
        ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
        ansD.setText(QuestionAnswer.choices[currentQuestionIndex][3]);

        selectedAnswer = "";
        startTimer();
    }

    void startTimer() {
        timerText.setText("Time Left: " + timer + "s");

        countDownTimer = new CountDownTimer(timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                loadNewQuestion();
            }
        }.start();
    }

    void finishQuiz() {

        String passStatus = (score >= totalQuestion * 0.6) ? "Passed" : "Failed";

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("total", totalQuestion);
        result.put("status", passStatus);

        db.collection("quiz_results")
                .document(userId)
                .set(result);

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score: " + score + " out of " + totalQuestion)
                .setPositiveButton("Restart", (dialog, i) -> restartQuiz())
                .setCancelable(false)
                .show();
    }

    void restartQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        loadNewQuestion();
    }
}