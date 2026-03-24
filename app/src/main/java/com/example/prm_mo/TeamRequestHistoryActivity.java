package com.example.prm_mo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.adapters.TeamRequestAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.TeamRequest;
import com.example.prm_mo.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamRequestHistoryActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView tvEmpty;
    private ImageView btnBack;
    private TeamRequestAdapter adapter;
    private List<TeamRequest> list = new ArrayList<>();
    private String missionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_request_history);

        missionId = getIntent().getStringExtra("MISSION_ID");

        rv = findViewById(R.id.rvTeamRequests);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamRequestAdapter(list);
        rv.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        if(missionId == null) return;
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getTeamRequests(token, missionId).enqueue(new Callback<ApiResponse<List<TeamRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TeamRequest>>> call, Response<ApiResponse<List<TeamRequest>>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    list.clear();
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    
                    if(list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(TeamRequestHistoryActivity.this, "Lỗi khi tải lịch sử", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<TeamRequest>>> call, Throwable t) {
                Toast.makeText(TeamRequestHistoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
