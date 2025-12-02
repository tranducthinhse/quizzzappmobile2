package com.example.quizzappmb2;

import java.util.List;
import java.util.ArrayList;

public class GlobalAnswerCache {
    private static List<UserAnswer> answers = new ArrayList<>();

    private GlobalAnswerCache() {}

    public static void setAnswers(List<UserAnswer> newAnswers) {
        answers = newAnswers;
    }

    public static List<UserAnswer> getAnswers() {
        return answers;
    }

    public static void clearAnswers() {
        answers = new ArrayList<>();
    }
}