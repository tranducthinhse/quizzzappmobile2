package com.example.quizzappmb2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.Slider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateQuizActivity extends AppCompatActivity {

    private EditText edtTitle, edtDesc;
    private Spinner spinnerDiff;
    private Slider sliderDuration, sliderTotalQ;
    private TextView tvDurationVal, tvTotalQVal;
    private Button btnCreate;
    private ImageView btnBack;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";
    private String ADMIN_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);
        spinnerDiff = findViewById(R.id.spinnerDiff);
        sliderDuration = findViewById(R.id.sliderDuration);
        sliderTotalQ = findViewById(R.id.sliderTotalQ);
        tvDurationVal = findViewById(R.id.tvDurationVal);
        tvTotalQVal = findViewById(R.id.tvTotalQVal);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        ADMIN_TOKEN = prefs.getString("TOKEN", "");

        String[] diffs = {"Easy", "Medium", "Hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diffs);
        spinnerDiff.setAdapter(adapter);

        sliderDuration.addOnChangeListener((slider, value, fromUser) -> {
            tvDurationVal.setText((int)value + "m");
        });
        sliderTotalQ.addOnChangeListener((slider, value, fromUser) -> {
            tvTotalQVal.setText(String.valueOf((int)value));
        });

        btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createNewQuiz());
    }

    private void createNewQuiz() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String diff = spinnerDiff.getSelectedItem().toString();
        int duration = (int) sliderDuration.getValue();
        int totalQ = (int) sliderTotalQ.getValue();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ tên và mô tả!", Toast.LENGTH_SHORT).show();
            return;
        }


        Quiz newQuiz = new Quiz();
        newQuiz.title = title;
        newQuiz.description = desc;
        newQuiz.difficulty = diff;
        newQuiz.durationMinutes = duration;
        newQuiz.totalQuestions = totalQ;


        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.createQuiz(API_KEY, ADMIN_TOKEN, newQuiz).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateQuizActivity.this, "Tạo môn học thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateQuizActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateQuizActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}