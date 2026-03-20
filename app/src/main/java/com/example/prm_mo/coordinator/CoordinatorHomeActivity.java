package com.example.prm_mo.coordinator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.R;

public class CoordinatorHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_home);

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.coordinator_bottom_navigation);

        // Mặc định load Dashboard
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coordinator_fragment_container, new CoordinatorDashboardFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            androidx.fragment.app.Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new CoordinatorDashboardFragment();
            } else if (itemId == R.id.nav_requests) {
                selectedFragment = new CoordinatorRequestsFragment();
            } else if (itemId == R.id.nav_missions) {
                selectedFragment = new CoordinatorMissionsFragment();
            } else if (itemId == R.id.nav_teams) {
                selectedFragment = new CoordinatorTeamsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new com.example.prm_mo.citizen.fragments.CitizenProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coordinator_fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}
