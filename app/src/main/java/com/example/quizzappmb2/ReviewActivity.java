package com.example.quizzappmb2;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView rcvReview;
    private TextView tvHeader;

    private static final String API_KEY = "COPY_KEY_CUA_BAN";
    private String USER_TOKEN = "COPY_TOKEN_TU_SHAREDPREFS"; // Token cần để xác thực

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        rcvReview = findViewById(R.id.rcvReviewList);
        tvHeader = findViewById(R.id.tvReviewHeader);
        rcvReview.setLayoutManager(new LinearLayoutManager(this));

        // 1. Lấy result_id từ màn hình History gửi sang
        int resultId = getIntent().getIntExtra("RESULT_ID", -1);

        if (resultId != -1) {
            tvHeader.setText("ÔN TẬP PHIÊN THI #" + resultId);
            loadReviewDetails(resultId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID phiên thi!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadReviewDetails(int resultId) {
        // Lấy Token từ bộ nhớ máy (trong HistoryActivity, bạn cần truyền Token vào Intent hoặc lưu Token Admin)
        String token = "Bearer " + API_KEY; // Giả sử dùng API Key cho đơn giản. Nếu có Token thật thì dùng Token.

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        // Gọi API lấy View chi tiết, lọc theo Result ID
        api.getReviewDetails(API_KEY, token, "eq." + resultId).enqueue(new Callback<List<ReviewDetail>>() {
            @Override
            public void onResponse(Call<List<ReviewDetail>> call, Response<List<ReviewDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewAdapter adapter = new ReviewAdapter(ReviewActivity.this, response.body());
                    rcvReview.setAdapter(adapter);
                } else {
                    Toast.makeText(ReviewActivity.this, "Lỗi tải dữ liệu ôn tập: Code " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<ReviewDetail>> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}