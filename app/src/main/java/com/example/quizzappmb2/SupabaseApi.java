package com.example.quizzappmb2;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {

    // 1. ĐĂNG NHẬP (Dùng Full URL để tránh bị cộng dồn /rest/v1)
    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/token?grant_type=password")
    Call<AuthResponse> login(
            @Header("apikey") String apiKey,
            @Body LoginRequest request
    );

    // 2. LẤY PROFILE (Giữ nguyên vì nó cần /rest/v1 ở BaseURL)
    @GET("profiles?select=*")
    Call<List<Profile>> getProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String userIdFilter
    );

    // 3. ĐĂNG KÝ (SỬA LẠI: Dùng Full URL giống Login)
    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/signup")
    Call<AuthResponse> signUp(
            @Header("apikey") String apiKey,
            @Body LoginRequest request
    );

    // 4. QUÊN MẬT KHẨU (THÊM MỚI: Dùng Full URL)
    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/recover")
    Call<Void> resetPassword(
            @Header("apikey") String apiKey,
            @Body LoginRequest request
    );

    // 5. THÊM CÂU HỎI (Giữ nguyên vì cần /rest/v1)
    @POST("questions")
    Call<Void> addQuestion(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Question question
    );

    // Lấy danh sách đề thi
    @GET("quizzes?select=*")
    Call<List<Quiz>> getQuizzes(@Header("apikey") String apiKey);

    @retrofit2.http.PATCH("profiles")
    Call<Void> updateProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String queryId, // Cú pháp: eq.xxxxx
            @Body Profile profile        // Dữ liệu cần sửa
    );

    @POST("https://hyueubinwueddmixxqyk.supabase.co/storage/v1/object/avatars/{path}")
    Call<Void> uploadAvatar(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Content-Type") String contentType,
            @retrofit2.http.Path("path") String imagePath,
            @Body RequestBody imageBytes
    );

    // Lấy câu hỏi theo quiz_id
    // URL: questions?quiz_id=eq.123&select=*
    @GET("questions?select=*")
    Call<List<Question>> getQuestionsByQuizId(
            @Header("apikey") String apiKey,
            @Query("quiz_id") String quizIdFilter // Cú pháp: eq.123
    );

    @POST("results")
    Call<Void> saveResult(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Result result
    );

    // Lấy lịch sử (Lọc theo user_id)
    @GET("history_view")
    Call<List<HistoryItem>> getHistory(
            @Header("apikey") String apiKey,
            @Query("user_id") String userIdFilter // eq.USER_ID
    );
    // Lấy danh sách tất cả user, TRỪ NHỮNG NGƯỜI CÓ ROLE LÀ 'admin'
    @GET("profiles?select=*,full_name,is_banned,avatar_url&role=neq.admin")
    Call<List<Profile>> getAllUsers(@Header("apikey") String apiKey);

    // 2. Xóa User (Khỏi bảng profiles)
    @DELETE("profiles")
    Call<Void> deleteUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String userIdFilter // Cú pháp: id=eq.XXXX
    );
    @POST("quizzes")
    Call<Void> createQuiz(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Quiz quiz
    );

    @PATCH("questions")
    Call<Void> updateQuestion(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String questionIdFilter, // id=eq.123
            @Body Question question
    );

    @DELETE("questions")
    Call<Void> deleteQuestion(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String questionIdFilter
    );


    @POST("rpc/get_admin_stats")
    Call<List<AdminStats>> getAdminStats(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token
    );

    // Trong SupabaseApi.java

    // API lấy 50 người có điểm cao nhất
    @GET("global_leaderboard_view?select=*&order=max_score.desc&limit=50")
    Call<List<LeaderboardEntry>> getLeaderboard(
            @Header("apikey") String apiKey
    );


}