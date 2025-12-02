package com.example.quizzappmb2;

import com.google.gson.annotations.SerializedName;

public class Question {

    @SerializedName("id")
    private Integer id; // Dùng Integer để hỗ trợ null khi thêm mới

    @SerializedName("quiz_id")
    private Integer quizId;

    @SerializedName("content")
    private String content;

    @SerializedName("answer_a")
    private String answerA;

    @SerializedName("answer_b")
    private String answerB;

    @SerializedName("answer_c")
    private String answerC;

    @SerializedName("answer_d")
    private String answerD;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("type")
    private String type; // "MC" hoặc "TF"

    @SerializedName("explanation")
    private String explanation; // Lời giải thích

    // Constructor 1: Full tham số
    public Question(String content, String answerA, String answerB, String answerC, String answerD, String correctAnswer) {
        this.content = content;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
    }

    // Constructor 2: Rỗng (Bắt buộc)
    public Question() {}

    // --- CÁC HÀM GETTER ---
    public Integer getId() { return id; }
    public Integer getQuizId() { return quizId; }
    public String getContent() { return content; }
    public String getAnswerA() { return answerA; }
    public String getAnswerB() { return answerB; }
    public String getAnswerC() { return answerC; }
    public String getAnswerD() { return answerD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getType() { return type; }
    public String getExplanation() { return explanation; }

    // --- CÁC HÀM SETTER (QUAN TRỌNG ĐỂ SỬA LỖI) ---
    public void setId(Integer id) { this.id = id; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public void setContent(String content) { this.content = content; }

    public void setAnswerA(String answerA) { this.answerA = answerA; }
    public void setAnswerB(String answerB) { this.answerB = answerB; }
    public void setAnswerC(String answerC) { this.answerC = answerC; }
    public void setAnswerD(String answerD) { this.answerD = answerD; }

    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setType(String type) { this.type = type; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}