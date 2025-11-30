package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
// import androidx.appcompat.app.AppCompatActivity; // <-- BỎ DÒNG NÀY

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SỬA: extends BaseActivity thay vì AppCompatActivity
public class PlayQuizActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTitle, tvCount, tvQuestion, tvTimer;
    private Button btnA, btnB, btnC, btnD;
    private ImageView btnBack;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private boolean isSFXOn;
    private boolean isTimePerQOn;
    private int valTimePerQ, valTotalTime, maxQuestions;

    private CountDownTimer timerPerQuestion, timerTotal;

    private MediaPlayer mpCorrect, mpWrong;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);

        tvTitle = findViewById(R.id.tvQuizTitle);
        tvCount = findViewById(R.id.tvQuestionCount);
        tvQuestion = findViewById(R.id.tvQuestionContent);
        tvTimer = findViewById(R.id.tvTimer);
        btnA = findViewById(R.id.btnOptionA);
        btnB = findViewById(R.id.btnOptionB);
        btnC = findViewById(R.id.btnOptionC);
        btnD = findViewById(R.id.btnOptionD);
        btnBack = findViewById(R.id.btnBack);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
        btnBack.setOnClickListener(v -> finishQuiz());

        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        isSFXOn = prefs.getBoolean("SFX", true);

        isTimePerQOn = prefs.getBoolean("IS_TIME_PER_Q", true);
        valTimePerQ = prefs.getInt("VAL_TIME_PER_Q", 10);
        valTotalTime = prefs.getInt("VAL_TOTAL_TIME", 5);
        maxQuestions = prefs.getInt("VAL_QUEST_COUNT", 10);

        if (isSFXOn) {
            mpCorrect = MediaPlayer.create(this, R.raw.correct);
            mpWrong = MediaPlayer.create(this, R.raw.wrong);

            android.util.Log.d("AUDIO_CHECK", "Correct MP ID: " + (mpCorrect != null ? mpCorrect.getDuration() : "NULL"));
        }

        if (!isTimePerQOn) {
            startTotalTimer();
        } else {
            if (timerTotal != null) timerTotal.cancel();
        }

        int quizId = getIntent().getIntExtra("QUIZ_ID", 0);
        tvTitle.setText(getIntent().getStringExtra("QUIZ_TITLE"));
        fetchQuestions(quizId);
    }



    private void fetchQuestions(int quizId) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getQuestionsByQuizId(API_KEY, "eq." + quizId).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    questionList = response.body();

                    Collections.shuffle(questionList);

                    if (questionList.size() > maxQuestions) {
                        questionList = questionList.subList(0, maxQuestions);
                    }

                    loadQuestion(0);
                } else {
                    tvQuestion.setText("Chưa có dữ liệu câu hỏi!");
                    disableButtons();
                }
            }
            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toast.makeText(PlayQuizActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestion(int index) {
        resetButtonColors();
        if (index < questionList.size()) {
            if (isTimePerQOn) startPerQuestionTimer();

            Question q = questionList.get(index);
            tvQuestion.setText(q.getContent());
            tvCount.setText((index + 1) + "/" + questionList.size());

            String type = q.getType();
            btnA.setEnabled(true); btnB.setEnabled(true);
            if ("TF".equalsIgnoreCase(type)) {
                btnA.setText("ĐÚNG"); btnB.setText("SAI");
                btnA.setVisibility(View.VISIBLE); btnB.setVisibility(View.VISIBLE);
                btnC.setVisibility(View.GONE); btnD.setVisibility(View.GONE);
            } else {
                btnA.setText("A. " + q.getAnswerA()); btnB.setText("B. " + q.getAnswerB());
                btnC.setText("C. " + q.getAnswerC()); btnD.setText("D. " + q.getAnswerD());
                btnA.setVisibility(View.VISIBLE); btnB.setVisibility(View.VISIBLE);
                btnC.setVisibility(View.VISIBLE); btnD.setVisibility(View.VISIBLE);
                btnC.setEnabled(true); btnD.setEnabled(true);
            }
        } else {
            finishQuiz();
        }
    }

    private void startPerQuestionTimer() {
        if (timerPerQuestion != null) timerPerQuestion.cancel();
        timerPerQuestion = new CountDownTimer(valTimePerQ * 1000L, 1000) {
            @Override
            public void onTick(long l) { tvTimer.setText(l/1000 + "s"); }
            @Override
            public void onFinish() { handleTimeOut(); }
        }.start();
    }

    private void startTotalTimer() {
        timerTotal = new CountDownTimer(valTotalTime * 60 * 1000L, 1000) {
            @Override
            public void onTick(long l) {
                long min = (l/1000)/60; long sec = (l/1000)%60;
                tvTimer.setText(min + ":" + sec);
            }
            @Override
            public void onFinish() { finishQuiz(); }
        }.start();
    }

    private void handleTimeOut() {
        disableButtons();
        Question q = questionList.get(currentQuestionIndex);
        showCorrectAnswer(q.getCorrectAnswer());
        playSound(mpWrong);
        new Handler().postDelayed(() -> {
            if (!isFinishing()) { currentQuestionIndex++; loadQuestion(currentQuestionIndex); }
        }, 1500);
    }

    @Override
    public void onClick(View v) {
        if (isTimePerQOn && timerPerQuestion != null) timerPerQuestion.cancel();
        disableButtons();
        Button btn = (Button) v;
        String choice = "";
        if(btn.getId() == R.id.btnOptionA) choice = "A";
        else if(btn.getId() == R.id.btnOptionB) choice = "B";
        else if(btn.getId() == R.id.btnOptionC) choice = "C";
        else if(btn.getId() == R.id.btnOptionD) choice = "D";

        checkAnswer(choice, btn);
    }

    private void checkAnswer(String userChoice, Button btn) {
        Question q = questionList.get(currentQuestionIndex);
        if (userChoice.equalsIgnoreCase(q.getCorrectAnswer())) {
            score++;
            btn.setBackgroundColor(Color.parseColor("#00E676"));
            playSound(mpCorrect);
        } else {
            btn.setBackgroundColor(Color.parseColor("#FF3D71"));
            showCorrectAnswer(q.getCorrectAnswer());
            playSound(mpWrong);
        }
        new Handler().postDelayed(() -> {
            if (!isFinishing()) { currentQuestionIndex++; loadQuestion(currentQuestionIndex); }
        }, 1000);
    }

    private void playSound(MediaPlayer mp) {
        if (isSFXOn && mp != null) {
            try {
                if (mp.isPlaying()) mp.seekTo(0);
                mp.start();
            } catch (Exception e) {}
        }
    }

    private void showCorrectAnswer(String correct) {
        int color = Color.parseColor("#00E676");
        if(correct.equalsIgnoreCase("A")) btnA.setBackgroundColor(color);
        else if(correct.equalsIgnoreCase("B")) btnB.setBackgroundColor(color);
        else if(correct.equalsIgnoreCase("C")) btnC.setBackgroundColor(color);
        else if(correct.equalsIgnoreCase("D")) btnD.setBackgroundColor(color);
    }

    private void disableButtons() { btnA.setEnabled(false); btnB.setEnabled(false); btnC.setEnabled(false); btnD.setEnabled(false); }

    private void resetButtonColors() {
        int color = Color.parseColor("#2F3142");
        btnA.setBackgroundColor(color); btnB.setBackgroundColor(color);
        btnC.setBackgroundColor(color); btnD.setBackgroundColor(color);
    }

    private void finishQuiz() {
        if(timerPerQuestion!=null) timerPerQuestion.cancel();
        if(timerTotal!=null) timerTotal.cancel();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("QUIZ_ID", getIntent().getIntExtra("QUIZ_ID", 0));
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerPerQuestion != null) timerPerQuestion.cancel();
        if (timerTotal != null) timerTotal.cancel();

        if (mpCorrect != null) mpCorrect.release();
        if (mpWrong != null) mpWrong.release();

    }
}