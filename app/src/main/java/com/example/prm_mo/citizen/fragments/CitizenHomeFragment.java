package com.example.prm_mo.citizen.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.prm_mo.citizen.CreateRequestActivity;
import com.example.prm_mo.adapters.RescueRequestAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenHomeFragment extends Fragment {

    private RecyclerView rvRequests;
    private RescueRequestAdapter adapter;
    private List<RescueRequest> requestList = new ArrayList<>();
    private FloatingActionButton btnCreateRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        rvRequests = view.findViewById(R.id.rvRequests);
        btnCreateRequest = view.findViewById(R.id.btnCreateRequest);

        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RescueRequestAdapter(requestList);
        rvRequests.setAdapter(adapter);

        loadMyRequests();

        btnCreateRequest.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateRequestActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMyRequests();
    }

    private void loadMyRequests() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        RetrofitClient.getApiService().getMyRequests("Bearer " + token, null, null, 1, 20)
                .enqueue(new Callback<ApiResponse<List<RescueRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RescueRequest>>> call, Response<ApiResponse<List<RescueRequest>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RescueRequest> data = response.body().getData();
                    if (data != null) {
                        requestList.clear();
                        requestList.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RescueRequest>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
