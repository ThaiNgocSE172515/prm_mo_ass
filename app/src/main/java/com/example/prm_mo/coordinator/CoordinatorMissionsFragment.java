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
import com.example.prm_mo.adapters.MissionAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.MissionListResponse;
import com.example.prm_mo.models.Mission;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorMissionsFragment extends Fragment {

    private RecyclerView rvCoordinatorMissions;
    private MissionAdapter adapter;
    private List<Mission> missionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_missions, container, false);
        
        rvCoordinatorMissions = view.findViewById(R.id.rvCoordinatorMissions);
        rvCoordinatorMissions.setLayoutManager(new LinearLayoutManager(getContext()));
        
        missionList = new ArrayList<>();
        adapter = new MissionAdapter(missionList);
        rvCoordinatorMissions.setAdapter(adapter);

        loadMissions();
        return view;
    }

    private void loadMissions() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        RetrofitClient.getApiService().listMissions("Bearer " + token, null, 1, 50)
            .enqueue(new Callback<MissionListResponse>() {
                @Override
                public void onResponse(Call<MissionListResponse> call, Response<MissionListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess() && response.body().getData() != null) {
                            missionList.clear();
                            missionList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MissionListResponse> call, Throwable t) {
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "Không tải được danh sách Mission", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
