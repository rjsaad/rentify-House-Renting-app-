package com.example.android.rentify.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.rentify.R;
import com.example.android.rentify.adapters.HomeAdapter;
import com.example.android.rentify.modelclasses.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyHomesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private List<HomeModel> homeModelList;
    LottieAnimationView lottieAnimationView ;
    TextView textNoTFound , titleMyPro ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_my_homes);
        getSupportActionBar().hide();
        iniUI();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerView and adapter

        homeModelList = new ArrayList<>();
        homeAdapter = new HomeAdapter(homeModelList);
        lottieAnimationView.setVisibility(View.VISIBLE);
        textNoTFound.setVisibility(View.GONE);
        titleMyPro.setVisibility(View.GONE);

        // Set up RecyclerView layout manager and adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homeAdapter);


        // Retrieve user's uploaded houses from Firebase for both Homes and Commercial
        retrieveUserHouses("Homes");
        retrieveUserHouses("Commercial");


    }

    private void retrieveUserHouses(String category) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference().child(category);

        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot propertyTypeSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot houseSnapshot : propertyTypeSnapshot.getChildren()) {
                        HomeModel homeModel = houseSnapshot.getValue(HomeModel.class);
                        if (homeModel != null && homeModel.getUserId() != null && homeModel.getUserId().equals(uid)) {
                            homeModelList.add(homeModel);
                        }
                    }
                }

                // Log the number of houses retrieved for the current category
                Log.d("NumHouses", "Number of houses retrieved for " + category + ": " + homeModelList.size());
                titleMyPro.setVisibility(View.VISIBLE);
                // Update UI based on whether there are houses or not
                homeAdapter.notifyDataSetChanged();
                lottieAnimationView.setVisibility(View.GONE);
                textNoTFound.setVisibility(View.GONE);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(MyHomesActivity.this, "Error retrieving houses for " + category + ".", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniUI () {
        lottieAnimationView = findViewById(R.id.searchAnimation);
        textNoTFound = findViewById(R.id.notFoundTextView);
        recyclerView = findViewById(R.id.myHomesRecView);
        titleMyPro = findViewById(R.id.titleMyPro);
    }
}
