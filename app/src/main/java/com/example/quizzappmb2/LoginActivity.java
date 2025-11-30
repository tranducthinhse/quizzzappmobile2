package com.example.quizzappmb2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private LinearLayout btnGoogle, btnEmailLogin;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Ánh xạ (Giữ nguyên)
        edtEmail = findViewById(R.id.edittext_username);
        edtPass = findViewById(R.id.edittext_password);
        btnLogin = findViewById(R.id.button_login);
        tvRegister = findViewById(R.id.textview_create_account);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 2. Nút Login
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(email, pass);
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        tvForgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });
    }

    private void performLogin(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang xử lý...");

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.login(API_KEY, new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userId = response.body().user.id;
                    String token = "Bearer " + response.body().accessToken;

                    getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("USER_ID", userId)
                            .putString("TOKEN", token)
                            .apply();

                    checkUserRole(api, token, userId);
                } else {
                    resetButton();
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                resetButton();
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRole(SupabaseApi api, String token, String userId) {
        String queryId = "eq." + userId;
        api.getProfile(API_KEY, token, queryId).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                resetButton();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile profile = response.body().get(0);

                    if (profile.isBanned) {
                        Toast.makeText(LoginActivity.this, "Do vi phạm nên tài khoản của bạn đã bị khóa ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String role = profile.role;
                    if ("admin".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, UserActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi hồ sơ người dùng! (Hãy thử đăng ký lại)", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                resetButton();
                Toast.makeText(LoginActivity.this, "Lỗi lấy quyền: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Khôi phục mật khẩu");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Gửi mail", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) { sendResetEmail(email); }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendResetEmail(String email) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.resetPassword(API_KEY, new LoginRequest(email, "")).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đã gửi mail! Hãy kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi: Email không tồn tại.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void resetButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Đăng nhập");
    }
}