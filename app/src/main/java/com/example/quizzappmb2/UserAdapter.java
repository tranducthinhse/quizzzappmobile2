package com.example.quizzappmb2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Profile> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onBan(Profile user);
        void onDelete(Profile user);
        void onHistory(Profile user);
    }

    public UserAdapter(Context context, List<Profile> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile user = userList.get(position);

        holder.tvName.setText(user.fullName != null ? user.fullName : "User ID: " + user.id);

        if (user.avatarUrl != null) Glide.with(context).load(user.avatarUrl).into(holder.imgAvatar);

        if (user.isBanned) {
            holder.tvStatus.setText("BANNED");
            holder.tvStatus.setTextColor(Color.RED);
            holder.btnBan.setText("MỞ KHÓA");
        } else {
            holder.tvStatus.setText("ACTIVE");
            holder.tvStatus.setTextColor(Color.GREEN);
            holder.btnBan.setText("BAN");
        }

        holder.btnBan.setOnClickListener(v -> listener.onBan(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        holder.btnHistory.setOnClickListener(v -> listener.onHistory(user));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        ImageView imgAvatar;
        Button btnHistory, btnBan, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnBan = itemView.findViewById(R.id.btnBan);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}