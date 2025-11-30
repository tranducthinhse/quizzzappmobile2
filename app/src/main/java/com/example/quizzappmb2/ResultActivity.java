package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    private TextView tvScore, tvFraction, tvMessage, tvTitle;
    private Button btnReplay, btnHome;

    private static final String API_KEY = "COPY_KEY_CUA_BAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvScore = findViewById(R.id.tvScore);
        tvFraction = findViewById(R.id.tvFraction);
        tvMessage = findViewById(R.id.tvMessage);
        tvTitle = findViewById(R.id.tvResultTitle);
        btnReplay = findViewById(R.id.btnReplay);
        btnHome = findViewById(R.id.btnHome);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        int quizId = getIntent().getIntExtra("QUIZ_ID", 0);

        showResultUI(score, total);

        saveResultToServer(quizId, score, total);

        btnReplay.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, PlayQuizActivity.class);
            intent.putExtra("QUIZ_ID", quizId);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, UserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showResultUI(int score, int total) {
        tvFraction.setText(score + "/" + total);

        int percentage = (total > 0) ? (score * 100) / total : 0;
        tvScore.setText(percentage + "%");

        if (percentage == 100) {
            tvMessage.setText("Legendary");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#FFD700")); // Vàng
        } else if (percentage >= 80) {
            tvMessage.setText("Expert");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#00E676")); // Xanh
        } else if (percentage >= 50) {
            tvMessage.setText("Junior");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#00D2FC")); // Cyan
        } else {
            tvMessage.setText("Newbie");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#FF3D71")); // Đỏ
        }
    }

    private void saveResultToServer(int quizId, int score, int total) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        String token = prefs.getString("TOKEN", null);

        if (userId != null && token != null) {
            Result result = new Result(userId, quizId, score, total);

            SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
            api.saveResult(API_KEY, token, result).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ResultActivity.this, "Đã lưu kết quả!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    }
            });
        }
    }
}