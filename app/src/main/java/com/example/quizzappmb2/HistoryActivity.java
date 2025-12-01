package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity { // Giữ nguyên AppCompatActivity nếu BaseActivity bị lỗi

    private RecyclerView rcvHistory;
    private static final String API_KEY = "COPY_KEY_CUA_BAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rcvHistory = findViewById(R.id.rcvHistory);
        rcvHistory.setLayoutManager(new LinearLayoutManager(this));

        String targetUserId;

        if (getIntent().hasExtra("TARGET_USER_ID")) {
            targetUserId = getIntent().getStringExtra("TARGET_USER_ID");
            Toast.makeText(this, "Xem lịch sử người dùng khác...", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            targetUserId = prefs.getString("USER_ID", null);
        }
        // -------------------------------------------------------------

        if (targetUserId != null) {
            loadHistory(targetUserId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng để tải lịch sử.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadHistory(String userId) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        // Lọc theo user_id
        api.getHistory(API_KEY, "eq." + userId).enqueue(new Callback<List<HistoryItem>>() {
            @Override
            public void onResponse(Call<List<HistoryItem>> call, Response<List<HistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistoryItem> list = response.body();

                    if (list.isEmpty()) {
                        Toast.makeText(HistoryActivity.this, "User này chưa thi bài nào!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    HistoryAdapter adapter = new HistoryAdapter(list);
                    rcvHistory.setAdapter(adapter);
                } else {
                    Toast.makeText(HistoryActivity.this, "Lỗi tải lịch sử: Code " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistoryItem>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}