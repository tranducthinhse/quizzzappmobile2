package com.example.quizzappmb2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {

    private RecyclerView rcvHistory;
    private static final String API_KEY = "COPY_KEY_CUA_BAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rcvHistory = findViewById(R.id.rcvHistory);
        rcvHistory.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            loadHistory(userId);
        }
    }

    private void loadHistory(String userId) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        api.getHistory(API_KEY, "eq." + userId).enqueue(new Callback<List<HistoryItem>>() {
            @Override
            public void onResponse(Call<List<HistoryItem>> call, Response<List<HistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistoryAdapter adapter = new HistoryAdapter(response.body());
                    rcvHistory.setAdapter(adapter);
                } else {
                    Toast.makeText(HistoryActivity.this, "Chưa có lịch sử!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistoryItem>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}