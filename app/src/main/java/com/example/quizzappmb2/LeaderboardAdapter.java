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
        LeaderboardEntry entry = list.get(position);

        int rank = position + startRank;

        holder.tvRank.setText("#" + rank);
        holder.tvName.setText(entry.fullName);
        holder.tvScore.setText(entry.maxScore + " Score");

        holder.tvRank.setTextColor(Color.parseColor("#00D2FC"));

        if (entry.avatarUrl != null && !entry.avatarUrl.isEmpty()) {
            Glide.with(context).load(entry.avatarUrl).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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