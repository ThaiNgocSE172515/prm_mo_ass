package com.example.prm_mo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.adapters.TimelineAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.Timeline;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamHomeActivity extends AppCompatActivity {

    private RecyclerView rvTimelines;
    private TimelineAdapter adapter;
    private List<Timeline> timelineList;
    private ImageView btnLogout;
    private ImageView btnNotification;

    // ActivityResultLauncher: tự động reload danh sách khi quay lại từ TimelineDetailActivity
    private final ActivityResultLauncher<Intent> timelineLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                fetchMyTimelines(); // Luôn reload khi quay lại
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_home);

        btnLogout = findViewById(R.id.btnLogout);
        rvTimelines = findViewById(R.id.rvTimelines);
        rvTimelines.setLayoutManager(new LinearLayoutManager(this));

        timelineList = new ArrayList<>();
        adapter = new TimelineAdapter(timelineList);

        // Mở TimelineDetailActivity qua launcher để bắt kết quả
        adapter.setOnItemClickListener(timeline -> {
            Intent intent = new Intent(this, TimelineDetailActivity.class);
            intent.putExtra("TIMELINE_ID", timeline.getId());
            if (timeline.getMission() != null) {
                intent.putExtra("MISSION_ID", timeline.getMission().getId());
            }
            intent.putExtra("TIMELINE_STATUS", timeline.getStatus());
            timelineLauncher.launch(intent);
        });

        rvTimelines.setAdapter(adapter);

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));

        btnLogout.setOnClickListener(v -> logout());

        fetchMyTimelines();
    }

    private void logout() {
        SharedPrefsManager.getInstance(this).clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchMyTimelines() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        if (token == null) return;

        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService().getMyTimelines(authHeader, null).enqueue(new Callback<ApiResponse<List<Timeline>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Timeline>>> call, Response<ApiResponse<List<Timeline>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    timelineList.clear();
                    timelineList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                    if (timelineList.isEmpty()) {
                        Toast.makeText(TeamHomeActivity.this, "Bạn chưa được giao nhiệm vụ nào!", Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(TeamHomeActivity.this, "Phiên đăng nhập hết hạn, vui lòng login lại", Toast.LENGTH_SHORT).show();
                    logout();
                } else {
                    Toast.makeText(TeamHomeActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Timeline>>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(TeamHomeActivity.this, "Không thể kết nối Server (Timeout)!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}