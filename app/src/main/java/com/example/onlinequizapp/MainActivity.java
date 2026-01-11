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
    TextView progressText;

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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        timerText = findViewById(R.id.timerText);
        progressText = findViewById(R.id.progress_text);

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
        Button clickedButton = (Button) view;

        if (clickedButton.getId() != R.id.submit_btn) {

            selectedAnswer = clickedButton.getText().toString();


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
        if (QuestionAnswer.correctAnswers[currentQuestionIndex].equals(ansA.getText().toString()))
            correctButton = ansA;
        else if (QuestionAnswer.correctAnswers[currentQuestionIndex].equals(ansB.getText().toString()))
            correctButton = ansB;
        else if (QuestionAnswer.correctAnswers[currentQuestionIndex].equals(ansC.getText().toString()))
            correctButton = ansC;
        else if (QuestionAnswer.correctAnswers[currentQuestionIndex].equals(ansD.getText().toString()))
            correctButton = ansD;

        if (selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])) {
            score++;
            if (correctButton != null)
                correctButton.setBackgroundColor(getResources().getColor(R.color.correct_green));
        } else {
            if (correctButton != null)
                correctButton.setBackgroundColor(getResources().getColor(R.color.correct_green));
            if (!selectedAnswer.isEmpty())
                clickedButton.setBackgroundColor(getResources().getColor(R.color.wrong_red));
        }

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

    void loadNewQuestion() {
        resetOptionColors();

        if (currentQuestionIndex == totalQuestion) {
            finishQuiz();
            return;
        }

        if (countDownTimer != null) countDownTimer.cancel();

        progressText.setText("Question " + (currentQuestionIndex + 1) + " / " + totalQuestion);
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
                handleSubmit();
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