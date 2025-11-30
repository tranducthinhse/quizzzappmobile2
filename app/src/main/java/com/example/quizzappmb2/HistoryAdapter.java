package com.example.quizzappmb2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> list;

    public HistoryAdapter(List<HistoryItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = list.get(position);

        holder.tvTitle.setText(item.quizTitle);
        holder.tvScore.setText(item.score + "/" + item.total);

        if(item.createdAt != null && item.createdAt.length() > 10) {
            holder.tvDate.setText(item.createdAt.substring(0, 10));
        }

        float percent = (float) item.score / item.total;
        if (percent >= 0.8) holder.tvScore.setTextColor(Color.parseColor("#00E676")); // Xanh
        else if (percent < 0.5) holder.tvScore.setTextColor(Color.parseColor("#FF3D71")); // Đỏ
        else holder.tvScore.setTextColor(Color.parseColor("#00D2FC")); // Cyan
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvScore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
        }
    }
}