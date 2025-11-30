package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalQuizzes, tvTotalQuestions, tvAvgScore;

    private CardView btnManageUsers, btnAddQuiz, btnAddQuestion;
    private Button btnLogout;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnManageUsers = findViewById(R.id.cardManageUsers);
        btnAddQuiz = findViewById(R.id.cardAddQuiz);
        btnAddQuestion = findViewById(R.id.cardAddQuestion);
        btnLogout = findViewById(R.id.btnLogout);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvAvgScore = findViewById(R.id.tvAvgScore);

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageUsersActivity.class));
        });
        btnAddQuiz.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, CreateQuizActivity.class));
        });
        btnAddQuestion.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddQuestionActivity.class));
        });
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });

        loadAdminStatistics();
    }

    private void loadAdminStatistics() {
        // Lấy Token Admin
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        // Gọi hàm RPC get_admin_stats
        api.getAdminStats(API_KEY, token).enqueue(new Callback<List<AdminStats>>() {
            @Override
            public void onResponse(Call<List<AdminStats>> call, Response<List<AdminStats>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    AdminStats stats = response.body().get(0);

                    // Đổ dữ liệu vào UI
                    tvTotalUsers.setText(String.valueOf(stats.totalUsers));
                    tvTotalQuizzes.setText(String.valueOf(stats.totalQuizzes));
                    tvTotalQuestions.setText(String.valueOf(stats.totalQuestions));

                    String avg = String.format(Locale.getDefault(), "%.1f", stats.avgScore);
                    tvAvgScore.setText(avg);
                } else {
                    Toast.makeText(AdminActivity.this, "Lỗi tải thống kê: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminStats>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}