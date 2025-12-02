package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class ReviewDetail {
    // Nội dung câu hỏi gốc
    @SerializedName("question_content") public String questionContent;
    @SerializedName("answer_a") public String answerA;
    @SerializedName("answer_b") public String answerB;
    @SerializedName("answer_c") public String answerC;
    @SerializedName("answer_d") public String answerD;
    @SerializedName("correct_answer") public String correctAnswer;
    @SerializedName("type") public String type;

    // Thông tin trả lời của User
    @SerializedName("user_choice") public String userChoice;
    @SerializedName("is_correct") public Boolean isCorrect;

    // Dùng cho API filter
    @SerializedName("result_id") public Integer resultId;

    @SerializedName("explanation")
    public String explanation;
}