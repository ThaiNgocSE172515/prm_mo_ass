package com.example.prm_mo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.Team;
import com.example.prm_mo.models.UpdateTeamRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetailActivity extends AppCompatActivity {

    private String teamId;
    private TextView tvTeamNameTitle, tvTeamStatus;
    private Team currentTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        teamId = getIntent().getStringExtra("TEAM_ID");
        if (teamId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin Đội", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTeamNameTitle = findViewById(R.id.tvTeamNameTitle);
        tvTeamStatus = findViewById(R.id.tvTeamStatus);

        findViewById(R.id.btnEditTeam).setOnClickListener(v -> showEditDialog());
        findViewById(R.id.btnDeleteTeam).setOnClickListener(v -> deleteTeam());

        loadTeamDetail();
    }

    private void loadTeamDetail() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getTeamDetail("Bearer " + token, teamId).enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTeam = response.body();
                    displayData();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(TeamDetailActivity.this, "Lỗi: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TeamDetailActivity.this, "Không thể tải dữ liệu đội", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData() {
        if (currentTeam != null) {
            tvTeamNameTitle.setText(currentTeam.getName() != null ? currentTeam.getName() : "Không tên");
            tvTeamStatus.setText(currentTeam.getStatus() != null ? currentTeam.getStatus() : "N/A");
        }
    }

    private void showEditDialog() {
        if (currentTeam == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin đội");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setText(currentTeam.getName());
        layout.addView(inputName);

        final android.widget.EditText inputStatus = new android.widget.EditText(this);
        inputStatus.setText(currentTeam.getStatus());
        layout.addView(inputStatus);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newStatus = inputStatus.getText().toString().trim();
            updateTeam(newName, newStatus);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateTeam(String name, String status) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        UpdateTeamRequest req = new UpdateTeamRequest(name, status);

        RetrofitClient.getApiService().updateTeam("Bearer " + token, teamId, req).enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeamDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadTeamDetail();
                } else {
                    Toast.makeText(TeamDetailActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTeam() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().deleteTeam("Bearer " + token, teamId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeamDetailActivity.this, "Đã xóa đội", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TeamDetailActivity.this, "Xóa thất bại (Đội phải đang AVAILABLE và không có thành viên)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
