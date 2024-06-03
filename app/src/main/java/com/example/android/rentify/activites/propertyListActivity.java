package com.example.android.rentify.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.rentify.R;
import com.example.android.rentify.adapters.HomeAdapter;
import com.example.android.rentify.fragments.HomeFragment;
import com.example.android.rentify.fragments.SearchFragment;
import com.example.android.rentify.modelclasses.HomeModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class propertyListActivity extends AppCompatActivity {

    List<HomeModel> homeModelList;
    List<String> locationsList , roomsList , bathroomList;;
    HomeAdapter homeAdapter;
    TextView notFoundTextView , titleProType , titleProExtra;
    LottieAnimationView lottieAnimationView ;
    View homesLine ;
    LinearLayout linearLayout ;

    RecyclerView recyclerView ;
    MaterialCardView filtersFrag , housesLocation, housesRooms , housesBaths ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        setContentView(R.layout.activity_property_list);
        getSupportActionBar().hide();
        initiUI();
        setUpLocationCardView();
        setUpBathroomsCardView();
        setUpRoomsCardView();


        homeModelList = new ArrayList<>();
        List<HomeModel> modelClassList = new ArrayList<>();




        Intent intent = getIntent();
        if (intent != null) {
            String category = intent.getStringExtra("category");
            String propertyType = intent.getStringExtra("propertyType");
            titleProType.setText(propertyType);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(category).child(propertyType);

            ArrayList<HomeModel> filteredList = new ArrayList<>();
            homeAdapter = new HomeAdapter(filteredList,this );
            recyclerView.setAdapter(homeAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            lottieAnimationView.setVisibility(View.VISIBLE);
            notFoundTextView.setVisibility(View.GONE);
            filtersFrag.setVisibility(View.GONE);
            housesLocation.setVisibility(View.GONE);
            housesBaths.setVisibility(View.GONE);
            housesRooms.setVisibility(View.GONE);
            titleProType.setVisibility(View.GONE);
            titleProExtra.setVisibility(View.GONE);
            homesLine.setVisibility(View.GONE);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method will be called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HomeModel homeModel = snapshot.getValue(HomeModel.class);
                        if (homeModel != null) {
                            filteredList.add(homeModel);
                        }
                    }
                    filtersFrag.setVisibility(View.VISIBLE);
                    housesLocation.setVisibility(View.VISIBLE);
                    if (("Commercial" +
                            "").equals(category)) {
                        housesBaths.setVisibility(View.GONE);
                        housesRooms.setVisibility(View.GONE);
                    } else {
                        housesBaths.setVisibility(View.VISIBLE);
                        housesRooms.setVisibility(View.VISIBLE);
                    }
                    titleProType.setVisibility(View.VISIBLE);
                    titleProExtra.setVisibility(View.VISIBLE);
                    homesLine.setVisibility(View.VISIBLE);
                    linearLayout.setBackgroundColor(ContextCompat.getColor(propertyListActivity.this, R.color.main_color));

                    homeAdapter.notifyDataSetChanged();
                    lottieAnimationView.setVisibility(View.GONE);

                    if (filteredList.isEmpty()) {
                        notFoundTextView.setVisibility(View.VISIBLE);
                        notFoundTextView.setText("No data found.");
                    } else {
                        notFoundTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    lottieAnimationView.setVisibility(View.GONE);
                    notFoundTextView.setVisibility(View.VISIBLE);


                }
            });

        }
        filtersFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeAdapter.showAllAgents(); // Show all agents
            }
        });
    }

    private void setUpLocationCardView() {
        housesLocation.setOnClickListener(view -> showCityListDialog());
    }
    private void setUpRoomsCardView() {
        housesRooms.setOnClickListener(view -> showRoomsListDialog());
    }

    private void setUpBathroomsCardView() {
        housesBaths.setOnClickListener(view -> showBathroomsListDialog());
    }
    private void showRoomsListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Rooms");

        final String[] roomsArray = getRoomsList().toArray(new String[0]);

        builder.setItems(roomsArray, (dialog, which) -> {
            String selectedRooms = roomsArray[which];
            homeAdapter.filterHousesByRooms(selectedRooms);
            homeAdapter.notifyDataSetChanged();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showBathroomsListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Bathrooms");

        final String[] bathroomsArray = getBathroomsList().toArray(new String[0]);

        builder.setItems(bathroomsArray, (dialog, which) -> {
            String selectedBathrooms = bathroomsArray[which];
            homeAdapter.filterHousesByBathrooms(selectedBathrooms);
            homeAdapter.notifyDataSetChanged();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private List<String> getRoomsList() {
        // Generate a list of room numbers from 1 to 10
        if (roomsList == null) {
            roomsList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                roomsList.add(String.valueOf(i));
            }
        }
        return roomsList;
    }

    private List<String> getBathroomsList() {
        // Generate a list of bathroom numbers from 1 to 10
        if (bathroomList == null) {
            bathroomList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                bathroomList.add(String.valueOf(i));
            }
        }
        return bathroomList;
    }
    private void showCityListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a City");

        final String[] citiesArray = getLocationsList().toArray(new String[0]);

        builder.setItems(citiesArray, (dialog, which) -> {
            String selectedCity = citiesArray[which];
            homeAdapter.filterHousesByLocation(selectedCity);
            // No need to create a new adapter, just notify the existing one
            homeAdapter.notifyDataSetChanged();
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
    private void initiUI()
    {
        filtersFrag = findViewById(R.id.filtersFrag);
        housesLocation = findViewById(R.id.housesLocation);
        housesRooms = findViewById(R.id.housesRooms);
        housesBaths = findViewById(R.id.housesBaths);
        lottieAnimationView = findViewById(R.id.searchAnimation);
        notFoundTextView = findViewById(R.id.notFoundTextView);
        titleProType = findViewById(R.id.titleProType);
        titleProExtra = findViewById(R.id.titleProExtra);
        homesLine = findViewById(R.id.homesLine);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayout = findViewById(R.id.activityPropertyList);

    }


}


