package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private TextView tvStatus;
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvStatus = findViewById(R.id.tvStatus);

        // Khởi tạo nhạc nền (để sẵn sàng phát ở màn hình sau)
        MusicManager.initialize(getApplicationContext());

        // Giả vờ loading 1.5 giây cho user kịp nhìn Logo
        new Handler().postDelayed(() -> {
            checkAutoLogin();
        }, 1500);
    }

    private void checkAutoLogin() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        String token = prefs.getString("TOKEN", null);

        if (userId != null && token != null) {
            // Có Token -> Kiểm tra với Server
            tvStatus.setText("Đang xác thực tài khoản...");
            validateTokenWithServer(token, userId);
        } else {
            // Chưa từng đăng nhập
            tvStatus.setText("Chưa đăng nhập...");
            goToLogin();
        }
    }

    private void validateTokenWithServer(String token, String userId) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        // Gọi API lấy Profile. Nếu Token sai/hết hạn, API này sẽ trả về lỗi 401
        api.getProfile(API_KEY, token, "eq." + userId).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // 1. Token Hợp lệ -> Vào App
                    Profile profile = response.body().get(0);
                    tvStatus.setText("Xin chào, " + profile.fullName);

                    // Kiểm tra Ban
                    if (profile.isBanned) {
                        Toast.makeText(SplashActivity.this, "Tài khoản đã bị KHÓA!", Toast.LENGTH_LONG).show();
                        goToLogin();
                    } else {
                        redirectUser(profile.role);
                    }
                } else {
                    // 2. Token Hết hạn hoặc Lỗi -> Bắt đăng nhập lại
                    tvStatus.setText("Phiên đăng nhập hết hạn");
                    Toast.makeText(SplashActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();

                    // Xóa Token cũ đi cho sạch
                    getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
                    goToLogin();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                // Lỗi mạng -> Có thể cho vào Offline Mode hoặc về Login
                tvStatus.setText("Lỗi kết nối mạng!");
                Toast.makeText(SplashActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        });
    }

    private void redirectUser(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(SplashActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, UserActivity.class);
        }
        startActivity(intent);
        finish(); // Đóng Splash để không back lại được
    }

    private void goToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}