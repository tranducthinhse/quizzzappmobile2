package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class UserAnswer {
    @SerializedName("result_id") public Integer resultId;
    @SerializedName("question_id") public Integer questionId;
    @SerializedName("user_choice") public String userChoice;
    @SerializedName("is_correct") public Boolean isCorrect;

    public UserAnswer(Integer questionId, String userChoice, Boolean isCorrect) {
        this.questionId = questionId;
        this.userChoice = userChoice;
        this.isCorrect = isCorrect;
    }
}