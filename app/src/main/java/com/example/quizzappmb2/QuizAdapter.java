package com.example.quizzappmb2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private Context context;
    private List<Quiz> quizList;

    public QuizAdapter(Context context, List<Quiz> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_quiz_tech mới
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_tech, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        holder.tvTitle.setText(quiz.title);


        int qCount = (quiz.totalQuestions > 0) ? quiz.totalQuestions : 10;
        int qTime = (quiz.durationMinutes > 0) ? quiz.durationMinutes : 15;
        holder.tvInfo.setText(qCount + " Qs • " + qTime + " Mins");

        String diff = (quiz.difficulty != null) ? quiz.difficulty : "Medium";
        holder.tvDifficulty.setText(diff);

        int color;
        if ("Easy".equalsIgnoreCase(diff)) color = Color.parseColor("#00E676");
        else if ("Medium".equalsIgnoreCase(diff)) color = Color.parseColor("#FFB84C");
        else color = Color.parseColor("#FF3D71");

        holder.tvDifficulty.setBackgroundTintList(ColorStateList.valueOf(color));

        holder.btnStart.setOnClickListener(v -> {
            if (context instanceof UserActivity) {
                ((UserActivity) context).showQuizDetailDialog(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo, tvDifficulty;
        Button btnStart;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuizTitle);
            tvInfo = itemView.findViewById(R.id.tvQuizInfo);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            btnStart = itemView.findViewById(R.id.btnStartQuiz);
        }
    }
}