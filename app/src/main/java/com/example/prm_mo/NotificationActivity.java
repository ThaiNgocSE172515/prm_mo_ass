package com.example.prm_mo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.adapters.NotificationAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.Notification;
import com.example.prm_mo.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView tvEmpty;
    private ImageView btnBack;
    private NotificationAdapter adapter;
    private List<Notification> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rv = findViewById(R.id.rvNotifications);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(list, item -> {
            if(!item.isRead()) markAsRead(item);
        });
        rv.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getMyNotifications(token).enqueue(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {
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
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(Notification item) {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().markNotificationRead(token, item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful()) {
                    item.setRead(true);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }
}
