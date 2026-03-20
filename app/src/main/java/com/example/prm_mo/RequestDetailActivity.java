package com.example.prm_mo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvType, tvStatus, tvIncidentType, tvDescription, tvPeopleCount, tvPriority, tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        initViews();
        
        String requestId = getIntent().getStringExtra("requestId");
        if (requestId != null) {
            loadRequestDetail(requestId);
        }
    }

    private void initViews() {
        tvType = findViewById(R.id.tvDetailType);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvIncidentType = findViewById(R.id.tvDetailIncidentType);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvPeopleCount = findViewById(R.id.tvDetailPeopleCount);
        tvPriority = findViewById(R.id.tvDetailPriority);
        tvCreatedAt = findViewById(R.id.tvDetailCreatedAt);
    }

    private void loadRequestDetail(String requestId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        
        RetrofitClient.getApiService().getRequestDetail("Bearer " + token, requestId).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RescueRequest request = response.body().getData();
                    if (request != null) {
                        displayData(request);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(RequestDetailActivity.this, "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(RescueRequest request) {
        tvType.setText(request.getType());
        tvStatus.setText(request.getStatus());
        tvIncidentType.setText(request.getIncidentType());
        tvDescription.setText(request.getDescription());
        tvPeopleCount.setText(String.valueOf(request.getPeopleCount()));
        tvPriority.setText(request.getPriority());
        tvCreatedAt.setText(request.getCreatedAt());

        String role = SharedPrefsManager.getInstance(this).getUserRole();
        android.widget.LinearLayout llActions = findViewById(R.id.llCoordinatorActions);

        // Chỉ hiện nút hành động nếu là Coordinator và Yêu cầu đang SUBMITTED
        if ("Rescue Coordinator".equals(role) && "SUBMITTED".equals(request.getStatus())) {
            llActions.setVisibility(android.view.View.VISIBLE);
            
            findViewById(R.id.btnVerify).setOnClickListener(v -> actionVerifyRequest(request.getId(), true, request.getPriority(), null));
            findViewById(R.id.btnReject).setOnClickListener(v -> actionVerifyRequest(request.getId(), false, null, "Từ chối bởi Coordinator."));
        } else {
            llActions.setVisibility(android.view.View.GONE);
        }
    }

    private void actionVerifyRequest(String requestId, boolean isApproved, String priority, String reason) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        com.example.prm_mo.models.VerifyRequestInput input = new com.example.prm_mo.models.VerifyRequestInput(isApproved, priority, reason);

        RetrofitClient.getApiService().verifyRequest("Bearer " + token, requestId, input).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestDetailActivity.this, "Đã cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    loadRequestDetail(requestId); // Reload lại trang để thấy status nhảy sang VERIFIED / REJECTED
                } else {
                    Toast.makeText(RequestDetailActivity.this, "Lỗi khi xử lý Yêu Cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(RequestDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
