package com.example.prm_mo.citizen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.prm_mo.R;
import com.example.prm_mo.citizen.fragments.CitizenHomeFragment;
import com.example.prm_mo.citizen.fragments.CitizenProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CitizenHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CitizenHomeFragment()).commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new CitizenHomeFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new CitizenProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }
}
