package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class Quiz {

    // --- SỬA TẠI ĐÂY: Dùng Integer thay vì int ---
    @SerializedName("id")
    public Integer id; // Đã sửa: Integer

    @SerializedName("title") public String title;
    @SerializedName("description") public String description;
    @SerializedName("image_url") public String imageUrl;

    @SerializedName("difficulty") public String difficulty;
    @SerializedName("total_questions") public int totalQuestions; // Giữ int cho các trường này
    @SerializedName("duration_minutes") public int durationMinutes;

    public Quiz() {} // Bắt buộc có Constructor rỗng
}