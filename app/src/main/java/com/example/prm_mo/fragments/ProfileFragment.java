package com.example.prm_mo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prm_mo.MainActivity;
import com.example.prm_mo.R;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.User;
import com.example.prm_mo.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvDisplayName, tvEmail, tvPhone, tvUername;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        loadUserProfile();
        setupLogout();
        return view;
    }

    private void initViews(View view) {
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvUername = view.findViewById(R.id.tvUsername);
    }

    private void loadUserProfile() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        RetrofitClient.getApiService().getCurrentUser("Bearer " + token).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        tvDisplayName.setText(user.getDisplayName());
                        tvEmail.setText(user.getEmail());
                        tvPhone.setText(user.getPhoneNumber());
                        tvUername.setText(user.getUserName());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            SharedPrefsManager.getInstance(getContext()).clear();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
