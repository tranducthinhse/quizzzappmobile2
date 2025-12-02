package com.example.quizzappmb2;

import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {

    // --- AUTH ---
    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Header("apikey") String apiKey, @Body LoginRequest request);

    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/signup")
    Call<AuthResponse> signUp(@Header("apikey") String apiKey, @Body LoginRequest request);

    @POST("https://hyueubinwueddmixxqyk.supabase.co/auth/v1/recover")
    Call<Void> resetPassword(@Header("apikey") String apiKey, @Body LoginRequest request);

    // --- PROFILES ---
    @GET("profiles?select=*")
    Call<List<Profile>> getProfile(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("id") String userIdFilter);

    @GET("profiles?select=*,full_name,is_banned,avatar_url&role=neq.admin")
    Call<List<Profile>> getAllUsers(@Header("apikey") String apiKey);

    @PATCH("profiles")
    Call<Void> updateProfile(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("id") String queryId, @Body Profile profile);

    @DELETE("profiles")
    Call<Void> deleteUser(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("id") String userIdFilter);

    @POST("https://hyueubinwueddmixxqyk.supabase.co/storage/v1/object/avatars/{path}")
    Call<Void> uploadAvatar(@Header("apikey") String apiKey, @Header("Authorization") String token, @Header("Content-Type") String contentType, @retrofit2.http.Path("path") String imagePath, @Body RequestBody imageBytes);

    // --- QUIZZES ---
    @GET("quizzes?select=*")
    Call<List<Quiz>> getQuizzes(@Header("apikey") String apiKey);

    @POST("quizzes")
    Call<Void> createQuiz(@Header("apikey") String apiKey, @Header("Authorization") String token, @Body Quiz quiz);

    // [MỚI] TẠO QUIZ VÀ TRẢ VỀ ID (Dùng cho Import)
    @POST("quizzes")
    @Headers({"Prefer: return=representation"})
    Call<List<Quiz>> createQuizAndReturn(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Quiz quiz
    );

    // --- QUESTIONS ---
    @GET("questions?select=*")
    Call<List<Question>> getQuestionsByQuizId(@Header("apikey") String apiKey, @Query("quiz_id") String quizIdFilter);

    @POST("questions")
    Call<Void> addQuestion(@Header("apikey") String apiKey, @Header("Authorization") String token, @Body Question question);

    // [MỚI] THÊM NHIỀU CÂU HỎI CÙNG LÚC (Dùng cho Import)
    @POST("questions")
    Call<Void> addQuestionsBulk(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body List<Question> questions
    );

    @PATCH("questions")
    Call<Void> updateQuestion(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("id") String questionIdFilter, @Body Question question);

    @DELETE("questions")
    Call<Void> deleteQuestion(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("id") String questionIdFilter);

    // --- RESULTS & HISTORY ---
    @POST("results")
    @Headers({"Prefer: return=representation"})
    Call<List<Result>> saveResult(@Header("apikey") String apiKey, @Header("Authorization") String token, @Body Result result);

    @POST("user_answers")
    Call<Void> saveUserAnswers(@Header("apikey") String apiKey, @Header("Authorization") String token, @Body List<UserAnswer> userAnswers);

    @GET("history_view")
    Call<List<HistoryItem>> getHistory(@Header("apikey") String apiKey, @Query("user_id") String userIdFilter);

    @GET("review_detail_view?select=*")
    Call<List<ReviewDetail>> getReviewDetails(@Header("apikey") String apiKey, @Header("Authorization") String token, @Query("result_id") String resultIdFilter);

    // --- STATS & LEADERBOARD ---
    @POST("rpc/get_admin_stats")
    Call<List<AdminStats>> getAdminStats(@Header("apikey") String apiKey, @Header("Authorization") String token);

    @GET("global_leaderboard_view?select=*&order=max_score.desc&limit=50")
    Call<List<LeaderboardEntry>> getLeaderboard(@Header("apikey") String apiKey);
}