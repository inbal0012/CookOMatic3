package com.example.adopy.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.adopy.R;
import com.example.adopy.ui.myPets.MyPetsFragment;
import com.example.adopy.ui.profile.ProfileFragment;
import com.example.adopy.ui.favorites.FavoritesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MyPetsActivity extends AppCompatActivity {

    private static final String TAG = "MyPetsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(navLister);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new ProfileFragment()).commit();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navLister = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            FragmentManager fm = getSupportFragmentManager();
            Log.d(TAG, "onNavigationItemSelected: " + fm.getPrimaryNavigationFragment());
            fm.beginTransaction().remove(fm.getPrimaryNavigationFragment());
            Log.d(TAG, "onNavigationItemSelected: 1 " + fm.getPrimaryNavigationFragment());

            switch (menuItem.getItemId()) {
                case R.id.navigation_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.navigation_my_pets:
                    selectedFragment = new MyPetsFragment();
                    break;
                case R.id.navigation_my_favorites:
                    selectedFragment = new FavoritesFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment).commit();

            return true;
        }
    };

}
