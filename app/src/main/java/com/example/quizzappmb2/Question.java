package com.example.quizzappmb2;

import com.google.gson.annotations.SerializedName;

public class Question {


    @SerializedName("id")
    private Integer id;

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
    private String type;

    public Question(String content, String answerA, String answerB, String answerC, String answerD, String correctAnswer) {
        this.content = content;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
    }

    public Question() {}

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // --- GETTERS ---
    public Integer getId() { return id; }
    public Integer getQuizId() { return quizId; }
    public String getContent() { return content; }
    public String getAnswerA() { return answerA; }
    public String getAnswerB() { return answerB; }
    public String getAnswerC() { return answerC; }
    public String getAnswerD() { return answerD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getType() { return type; }
}