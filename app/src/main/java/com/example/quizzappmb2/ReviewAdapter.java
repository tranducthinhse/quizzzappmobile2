package com.example.quizzappmb2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<ReviewDetail> reviewList;
    private final Context context;

    private static final int COLOR_CORRECT = 0xFF00E676;
    private static final int COLOR_WRONG = 0xFFFF3D71;
    private static final int COLOR_USER_CHOICE_BG = 0x306C5DD3;
    private static final int COLOR_DEFAULT = 0xFFFFFFFF;



    public ReviewAdapter(Context context, List<ReviewDetail> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_question, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDetail item = reviewList.get(position);

        // KIỂM TRA NULL AN TOÀN CHO MỌI VIEW
        if (holder.tvQuestion != null) holder.tvQuestion.setText((position + 1) + ". " + item.questionContent);
        if (holder.tvOptionA != null) holder.tvOptionA.setText("A. " + item.answerA);
        if (holder.tvOptionB != null) holder.tvOptionB.setText("B. " + item.answerB);

        if ("MC".equalsIgnoreCase(item.type)) {
            if (holder.tvOptionC != null) {
                holder.tvOptionC.setVisibility(View.VISIBLE);
                holder.tvOptionC.setText("C. " + item.answerC);
            }
            if (holder.tvOptionD != null) {
                holder.tvOptionD.setVisibility(View.VISIBLE);
                holder.tvOptionD.setText("D. " + item.answerD);
            }
        } else {
            if (holder.tvOptionC != null) holder.tvOptionC.setVisibility(View.GONE);
            if (holder.tvOptionD != null) holder.tvOptionD.setVisibility(View.GONE);
        }

        // Xử lý trạng thái
        if (holder.tvUserStatus != null) {
            if (item.isCorrect != null && item.isCorrect) { // Check null cho Boolean
                holder.tvUserStatus.setText("TRẢ LỜI: ĐÚNG");
                holder.tvUserStatus.setTextColor(COLOR_CORRECT);
            } else {
                String status = ("TIMEOUT".equals(item.userChoice)) ? "HẾT GIỜ / BỎ QUA" : "SAI";
                holder.tvUserStatus.setText("TRẢ LỜI: " + status);
                holder.tvUserStatus.setTextColor(COLOR_WRONG);
            }
        }

        if (holder.tvCorrectAnswer != null) {
            holder.tvCorrectAnswer.setText("ĐÁP ÁN CHUẨN: " + item.correctAnswer);
        }

        if (item.explanation != null && !item.explanation.isEmpty() && !"null".equals(item.explanation)) {
            holder.layoutExplanation.setVisibility(View.VISIBLE);
            holder.tvExplanation.setText(item.explanation);
        } else {
            // Nếu không có giải thích thì ẩn khung đi cho gọn
            holder.layoutExplanation.setVisibility(View.GONE);
        }

        // Tô màu (Hàm này cũng đã check null)
        highlightOptions(holder, item.userChoice, item.correctAnswer, (item.isCorrect != null && item.isCorrect));
    }

    private void highlightOptions(ReviewViewHolder holder, String userChoice, String correctAnswer, boolean isCorrect) {
        resetOptionBackgrounds(holder);

        TextView userSelectedView = getOptionView(holder, userChoice);
        TextView correctView = getOptionView(holder, correctAnswer);

        if (userSelectedView != null) {
            userSelectedView.setBackgroundColor(isCorrect ? COLOR_USER_CHOICE_BG : COLOR_WRONG);
            userSelectedView.setTextColor(Color.WHITE);
        }

        if (!isCorrect && correctView != null) {
            correctView.setBackgroundColor(COLOR_CORRECT);
            correctView.setTextColor(Color.BLACK);
        }
    }

    private TextView getOptionView(ReviewViewHolder holder, String option) {
        if (option == null) return null;
        switch (option.toUpperCase()) {
            case "A": return holder.tvOptionA;
            case "B": return holder.tvOptionB;
            case "C": return holder.tvOptionC;
            case "D": return holder.tvOptionD;
            default: return null;
        }
    }

    private void resetOptionBackgrounds(ReviewViewHolder holder) {
        if (holder.tvOptionA != null) {
            holder.tvOptionA.setBackgroundColor(Color.TRANSPARENT);
            holder.tvOptionA.setTextColor(COLOR_DEFAULT);
        }
        if (holder.tvOptionB != null) {
            holder.tvOptionB.setBackgroundColor(Color.TRANSPARENT);
            holder.tvOptionB.setTextColor(COLOR_DEFAULT);
        }
        if (holder.tvOptionC != null) {
            holder.tvOptionC.setBackgroundColor(Color.TRANSPARENT);
            holder.tvOptionC.setTextColor(COLOR_DEFAULT);
        }
        if (holder.tvOptionD != null) {
            holder.tvOptionD.setBackgroundColor(Color.TRANSPARENT);
            holder.tvOptionD.setTextColor(COLOR_DEFAULT);
        }
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvUserStatus, tvCorrectAnswer;
        TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
        TextView tvExplanation;
        View layoutExplanation;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvReviewQuestion);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvExplanation = itemView.findViewById(R.id.tvExplanation);
            layoutExplanation = itemView.findViewById(R.id.layoutExplanation);
        }
    }
}