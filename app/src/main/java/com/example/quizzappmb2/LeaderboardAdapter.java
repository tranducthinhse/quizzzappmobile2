package com.example.quizzappmb2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final Context context;
    private final List<LeaderboardEntry> list;
    private final int startRank;

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> list, int startRank) {
        this.context = context;
        this.list = list;
        this.startRank = startRank;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        if (list == null || position >= list.size()) return; // Check list

        LeaderboardEntry entry = list.get(position);
        int rank = position + startRank;

        if (holder.tvRank != null) holder.tvRank.setText("#" + rank);
        if (holder.tvName != null) holder.tvName.setText(entry.fullName);
        if (holder.tvScore != null) {
            int score = (entry.maxScore != null) ? entry.maxScore.intValue() : 0;
            holder.tvScore.setText(score + " Score");
        }

        // Tô màu
        if (holder.itemView != null) {
            if (position % 2 == 0) holder.itemView.setBackgroundColor(Color.parseColor("#1F1D2B"));
            else holder.itemView.setBackgroundColor(Color.parseColor("#252836"));
        }

        if (holder.tvRank != null && holder.tvScore != null) {
            if (rank == 4) {
                holder.tvRank.setTextColor(Color.parseColor("#00E676"));
                holder.tvScore.setTextColor(Color.parseColor("#00E676"));
            } else {
                holder.tvRank.setTextColor(Color.parseColor("#9E9EA7"));
                holder.tvScore.setTextColor(Color.parseColor("#00D2FC"));
            }
        }

        // --- PHẦN QUAN TRỌNG NHẤT: LOAD ẢNH AN TOÀN ---
        if (holder.imgAvatar != null) { // Kiểm tra chắc chắn View tồn tại
            try {
                if (entry.avatarUrl != null && !entry.avatarUrl.isEmpty()) {
                    // Thử tải ảnh
                    Glide.with(holder.itemView.getContext()) // Dùng Context từ View thay vì biến toàn cục
                            .load(entry.avatarUrl)
                            .placeholder(android.R.drawable.sym_def_app_icon)
                            .error(android.R.drawable.sym_def_app_icon)
                            .into(holder.imgAvatar);
                } else {
                    // Không có link thì set ảnh mặc định
                    holder.imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                }
            } catch (Exception e) {
                // Nếu Glide lỗi, bỏ qua luôn, không crash app
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvScore;
        ImageView imgAvatar;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}