package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private RecyclerView rcvQuiz;
    private QuizAdapter adapter;
    private ImageView imgAvatar;
    private ImageView btnLeaderboard;
    private TextView tvUserName, tvHello; // Khai báo thêm TextView tên

    // API KEY & Thông tin User
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";
    private String USER_ID, TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        rcvQuiz = findViewById(R.id.rcvQuiz);
        rcvQuiz.setHasFixedSize(true);
        imgAvatar = findViewById(R.id.imgUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvHello = findViewById(R.id.tvHello);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        rcvQuiz.setLayoutManager(new GridLayoutManager(this, 2));

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        USER_ID = prefs.getString("USER_ID", null);
        TOKEN = prefs.getString("TOKEN", null);

        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, LeaderboardActivity.class));
        });

        getQuizList();

        ImageView btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, HistoryActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (USER_ID != null && TOKEN != null) {
            getUserProfile();
        }
    }
    private void getUserProfile() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        String query = "eq." + USER_ID;

        api.getProfile(API_KEY, TOKEN, query).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile p = response.body().get(0);

                    if (p.fullName != null && !p.fullName.isEmpty()) {
                        tvUserName.setText(p.fullName);
                        tvHello.setText("Chào mừng trở lại,");
                    } else {
                        tvUserName.setText("Người chơi mới");
                        tvHello.setText("Vui lòng cập nhật hồ sơ!");
                    }

                    if (p.avatarUrl != null && !p.avatarUrl.isEmpty()) {
                        Glide.with(UserActivity.this)
                                .load(p.avatarUrl)
                                .placeholder(android.R.drawable.sym_def_app_icon)
                                .error(android.R.drawable.sym_def_app_icon)
                                .into(imgAvatar);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {

            }
        });
    }


    private void getQuizList() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getQuizzes(API_KEY).enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Quiz> list = response.body();
                    adapter = new QuizAdapter(UserActivity.this, list);
                    rcvQuiz.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Quiz>> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Lỗi lấy đề thi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showQuizDetailDialog(Quiz quiz) {
        // Tạo Dialog Trượt từ dưới lên
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_quiz_detail);

        // Ánh xạ View trong Dialog
        TextView tvTitle = dialog.findViewById(R.id.tvDetailTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDetailDesc);
        TextView tvQs = dialog.findViewById(R.id.tvDetailQs);
        TextView tvTime = dialog.findViewById(R.id.tvDetailTime);
        TextView tvDiff = dialog.findViewById(R.id.tvDetailDiff);
        Button btnStart = dialog.findViewById(R.id.btnStartNow);

        // Gán dữ liệu
        tvTitle.setText(quiz.title);
        tvDesc.setText(quiz.description);
        tvQs.setText(String.valueOf(quiz.totalQuestions));
        tvTime.setText(quiz.durationMinutes + "m");
        tvDiff.setText(quiz.difficulty);

        // Bắt sự kiện nút Start trong Dialog
        btnStart.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog

            // Chuyển sang màn hình chơi game
            Intent intent = new Intent(UserActivity.this, PlayQuizActivity.class);
            intent.putExtra("QUIZ_ID", quiz.id);
            intent.putExtra("QUIZ_TITLE", quiz.title);

            // Truyền thêm cài đặt mặc định của đề thi (nếu user không chỉnh trong Profile)
            intent.putExtra("DEFAULT_TIME", quiz.durationMinutes);
            intent.putExtra("DEFAULT_COUNT", quiz.totalQuestions);

            startActivity(intent);
        });
        dialog.show();
}
}