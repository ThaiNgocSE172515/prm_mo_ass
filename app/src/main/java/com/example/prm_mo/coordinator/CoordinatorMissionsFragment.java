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

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import com.example.prm_mo.models.CreateMissionInput;
import com.example.prm_mo.models.ApiResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        try {
            View view = inflater.inflate(R.layout.fragment_coordinator_missions, container, false);
            
            rvCoordinatorMissions = view.findViewById(R.id.rvCoordinatorMissions);
            if (rvCoordinatorMissions == null) {
                Log.e("CoordinatorMissions", "RecyclerView not found");
                return view;
            }
            
            rvCoordinatorMissions.setLayoutManager(new LinearLayoutManager(getContext()));
            
            missionList = new ArrayList<>();
            try {
                adapter = new MissionAdapter(missionList);
                rvCoordinatorMissions.setAdapter(adapter);
            } catch (Exception e) {
                Log.e("CoordinatorMissions", "Error creating adapter", e);
                Toast.makeText(getContext(), "Lỗi khởi tạo adapter", Toast.LENGTH_SHORT).show();
                return view;
            }

            FloatingActionButton fabAddMission = view.findViewById(R.id.fabAddMission);
            if (fabAddMission != null) {
                fabAddMission.setOnClickListener(v -> {
                    try {
                        showCreateMissionDialog();
                    } catch (Exception e) {
                        Log.e("CoordinatorMissions", "Error in FAB click", e);
                        Toast.makeText(getContext(), "Lỗi mở dialog", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            loadMissions();
            return view;
        } catch (Exception e) {
            Log.e("CoordinatorMissions", "Error in onCreateView", e);
            Toast.makeText(getContext(), "Lỗi tải màn hình", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void loadMissions() {
        try {
            if (getContext() == null) {
                Log.e("CoordinatorMissions", "Context is null");
                return;
            }

            String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
            if (token == null) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getApiService().listMissions("Bearer " + token, null, 1, 50)
                .enqueue(new Callback<MissionListResponse>() {
                    @Override
                    public void onResponse(Call<MissionListResponse> call, Response<MissionListResponse> response) {
                        try {
                            if (getContext() == null) return;
                            
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().isSuccess() && response.body().getData() != null) {
                                    missionList.clear();
                                    missionList.addAll(response.body().getData());
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Không có nhiệm vụ nào", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("CoordinatorMissions", "Error in onResponse", e);
                            Toast.makeText(getContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MissionListResponse> call, Throwable t) {
                        Log.e("CoordinatorMissions", "API call failed", t);
                        if(getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } catch (Exception e) {
            Log.e("CoordinatorMissions", "Error in loadMissions", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCreateMissionDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tạo Nhiệm Vụ Mới");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputName = new EditText(getContext());
        inputName.setHint("Tên nhiệm vụ");
        layout.addView(inputName);

        final EditText inputDescription = new EditText(getContext());
        inputDescription.setHint("Mô tả");
        layout.addView(inputDescription);

        final Spinner spinnerType = new Spinner(getContext());
        String[] types = {"RESCUE", "RELIEF"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, types);
        spinnerType.setAdapter(typeAdapter);
        layout.addView(spinnerType);

        final Spinner spinnerPriority = new Spinner(getContext());
        String[] priorities = {"Critical", "High", "Normal"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, priorities);
        spinnerPriority.setAdapter(priorityAdapter);
        layout.addView(spinnerPriority);

        builder.setView(layout);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String priority = spinnerPriority.getSelectedItem().toString();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            createMission(name, type, description, priority);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createMission(String name, String type, String description, String priority) {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        CreateMissionInput input = new CreateMissionInput(name, type, description, priority);
        RetrofitClient.getApiService().createMission("Bearer " + token, input)
                .enqueue(new Callback<ApiResponse<Mission>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Mission>> call, Response<ApiResponse<Mission>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(getContext(), "Tạo nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                            loadMissions();
                        } else {
                            try {
                                String error = "Lỗi khi tạo nhiệm vụ";
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                                Toast.makeText(getContext(), "Thất bại: " + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Mission>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
