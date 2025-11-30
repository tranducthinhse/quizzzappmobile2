package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class AdminStats {
    @SerializedName("total_users") public long totalUsers;
    @SerializedName("total_quizzes") public long totalQuizzes;
    @SerializedName("total_questions") public long totalQuestions;
    @SerializedName("avg_score") public double avgScore;
}