package com.example.quizzappmb2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionAdminAdapter extends RecyclerView.Adapter<QuestionAdminAdapter.ViewHolder> {

    private List<Question> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Question question);
    }

    public QuestionAdminAdapter(List<Question> list, OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_admin,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Question q = list.get(position);
        holder.tvContent.setText(q.getContent());
        holder.tvAnswer.setText(q.getCorrectAnswer());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(q));
    }

    @Override
    public int getItemCount(){ return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvContent,tvAnswer;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
        }
    }
}
