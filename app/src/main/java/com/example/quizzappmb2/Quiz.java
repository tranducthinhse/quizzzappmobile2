package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class Quiz {

    @SerializedName("id")
    public Integer id;

    @SerializedName("title") public String title;
    @SerializedName("description") public String description;
    @SerializedName("image_url") public String imageUrl;

    @SerializedName("difficulty") public String difficulty;
    @SerializedName("total_questions") public int totalQuestions;
    @SerializedName("duration_minutes") public int durationMinutes;

    public Quiz() {}
}