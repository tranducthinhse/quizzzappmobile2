package com.example.quizzappmb2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rcvUsers;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";
    private String ADMIN_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        rcvUsers = findViewById(R.id.rcvUsers);
        rcvUsers.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        ADMIN_TOKEN = prefs.getString("TOKEN", "");

        if (ADMIN_TOKEN.isEmpty()) {
            Toast.makeText(this, "Phiên Admin hết hạn!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadAllUsers();
    }

    private void loadAllUsers() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getAllUsers(API_KEY).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Profile> list = response.body();

                    UserAdapter adapter = new UserAdapter(ManageUsersActivity.this, list, new UserAdapter.OnUserActionListener() {
                        @Override
                        public void onBan(Profile user) {
                            toggleBanUser(user);
                        }
                        @Override
                        public void onDelete(Profile user) {
                            confirmDeleteUser(user);
                        }
                        @Override
                        public void onHistory(Profile user) {
                            // Xem lịch sử của người này
                            Intent intent = new Intent(ManageUsersActivity.this, HistoryActivity.class);
                            intent.putExtra("TARGET_USER_ID", user.id);
                            startActivity(intent);
                        }
                    });
                    rcvUsers.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Lỗi tải dữ liệu! Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleBanUser(Profile user) {
        user.isBanned = !user.isBanned;
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        api.updateProfile(API_KEY, ADMIN_TOKEN, "eq." + user.id, user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String msg = user.isBanned ? "Đã CHẶN user!" : "Đã MỞ KHÓA user!";
                    Toast.makeText(ManageUsersActivity.this, msg, Toast.LENGTH_SHORT).show();
                    loadAllUsers();
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Lỗi cập nhật trạng thái (Quyền hạn)!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi mạng khi cập nhật!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteUser(Profile user) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo xóa!")
                .setMessage("Bạn có chắc muốn xóa vĩnh viễn user " + user.fullName + " không?")
                .setPositiveButton("XÓA NGAY", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(Profile user) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        api.deleteUser(API_KEY, ADMIN_TOKEN, "eq." + user.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageUsersActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                    loadAllUsers();
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Lỗi Server khi xóa!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}