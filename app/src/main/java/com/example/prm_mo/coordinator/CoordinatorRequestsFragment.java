package com.example.prm_mo.coordinator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.adapters.RescueRequestAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.RequestListResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorRequestsFragment extends Fragment {

    private RecyclerView rvCoordinatorRequests;
    private RescueRequestAdapter adapter;
    private List<RescueRequest> requestList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_requests, container, false);
        
        rvCoordinatorRequests = view.findViewById(R.id.rvCoordinatorRequests);
        rvCoordinatorRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        
        requestList = new ArrayList<>();
        // Sử dụng lại adapter hiển thị RescueRequest của hệ thống
        adapter = new RescueRequestAdapter(requestList);
        rvCoordinatorRequests.setAdapter(adapter);

        loadRequests();
        return view;
    }

    private void loadRequests() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        RetrofitClient.getApiService().listAllRequests("Bearer " + token, null, null, 1, 50)
            .enqueue(new Callback<RequestListResponse>() {
                @Override
                public void onResponse(Call<RequestListResponse> call, Response<RequestListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess() && response.body().getData() != null) {
                            requestList.clear();
                            requestList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RequestListResponse> call, Throwable t) {
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "Không tải được danh sách yêu cầu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
