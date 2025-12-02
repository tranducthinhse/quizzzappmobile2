package com.example.quizzappmb2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtId, edtName, edtPhone, edtAddress;
    private TextView tvDob;
    private RadioGroup radioGroupGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave, btnLogout;
    private ImageView imgAvatar;
    private ImageView imgIconSoundMaster;

    private SwitchMaterial switchTotalTime, switchTimePerQ, switchSoundMaster; // KHÔNG CẦN switchMusic, switchSFX
    private Slider sliderTimePerQ, sliderTotalTime, sliderCount;
    private TextView tvTimePerQVal, tvTotalTimeVal, tvCountVal, tvSoundStatus;

    private String USER_ID, TOKEN;
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";
    private static final String STORAGE_URL = "https://hyueubinwueddmixxqyk.supabase.co/storage/v1/object/public/avatars/";

    private ActivityResultLauncher<String> pickImageLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        tvDob = findViewById(R.id.tvDob);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);

        switchTimePerQ = findViewById(R.id.switchTimePerQ);
        switchTotalTime = findViewById(R.id.switchTotalTime);
        switchSoundMaster = findViewById(R.id.switchSoundMaster);

        sliderTimePerQ = findViewById(R.id.sliderTimePerQ);
        sliderTotalTime = findViewById(R.id.sliderTotalTime);
        sliderCount = findViewById(R.id.sliderCount);
        imgIconSoundMaster = findViewById(R.id.imgIconSoundMaster);

        tvTimePerQVal = findViewById(R.id.tvTimePerQVal);
        tvTotalTimeVal = findViewById(R.id.tvTotalTimeVal);
        tvCountVal = findViewById(R.id.tvCountVal);
        tvSoundStatus = findViewById(R.id.tvSoundStatus);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        USER_ID = prefs.getString("USER_ID", null);
        TOKEN = prefs.getString("TOKEN", null);

        if (USER_ID == null || TOKEN == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        edtId.setText(USER_ID);

        loadUserProfile();
        loadSettings();


        sliderTimePerQ.addOnChangeListener((slider, value, fromUser) -> tvTimePerQVal.setText((int)value + "s"));
        sliderTotalTime.addOnChangeListener((slider, value, fromUser) -> tvTotalTimeVal.setText((int)value + "m"));
        sliderCount.addOnChangeListener((slider, value, fromUser) -> tvCountVal.setText(String.valueOf((int)value)));
        switchTimePerQ.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sliderTimePerQ.setEnabled(isChecked);
            tvTimePerQVal.setTextColor(isChecked ? Color.parseColor("#00D2FC") : Color.GRAY);
        });

        switchTotalTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sliderTotalTime.setEnabled(isChecked);
            tvTotalTimeVal.setTextColor(isChecked ? Color.parseColor("#FF3D71") : Color.GRAY);
        });

        switchSoundMaster.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("GameSettings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("MUSIC", isChecked)
                    .putBoolean("SFX", isChecked)
                    .apply();
            MusicManager.updateMusicState(getApplicationContext(), isChecked);

            updateMasterSoundUI(isChecked);
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                imgAvatar.setImageURI(uri);
                uploadImageToSupabase();
            }
        });
        imgAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        tvDob.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            saveUserProfile();
            saveSettingsLocal();
            Toast.makeText(this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            MusicManager.stop();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateMasterSoundUI(boolean isOn) {
        int thumbColor = isOn ? Color.parseColor("#00D2FC") : Color.parseColor("#9E9EA7");
        int iconColor = isOn ? Color.parseColor("#00D2FC") : Color.parseColor("#E0E0E0");

        String textStatus = isOn ? "SOUND IS ON" : "MUTED";
        int textStatusColor = isOn ? Color.WHITE : Color.GRAY;

        switchSoundMaster.setThumbTintList(ColorStateList.valueOf(thumbColor));

        imgIconSoundMaster.setImageResource(isOn ? android.R.drawable.ic_lock_silent_mode_off : android.R.drawable.ic_lock_silent_mode);
        imgIconSoundMaster.setColorFilter(iconColor);

        tvSoundStatus.setText(textStatus);
        tvSoundStatus.setTextColor(textStatusColor);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);

        boolean isTimePerQOn = prefs.getBoolean("IS_TIME_PER_Q", true);
        boolean isTotalTimeOn = prefs.getBoolean("IS_TOTAL_TIME", false);
        boolean isSoundOn = prefs.getBoolean("MUSIC", true);

        switchTimePerQ.setChecked(isTimePerQOn);
        switchTotalTime.setChecked(isTotalTimeOn);
        switchSoundMaster.setChecked(isSoundOn);

        updateMasterSoundUI(isSoundOn);

        float valTimePerQ = (float) prefs.getInt("VAL_TIME_PER_Q", 10);
        float valTotalTime = (float) prefs.getInt("VAL_TOTAL_TIME", 5);
        float valCount = (float) prefs.getInt("VAL_QUEST_COUNT", 10);

        sliderTimePerQ.setValue(valTimePerQ);
        sliderTotalTime.setValue(valTotalTime);
        sliderCount.setValue(valCount);

        tvTimePerQVal.setText((int)valTimePerQ + "s");
        tvTotalTimeVal.setText((int)valTotalTime + "m");
        tvCountVal.setText(String.valueOf((int)valCount));

        sliderTimePerQ.setEnabled(isTimePerQOn);
        sliderTotalTime.setEnabled(isTotalTimeOn);
    }

    private void saveSettingsLocal() {
        SharedPreferences.Editor editor = getSharedPreferences("GameSettings", MODE_PRIVATE).edit();

        editor.putBoolean("IS_TIME_PER_Q", switchTimePerQ.isChecked());
        editor.putBoolean("IS_TOTAL_TIME", switchTotalTime.isChecked());
        // Sound đã lưu riêng ở sự kiện change

        editor.putInt("VAL_TIME_PER_Q", (int) sliderTimePerQ.getValue());
        editor.putInt("VAL_TOTAL_TIME", (int) sliderTotalTime.getValue());
        editor.putInt("VAL_QUEST_COUNT", (int) sliderCount.getValue());

        editor.apply();
    }

    // --- CÁC HÀM CŨ (Upload ảnh, Load Profile...) GIỮ NGUYÊN ---

    private void uploadImageToSupabase() {
        if (selectedImageUri == null) return;
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] bytes = getBytes(inputStream);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            String fileName = USER_ID + "_" + System.currentTimeMillis() + ".jpg";
            SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
            Toast.makeText(this, "Đang tải ảnh...", Toast.LENGTH_SHORT).show();
            api.uploadAvatar(API_KEY, TOKEN, "image/jpeg", fileName, requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String publicUrl = STORAGE_URL + fileName;
                        updateAvatarLinkInDatabase(publicUrl);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateAvatarLinkInDatabase(String url) {
        Profile p = new Profile();
        p.avatarUrl = url;
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.updateProfile(API_KEY, TOKEN, "eq." + USER_ID, p).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) Toast.makeText(ProfileActivity.this, "Đổi Avatar xong!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void loadUserProfile() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getProfile(API_KEY, TOKEN, "eq." + USER_ID).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile p = response.body().get(0);
                    if (p.fullName != null) edtName.setText(p.fullName);
                    if (p.phone != null) edtPhone.setText(p.phone);
                    if (p.address != null) edtAddress.setText(p.address);
                    if (p.dob != null) tvDob.setText(p.dob);
                    if ("Nam".equals(p.gender)) rbMale.setChecked(true);
                    else if ("Nữ".equals(p.gender)) rbFemale.setChecked(true);
                    if (p.avatarUrl != null && !p.avatarUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(p.avatarUrl)
                                .placeholder(android.R.drawable.sym_def_app_icon)
                                .error(android.R.drawable.stat_notify_error)
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {}
        });
    }

    private void saveUserProfile() {
        Profile p = new Profile();
        p.fullName = edtName.getText().toString();
        p.phone = edtPhone.getText().toString();
        p.address = edtAddress.getText().toString();
        String dobText = tvDob.getText().toString();
        p.dob = dobText.equals("Select Date") ? null : dobText;
        if (rbMale.isChecked()) p.gender = "Nam";
        else if (rbFemale.isChecked()) p.gender = "Nữ";
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.updateProfile(API_KEY, TOKEN, "eq." + USER_ID, p).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) Toast.makeText(ProfileActivity.this, "Đã lưu thông tin & Cài đặt!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(ProfileActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            tvDob.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}