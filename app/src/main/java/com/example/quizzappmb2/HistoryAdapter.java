package com.example.quizzappmb2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Nhớ import Button
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> list;
    private Context context; // Thêm biến context
    private OnItemClickListener listener;

    // Interface để gửi ID phiên thi (resultId) về Activity
    public interface OnItemClickListener {
        void onItemClick(int resultId);
    }

    // Constructor chuẩn: Nhận List, Context và Listener
    public HistoryAdapter(List<HistoryItem> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
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

        // 1. Gán dữ liệu
        holder.tvTitle.setText(item.quizTitle);
        holder.tvScore.setText(item.score + "/" + item.total);

        if(item.createdAt != null && item.createdAt.length() > 10) {
            holder.tvDate.setText(item.createdAt.substring(0, 10));
        }

        // 2. Logic màu sắc
        float percent = (float) item.score / item.total;
        if (percent >= 0.8) holder.tvScore.setTextColor(Color.parseColor("#00E676")); // Xanh
        else if (percent < 0.5) holder.tvScore.setTextColor(Color.parseColor("#FF3D71")); // Đỏ
        else holder.tvScore.setTextColor(Color.parseColor("#00D2FC")); // Cyan

        // 3. XỬ LÝ NÚT "ÔN TẬP" (Đây là phần bạn cần)
        holder.btnReview.setOnClickListener(v -> {
            if (listener != null && item.id != null) {
                // Gọi hàm trong Activity để chuyển trang
                listener.onItemClick(item.id);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    // --- VIEW HOLDER ---
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvScore;
        Button btnReview; // Khai báo nút

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);

            // Ánh xạ nút Ôn tập (ID phải có trong item_history.xml)
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}