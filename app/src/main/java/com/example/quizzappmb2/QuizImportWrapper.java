package com.example.quizzappmb2;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class QuizImportWrapper {
    // Các trường thông tin đề thi (Map với file JSON)
    @SerializedName("title")
    public String title = "";

    @SerializedName("description")
    public String description = "";

    @SerializedName("difficulty")
    public String difficulty = "Medium"; // Mặc định

    @SerializedName("duration_minutes")
    public int durationMinutes = 15; // Mặc định

    @SerializedName("total_questions")
    public int totalQuestions = 0;

    // Danh sách câu hỏi đi kèm
    @SerializedName("questions")
    public List<Question> questions = new ArrayList<>();
}