package com.example.quizzappmb2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportQuizActivity extends AppCompatActivity {

    private Button btnSelectFile, btnConfirmUpload;
    private TextView tvFileName, tvQuizTitle, tvQuizDesc, tvQuizStats, tvListHeader;
    private CardView cardQuizInfo;
    private RecyclerView rcvPreview;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";
    private String ADMIN_TOKEN;

    // Biến lưu dữ liệu tạm sau khi đọc file
    private QuizImportWrapper pendingData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_quiz);

        // Cấu hình fix lỗi POI
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        // Ánh xạ
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnConfirmUpload = findViewById(R.id.btnConfirmUpload);
        tvFileName = findViewById(R.id.tvFileName);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuizDesc = findViewById(R.id.tvQuizDesc);
        tvQuizStats = findViewById(R.id.tvQuizStats);
        tvListHeader = findViewById(R.id.tvListHeader);
        cardQuizInfo = findViewById(R.id.cardQuizInfo);
        rcvPreview = findViewById(R.id.rcvPreview);

        rcvPreview.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        ADMIN_TOKEN = prefs.getString("TOKEN", "");

        // Bộ chọn file
        ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            tvFileName.setText(uri.getPath());
                            processFileInBackground(uri);
                        }
                    }
                });

        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"application/json", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(intent);
        });

        // Nút Xác nhận Upload (Chỉ hoạt động khi đã có pendingData)
        btnConfirmUpload.setOnClickListener(v -> {
            if (pendingData != null) {
                uploadQuiz(pendingData);
            } else {
                Toast.makeText(this, "Chưa có dữ liệu để upload!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processFileInBackground(Uri uri) {
        Toast.makeText(this, "Đang đọc file...", Toast.LENGTH_SHORT).show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                QuizImportWrapper data = null;
                try {
                    data = parseDocx(uri);
                } catch (Exception e) {
                    try {
                        data = parseJson(uri);
                    } catch (Exception ex) {
                        throw new Exception("Lỗi định dạng file!");
                    }
                }

                QuizImportWrapper finalData = data;
                mainHandler.post(() -> {
                    if (finalData != null && finalData.questions != null && !finalData.questions.isEmpty()) {
                        // HIỂN THỊ DỮ LIỆU ĐỂ KIỂM TRA (PREVIEW)
                        showPreview(finalData);
                    } else {
                        Toast.makeText(ImportQuizActivity.this, "File rỗng hoặc sai mẫu!", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(ImportQuizActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    // --- HÀM HIỂN THỊ PREVIEW ---
    // --- HÀM HIỂN THỊ PREVIEW (ĐÃ SỬA) ---
    private void showPreview(QuizImportWrapper data) {
        this.pendingData = data; // Lưu lại để chờ upload

        // 1. Hiện thông tin đề thi
        cardQuizInfo.setVisibility(View.VISIBLE);
        tvQuizTitle.setText(data.title);
        tvQuizDesc.setText(data.description);
        tvQuizStats.setText("Độ khó: " + data.difficulty + " | " + data.durationMinutes + " phút");

        // 2. Hiện danh sách câu hỏi
        rcvPreview.setVisibility(View.VISIBLE);
        tvListHeader.setVisibility(View.VISIBLE);
        tvListHeader.setText("DANH SÁCH CÂU HỎI (" + data.questions.size() + ")");

        // SỬA LẠI CHỖ NÀY: Chỉ dùng 1 Adapter thôi
        ImportPreviewAdapter adapter = new ImportPreviewAdapter(data.questions);
        rcvPreview.setAdapter(adapter);

        // 3. Hiện nút Xác nhận
        btnConfirmUpload.setVisibility(View.VISIBLE);
    }

    // --- CÁC HÀM PARSE (Giữ nguyên) ---
    private QuizImportWrapper parseDocx(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        XWPFDocument document = new XWPFDocument(inputStream);
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        QuizImportWrapper wrapper = new QuizImportWrapper();
        wrapper.questions = new ArrayList<>();
        Question currentQ = null;

        for (XWPFParagraph para : paragraphs) {
            String text = para.getText().trim();
            if (text.isEmpty()) continue;

            if (text.startsWith("Title:")) wrapper.title = getValue(text);
            else if (text.startsWith("Desc:")) wrapper.description = getValue(text);
            else if (text.startsWith("Level:")) wrapper.difficulty = getValue(text);
            else if (text.startsWith("Time:")) wrapper.durationMinutes = Integer.parseInt(getValue(text));

            else if (text.startsWith("---")) {
                if (currentQ != null) wrapper.questions.add(currentQ);
                currentQ = new Question();
            }
            else if (currentQ != null) {
                if (text.startsWith("Q:")) currentQ.setContent(getValue(text));
                else if (text.startsWith("A:")) currentQ.setAnswerA(getValue(text));
                else if (text.startsWith("B:")) currentQ.setAnswerB(getValue(text));
                else if (text.startsWith("C:")) currentQ.setAnswerC(getValue(text));
                else if (text.startsWith("D:")) currentQ.setAnswerD(getValue(text));
                else if (text.startsWith("Correct:")) currentQ.setCorrectAnswer(getValue(text));
                else if (text.startsWith("Type:")) currentQ.setType(getValue(text));
                else if (text.startsWith("Explain:")) currentQ.setExplanation(getValue(text));
            }
        }
        if (currentQ != null) wrapper.questions.add(currentQ);
        wrapper.totalQuestions = wrapper.questions.size();
        document.close();
        return wrapper;
    }

    private String getValue(String text) {
        if (text.contains(":")) return text.substring(text.indexOf(":") + 1).trim();
        return "";
    }

    private QuizImportWrapper parseJson(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return new Gson().fromJson(sb.toString(), QuizImportWrapper.class);
    }

    // --- CÁC HÀM UPLOAD (Giữ nguyên logic, chỉ đổi chỗ gọi) ---
    private void uploadQuiz(QuizImportWrapper data) {
        Toast.makeText(this, "Đang tải lên Server...", Toast.LENGTH_SHORT).show();
        btnConfirmUpload.setEnabled(false);
        btnConfirmUpload.setText("ĐANG UPLOAD...");

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        Quiz quiz = new Quiz();
        quiz.title = data.title;
        quiz.description = data.description;
        quiz.difficulty = data.difficulty;
        quiz.durationMinutes = data.durationMinutes;
        quiz.totalQuestions = data.questions.size();

        api.createQuizAndReturn(API_KEY, ADMIN_TOKEN, quiz).enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Integer newQuizId = response.body().get(0).id;
                    uploadQuestions(newQuizId, data.questions, api);
                } else {
                    Toast.makeText(ImportQuizActivity.this, "Lỗi tạo Quiz: " + response.code(), Toast.LENGTH_LONG).show();
                    btnConfirmUpload.setEnabled(true);
                    btnConfirmUpload.setText("THỬ LẠI");
                }
            }
            @Override public void onFailure(Call<List<Quiz>> call, Throwable t) {
                Toast.makeText(ImportQuizActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                btnConfirmUpload.setEnabled(true);
            }
        });
    }

    private void uploadQuestions(Integer quizId, List<Question> questions, SupabaseApi api) {
        for (Question q : questions) q.setQuizId(quizId);

        api.addQuestionsBulk(API_KEY, ADMIN_TOKEN, questions).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ImportQuizActivity.this, "IMPORT THÀNH CÔNG!", Toast.LENGTH_LONG).show();
                    finish(); // Đóng màn hình khi xong
                } else {
                    Toast.makeText(ImportQuizActivity.this, "Lỗi thêm câu hỏi: " + response.code(), Toast.LENGTH_LONG).show();
                    btnConfirmUpload.setEnabled(true);
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ImportQuizActivity.this, "Lỗi mạng câu hỏi!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}