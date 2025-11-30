package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class LeaderboardEntry {
    @SerializedName("full_name") public String fullName;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("quiz_title") public String quizTitle;
    @SerializedName("max_score") public int maxScore;

    public String getAvatarUrl() {
        return avatarUrl;
    }
}