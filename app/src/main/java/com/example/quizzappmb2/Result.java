package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id") public Integer id;
    @SerializedName("user_id") private String userId;
    @SerializedName("quiz_id") private int quizId;
    @SerializedName("score") private int score;
    @SerializedName("total") private int total;

    public Result(String userId, int quizId, int score, int total) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.total = total;
    }
}