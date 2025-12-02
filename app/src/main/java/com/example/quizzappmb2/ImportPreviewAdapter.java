package com.example.quizzappmb2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ImportPreviewAdapter extends RecyclerView.Adapter<ImportPreviewAdapter.ViewHolder> {

    private final List<Question> list;

    public ImportPreviewAdapter(List<Question> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_import_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = list.get(position);

        holder.tvIndex.setText("#" + (position + 1));
        holder.tvType.setText(q.getType());
        holder.tvContent.setText(q.getContent());

        holder.tvOptionA.setText("A. " + q.getAnswerA());
        holder.tvOptionB.setText("B. " + q.getAnswerB());
        holder.tvOptionC.setText("C. " + q.getAnswerC());
        holder.tvOptionD.setText("D. " + q.getAnswerD());

        holder.tvCorrect.setText("ĐÁP ÁN ĐÚNG: " + q.getCorrectAnswer());

        // Logic tô màu đáp án đúng để dễ kiểm tra
        resetColors(holder);
        String correct = q.getCorrectAnswer();
        int colorCorrect = Color.parseColor("#00E676"); // Xanh lá

        if ("A".equalsIgnoreCase(correct)) holder.tvOptionA.setTextColor(colorCorrect);
        else if ("B".equalsIgnoreCase(correct)) holder.tvOptionB.setTextColor(colorCorrect);
        else if ("C".equalsIgnoreCase(correct)) holder.tvOptionC.setTextColor(colorCorrect);
        else if ("D".equalsIgnoreCase(correct)) holder.tvOptionD.setTextColor(colorCorrect);
    }

    private void resetColors(ViewHolder holder) {
        int gray = Color.parseColor("#9E9EA7");
        holder.tvOptionA.setTextColor(gray);
        holder.tvOptionB.setTextColor(gray);
        holder.tvOptionC.setTextColor(gray);
        holder.tvOptionD.setTextColor(gray);
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvType, tvContent, tvCorrect;
        TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvType = itemView.findViewById(R.id.tvType);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCorrect = itemView.findViewById(R.id.tvCorrect);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
        }
    }
}