package com.example.quizzappmb2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText edtContent, edtA, edtB, edtC, edtD, edtCorrect;
    private Spinner spinnerQuiz, spinnerType;
    private Button btnSave, btnDelete;
    private ImageView btnCancelEdit;
    private LinearLayout layoutOptionsCD;

    private RecyclerView rcvQuestions;
    private QuestionAdminAdapter listAdapter;

    private List<Quiz> quizList = new ArrayList<>();
    private int selectedQuizId = -1;
    private String selectedType = "MC";
    private int editingQuestionId = -1;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        edtContent = findViewById(R.id.edtContent);
        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        edtC = findViewById(R.id.edtC);
        edtD = findViewById(R.id.edtD);
        edtCorrect = findViewById(R.id.edtCorrect);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancelEdit = findViewById(R.id.btnCancelEdit); // Nút X/Reset

        spinnerQuiz = findViewById(R.id.spinnerQuiz);
        spinnerType = findViewById(R.id.spinnerType);
        layoutOptionsCD = findViewById(R.id.layoutOptionsCD);

        rcvQuestions = findViewById(R.id.rcvQuestions);
        rcvQuestions.setLayoutManager(new LinearLayoutManager(this));

        // 2. Gọi các hàm cài đặt
        setupTypeSpinner();
        loadQuizzes();

        // 3. CÁC NÚT BẤM (Đã nằm trong Thanh công cụ dưới cùng)
        btnSave.setOnClickListener(v -> handleSave());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnCancelEdit.setOnClickListener(v -> resetForm()); // Nút Reset (Xóa trắng Form)
    }

    // --- CÁC HÀM XỬ LÝ (GIỮ NGUYÊN) ---

    private void setupTypeSpinner() {
        String[] types = {"Trắc nghiệm (MC)", "Đúng / Sai (TF)"};

        // SỬ DỤNG LAYOUT MẶC ĐỊNH CỦA ANDROID
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner_white, types); // <-- LAYOUT 1 (Chữ đang hiển thị)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Logic ẩn hiện dòng C và D
                if (position == 0) {
                    selectedType = "MC";
                    layoutOptionsCD.setVisibility(View.VISIBLE);
                } else {
                    selectedType = "TF";
                    layoutOptionsCD.setVisibility(View.GONE);
                    edtC.setText("");
                    edtD.setText("");
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadQuizzes() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        api.getQuizzes(API_KEY).enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizList = response.body();

                    if (quizList.isEmpty()) {
                        Toast.makeText(AddQuestionActivity.this, "Không có đề thi nào trong Database.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<String> titles = new ArrayList<>();
                    for (Quiz q : quizList) titles.add(q.title);

                    // SỬ DỤNG LAYOUT MẶC ĐỊNH CỦA ANDROID
                    ArrayAdapter<String> quizAdapter = new ArrayAdapter<>(AddQuestionActivity.this, R.layout.item_spinner_white, titles); // <-- LAYOUT 1
                    quizAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                    spinnerQuiz.setAdapter(quizAdapter);

                    spinnerQuiz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedQuizId = quizList.get(position).id;
                            loadQuestionsByQuiz(selectedQuizId);
                        }
                        @Override public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }
            @Override public void onFailure(Call<List<Quiz>> call, Throwable t) {
                Toast.makeText(AddQuestionActivity.this, "Lỗi tải đề thi! Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByQuiz(int quizId) {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getQuestionsByQuizId(API_KEY, "eq." + quizId).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Question> list = response.body();
                    listAdapter = new QuestionAdminAdapter(list, question -> {
                        fillDataToForm(question);
                    });
                    rcvQuestions.setAdapter(listAdapter);
                }
            }
            @Override public void onFailure(Call<List<Question>> call, Throwable t) {}
        });
    }

    private void fillDataToForm(Question q) {
        editingQuestionId = q.getId();

        edtContent.setText(q.getContent());
        edtA.setText(q.getAnswerA());
        edtB.setText(q.getAnswerB());
        edtC.setText(q.getAnswerC());
        edtD.setText(q.getAnswerD());
        edtCorrect.setText(q.getCorrectAnswer());

        btnSave.setText("CẬP NHẬT");
        btnDelete.setVisibility(View.VISIBLE);
        btnCancelEdit.setVisibility(View.VISIBLE);

        if ("TF".equals(q.getType())) spinnerType.setSelection(1);
        else spinnerType.setSelection(0);
    }

    private void resetForm() {
        editingQuestionId = -1;
        edtContent.setText("");
        edtA.setText(""); edtB.setText(""); edtC.setText(""); edtD.setText("");
        edtCorrect.setText("");

        btnSave.setText("THÊM MỚI");
        btnDelete.setVisibility(View.GONE);
        btnCancelEdit.setVisibility(View.GONE); // Ẩn nút Hủy
    }

    private void handleSave() {
        String content = edtContent.getText().toString();
        String a = edtA.getText().toString();
        String b = edtB.getText().toString();
        String correct = edtCorrect.getText().toString().toUpperCase();

        if(content.isEmpty() || a.isEmpty() || correct.isEmpty()) {
            Toast.makeText(this, "Nhập thiếu thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Question q = new Question(content, a, b, edtC.getText().toString(), edtD.getText().toString(), correct);
        q.setQuizId(selectedQuizId);
        q.setType(selectedType);

        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);

        if (editingQuestionId == -1) {
            // THÊM MỚI
            api.addQuestion(API_KEY, "Bearer " + API_KEY, q).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(AddQuestionActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        resetForm();
                        loadQuestionsByQuiz(selectedQuizId);
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        } else {
            // CẬP NHẬT (UPDATE)
            api.updateQuestion(API_KEY, "Bearer " + API_KEY, "eq." + editingQuestionId, q).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(AddQuestionActivity.this, "Cập nhật xong!", Toast.LENGTH_SHORT).show();
                        resetForm();
                        loadQuestionsByQuiz(selectedQuizId);
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa câu hỏi?")
                .setMessage("Bạn chắc chắn muốn xóa câu này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
                    api.deleteQuestion(API_KEY, "Bearer " + API_KEY, "eq." + editingQuestionId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                Toast.makeText(AddQuestionActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                                resetForm();
                                loadQuestionsByQuiz(selectedQuizId);
                            }
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}