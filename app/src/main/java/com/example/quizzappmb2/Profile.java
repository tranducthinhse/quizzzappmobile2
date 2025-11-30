package com.example.quizzappmb2;
import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("id") public String id;
    @SerializedName("role") public String role;

    // Các trường mới thêm
    @SerializedName("full_name") public String fullName;
    @SerializedName("phone") public String phone;
    @SerializedName("address") public String address;
    @SerializedName("dob") public String dob;
    @SerializedName("gender") public String gender;
    @SerializedName("avatar_url") public String avatarUrl;
    @SerializedName("is_banned") public boolean isBanned;

    public Profile() {}
}