package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends BaseActivity { // Kế thừa BaseActivity để giữ nhạc nền

    private TextView tvScore, tvFraction, tvMessage, tvTitle;
    private Button btnReplay, btnHome, btnReviewNow; // Khai báo nút Review

    // Biến lưu ID của phiên thi vừa tạo
    private Integer currentResultId = null;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. Ánh xạ View
        tvScore = findViewById(R.id.tvScore);
        tvFraction = findViewById(R.id.tvFraction);
        tvMessage = findViewById(R.id.tvMessage);
        tvTitle = findViewById(R.id.tvResultTitle);
        btnReplay = findViewById(R.id.btnReplay);
        btnHome = findViewById(R.id.btnHome);

        // Ánh xạ nút Review mới thêm trong XML
        btnReviewNow = findViewById(R.id.btnReviewNow);

        // 2. Lấy dữ liệu từ PlayQuizActivity gửi sang
        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        int quizId = getIntent().getIntExtra("QUIZ_ID", 0);

        // 3. Hiển thị UI (Điểm số, lời chúc)
        showResultUI(score, total);

        // 4. Lưu điểm lên Server (Đây là bước quan trọng để có ID Review)
        saveResultToServer(quizId, score, total);

        // 5. SỰ KIỆN NÚT BẤM

        // --- NÚT REVIEW (XỬ LÝ MỚI) ---
        btnReviewNow.setOnClickListener(v -> {
            if (currentResultId != null) {
                // Nếu đã có ID (đã lưu xong) -> Chuyển trang Review
                Intent intent = new Intent(ResultActivity.this, ReviewActivity.class);
                intent.putExtra("RESULT_ID", currentResultId);
                startActivity(intent);
            } else {
                // Nếu chưa lưu xong mà bấm vội
                Toast.makeText(ResultActivity.this, "Đang lưu kết quả, vui lòng đợi giây lát...", Toast.LENGTH_SHORT).show();
            }
        });

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
            tvMessage.setText("Huyền Thoại!");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#FFD700"));
        } else if (percentage >= 80) {
            tvMessage.setText("Xuất Sắc!");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#00E676"));
        } else if (percentage >= 50) {
            tvMessage.setText("Khá Tốt");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#00D2FC"));
        } else {
            tvMessage.setText("Cố gắng lần sau");
            tvMessage.setTextColor(android.graphics.Color.parseColor("#FF3D71"));
        }
    }

    private void saveResultToServer(int quizId, int score, int total) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        String token = prefs.getString("TOKEN", null);

        // Lấy danh sách chi tiết từ bộ nhớ tạm (GlobalCache)
        List<UserAnswer> detailedAnswers = GlobalAnswerCache.getAnswers();

        if (userId != null && token != null) {
            Result result = new Result(userId, quizId, score, total);
            SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

            // Tạm thời khóa nút Review và hiện thông báo đang lưu
            btnReviewNow.setEnabled(false);
            btnReviewNow.setText("SAVING DATA...");

            // GỌI API LƯU KẾT QUẢ
            api.saveResult(API_KEY, token, result).enqueue(new Callback<List<Result>>() {
                @Override
                public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                        // 1. LẤY ĐƯỢC ID VỪA TẠO
                        currentResultId = response.body().get(0).id;

                        // 2. MỞ KHÓA NÚT REVIEW NGAY
                        btnReviewNow.setEnabled(true);
                        btnReviewNow.setText("REVIEW ANSWERS");

                        // 3. TIẾP TỤC LƯU CHI TIẾT (CHẠY NGẦM)
                        if (currentResultId != null && !detailedAnswers.isEmpty()) {
                            for (UserAnswer answer : detailedAnswers) {
                                answer.resultId = currentResultId;
                            }
                            saveDetailedAnswers(currentResultId, detailedAnswers, api, token);
                        }
                    } else {
                        Toast.makeText(ResultActivity.this, "Lỗi lưu điểm!", Toast.LENGTH_SHORT).show();
                        btnReviewNow.setText("SAVE FAILED");
                    }
                    // Dọn dẹp bộ nhớ tạm
                    GlobalAnswerCache.clearAnswers();
                }
                @Override
                public void onFailure(Call<List<Result>> call, Throwable t) {
                    Toast.makeText(ResultActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    btnReviewNow.setText("OFFLINE MODE"); // Hoặc xử lý khác
                }
            });
        }
    }

    private void saveDetailedAnswers(Integer resultId, List<UserAnswer> answers, SupabaseApi api, String token) {
        api.saveUserAnswers(API_KEY, token, answers).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("RESULT_SAVE", "Lỗi lưu chi tiết: " + response.code());
                }
                // Lưu xong chi tiết thì người dùng bấm vào Review mới thấy dữ liệu
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}