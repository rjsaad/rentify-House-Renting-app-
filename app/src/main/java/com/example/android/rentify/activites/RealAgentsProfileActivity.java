package com.example.android.rentify.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.example.android.rentify.R;
import com.example.android.rentify.adapters.RealEstateAgentAdapter;
import com.example.android.rentify.modelclasses.RealEstateAgentModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealAgentsProfileActivity extends AppCompatActivity {

    RatingBar ratingBar;
    List<RealEstateAgentModel> agentList = new ArrayList<>();

    TextView notFoundTextView, titleTextView;

    MaterialCardView allAgents, filterLocAgents, filterRatAgents;


    List<String> locationsList, ratingList;
    RecyclerView recyclerView;

    LottieAnimationView lottieAnimationView;
    View agentLine;
    LinearLayout linearLayout;
    RealEstateAgentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_agents_profile);
        getSupportActionBar().hide();
        intiUI();
        setUpLocationCardView();
        setUpRatingCardView();

        // Assuming agentList is a List<RealEstateAgentModel> containing your data

        adapter = new RealEstateAgentAdapter(agentList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.showAllAgents();

        // Reference to your Firebase database
        DatabaseReference realEstateAgentsRef = FirebaseDatabase.getInstance().getReference().child("realEstateAgents");
        lottieAnimationView.setVisibility(View.VISIBLE);
        notFoundTextView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        filterRatAgents.setVisibility(View.GONE);
        filterLocAgents.setVisibility(View.GONE);
        allAgents.setVisibility(View.GONE);
        agentLine.setVisibility(View.GONE);
        // Retrieve data from Firebase
        realEstateAgentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    agentList.clear();

                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        RealEstateAgentModel agent = agentSnapshot.getValue(RealEstateAgentModel.class);
                        agentList.add(agent);
                    }

                    titleTextView.setVisibility(View.VISIBLE);
                    filterRatAgents.setVisibility(View.VISIBLE);
                    filterLocAgents.setVisibility(View.VISIBLE);
                    allAgents.setVisibility(View.VISIBLE);
                    agentLine.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.main_color));
                    }
                    linearLayout.setBackgroundColor(ContextCompat.getColor(RealAgentsProfileActivity.this, R.color.main_color));


                    // Update the adapter with the new data
                    adapter.notifyDataSetChanged();
                }
                lottieAnimationView.setVisibility(View.GONE);

                if (agentList.isEmpty()) {
                    notFoundTextView.setVisibility(View.VISIBLE);
                } else {
                    notFoundTextView.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
        allAgents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.showAllAgents(); // Show all agents
            }
        });

    }

    private void intiUI() {
        ratingBar = findViewById(R.id.agentRating);
        lottieAnimationView = findViewById(R.id.searchAgentAnimation);
        notFoundTextView = findViewById(R.id.agentNotFoundTextView);
        titleTextView = findViewById(R.id.titRealEstTxtView);
        filterRatAgents = findViewById(R.id.sortAgents);
        filterLocAgents = findViewById(R.id.locationAgents);
        allAgents = findViewById(R.id.showAllAgents);
        agentLine = findViewById(R.id.agentLine);
        recyclerView = findViewById(R.id.rlEstateAgentProRecyclerview);
        allAgents = findViewById(R.id.showAllAgents);
        linearLayout = findViewById(R.id.realEstateAgentRoot);
    }

    private void setUpLocationCardView() {
        filterLocAgents.setOnClickListener(view -> showCityListDialog());
    }

    private void setUpRatingCardView() {
        filterRatAgents.setOnClickListener(view -> showRatingDialog());
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select rating");

        final String[] ratingArray = getRatingList().toArray(new String[0]);

        builder.setItems(ratingArray, (dialog, which) -> {
            String selectedRating = ratingArray[which];
            adapter.filterAgentsByRating(Float.parseFloat(selectedRating));
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCityListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a City");

        final String[] citiesArray = getLocationsList().toArray(new String[0]);

        builder.setItems(citiesArray, (dialog, which) -> {
            String selectedCity = citiesArray[which];
            adapter.filterAgentsByLocation(selectedCity);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> getLocationsList() {
        if (locationsList == null) {
            locationsList = new ArrayList<>();
            locationsList.add("Bagh");
            locationsList.add("Bhimber");
            locationsList.add("Hattian Bala");
            locationsList.add("Haveli");
            locationsList.add("Kotli");
            locationsList.add("Muzaffarabad");
            locationsList.add("Neelum");
            locationsList.add("Poonch");
            locationsList.add("Rawalakot");
            locationsList.add("Sudhanoti");
        }
        return locationsList;
    }

    private List<String> getRatingList() {
        if (ratingList == null) {
            ratingList = new ArrayList<>();
            ratingList.add("0.5");
            ratingList.add("1");
            ratingList.add("1.5");
            ratingList.add("2");
            ratingList.add("2.5");
            ratingList.add("3");
            ratingList.add("3.5");
            ratingList.add("4");
            ratingList.add("4.5");
            ratingList.add("5");
        }
        return ratingList;
    }


}