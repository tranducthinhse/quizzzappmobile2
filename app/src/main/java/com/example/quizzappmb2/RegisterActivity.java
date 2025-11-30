package com.example.quizzappmb2; // Nhớ check đúng package của bạn

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass, edtConfirmPass;
    private Button btnRegister;
    private TextView tvLoginLink;

    private static final String API_KEY = "COPY_KEY_ANON_CUA_BAN_VAO_DAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edittext_username);
        edtPass = findViewById(R.id.edittext_Password);
        edtConfirmPass = findViewById(R.id.edittext_Confirmation);
        btnRegister = findViewById(R.id.button_create_account);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            String confirm = edtConfirmPass.getText().toString().trim();

            if (validateInput(email, pass, confirm)) {
                performSignUp(email, pass);
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean validateInput(String email, String pass, String confirm) {
        if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performSignUp(String email, String pass) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang tạo...");

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        api.signUp(API_KEY, new LoginRequest(email, pass)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Email có thể đã tồn tại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");
                Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}