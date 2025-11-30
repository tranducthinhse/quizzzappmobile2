package com.example.quizzappmb2;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView rcvLeaderboard;
    private View top1View, top2View, top3View;
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5dWV1Ymlud3VlZGRtaXh4cXlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ0MDMxMjksImV4cCI6MjA3OTk3OTEyOX0.Q1PhMfB57cgDNnfdF_UgOVJDX-Y7Z-YZ6lyW0yV8ZuA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        top1View = findViewById(R.id.top1);
        top2View = findViewById(R.id.top2);
        top3View = findViewById(R.id.top3);

        rcvLeaderboard = findViewById(R.id.rcvLeaderboard);
        rcvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        SupabaseApi api = SupabaseClient.getClient().create(SupabaseApi.class);
        api.getLeaderboard(API_KEY).enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override
            public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayLeaderboard(response.body());
                } else {
                    Toast.makeText(LeaderboardActivity.this, "Lỗi tải BXH: Code " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                Toast.makeText(LeaderboardActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLeaderboard(List<LeaderboardEntry> list) {

        if (list.size() >= 1) bindTopItem(top1View, list.get(0), 1); else hideTopItem(top1View);
        if (list.size() >= 2) bindTopItem(top2View, list.get(1), 2); else hideTopItem(top2View);
        if (list.size() >= 3) bindTopItem(top3View, list.get(2), 3); else hideTopItem(top3View);

        if (list.size() > 3) {
            List<LeaderboardEntry> subList = list.subList(3, list.size());
            LeaderboardAdapter adapter = new LeaderboardAdapter(LeaderboardActivity.this, subList, 4);
            rcvLeaderboard.setAdapter(adapter);
        } else {
            rcvLeaderboard.setAdapter(null);
        }
    }

    private void bindTopItem(View topView, LeaderboardEntry entry, int rank) {
        TextView tvRank = topView.findViewById(R.id.tvRank);
        TextView tvName = topView.findViewById(R.id.tvName);
        TextView tvScore = topView.findViewById(R.id.tvScore);

        tvRank.setText("#" + rank);
        tvName.setText(entry.fullName);
        tvScore.setText(entry.maxScore + " Score");

        // *Logic tô màu cúp/rank*
        if (rank == 1) tvRank.setTextColor(0xFFFFD700);
        else if (rank == 2) tvRank.setTextColor(0xFFC0C0C0);
        else if (rank == 3) tvRank.setTextColor(0xFFCD7F32);
    }

    private void hideTopItem(View topView) {
        topView.setVisibility(View.INVISIBLE);
    }
}