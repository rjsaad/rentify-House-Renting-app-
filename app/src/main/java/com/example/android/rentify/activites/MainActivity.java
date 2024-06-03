package com.example.android.rentify.activites;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import com.example.android.rentify.R;
import com.example.android.rentify.databinding.ActivityMainBinding;
import com.example.android.rentify.fragments.FavouriteFragment;
import com.example.android.rentify.fragments.HomeFragment;
import com.example.android.rentify.fragments.AddFragment;
import com.example.android.rentify.fragments.ProfileFragment;
import com.example.android.rentify.fragments.SearchFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    DrawerLayout drawerLayout ;
    NavigationView navigationView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.meowbottomnavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_home_24));
        activityMainBinding.meowbottomnavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_search_24));
        activityMainBinding.meowbottomnavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_add_24));
        activityMainBinding.meowbottomnavigation.add(new MeowBottomNavigation.Model(4, R.drawable.baseline_favorite_24));
        activityMainBinding.meowbottomnavigation.add(new MeowBottomNavigation.Model(5, R.drawable.profile_icon));

        activityMainBinding.meowbottomnavigation.setOnClickMenuListener(item -> {


            switch (item.getId()) {
                case 1:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    HomeFragment homeFragment = new HomeFragment();
                    LoadFragment(new HomeFragment(), true);
                    transaction.replace(activityMainBinding.linearlayoutFrag.getId(), homeFragment);
                    transaction.commit();
                    break;
                case 2:
                    LoadFragment(new SearchFragment(), false);
                    break;
                case 3:
                    LoadFragment(new AddFragment(), false);
                    break;
                case 4:
                    LoadFragment(new FavouriteFragment(), false);
                    break;
                case 5:
                    LoadFragment(new ProfileFragment(), false);
                    break;
                default:
            }

        });
        activityMainBinding.meowbottomnavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model model) {
                // Handle show event (optional)
            }
        });
        activityMainBinding.meowbottomnavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                // Toast.makeText(MainActivity.this, "Item reselected: " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });


        LoadFragment(new HomeFragment(), false);
        activityMainBinding.meowbottomnavigation.show(1, false);

        checkAuthentication();
    }

    public void LoadFragment(Fragment fragment, Boolean flag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (flag) {
            transaction.add(activityMainBinding.linearlayoutFrag.getId(), fragment);
        } else {
            transaction.replace(activityMainBinding.linearlayoutFrag.getId(), fragment);
        }
        transaction.commit();
    }


    private void checkAuthentication() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentUser != null) {
            // User is logged in, navigate to the home fragment
            transaction.replace(activityMainBinding.linearlayoutFrag.getId(), new HomeFragment());
        } else {
            // User is not logged in, navigate to the login fragment or activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        transaction.commit();
    }
}

