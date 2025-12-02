package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class HistoryItem {
    @SerializedName("id")
    public Integer id;
    @SerializedName("quiz_title") public String quizTitle;
    @SerializedName("score") public int score;
    @SerializedName("total") public int total;
    @SerializedName("created_at") public String createdAt;
}