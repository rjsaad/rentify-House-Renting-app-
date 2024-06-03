package com.example.android.rentify.activites;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.android.rentify.R;
import com.example.android.rentify.adapters.HomeAdapter;
import com.example.android.rentify.modelclasses.HomeModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HouseFilterActivity extends AppCompatActivity {
    List<HomeModel> homeModelList;
    HomeAdapter homeAdapter;
    private static final String TAG = "HouseFilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_filter);
        homeModelList = new ArrayList<>();
        List<HomeModel> modelClassList = new ArrayList<>();


        RecyclerView recyclerView = findViewById(R.id.filterRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        double initialPriceRangeValue = 0.0;
        double finalPriceRangeValue = 0.0;

// Initialize with default values


        Intent intent = getIntent();
        if (intent != null) {
            String category = intent.getStringExtra("category");
            String propertyType = intent.getStringExtra("propertyType");

            if (category != null) {
                if (category.equals("Homes")) {
                    int selectedRooms = intent.getIntExtra("selectedRoom", 0); // Default value 0
                    int selectedBathroom = intent.getIntExtra("selectedBathroom", 0); // Default value 0

                    String iniAreaRangeString = intent.getStringExtra("iniAreaRange");
                    String finAreaRangeString = intent.getStringExtra("finAreaRange");


                    double iniAreaRangeValue = 0.0;
                    double finAreaRangeValue = 0.0;


                    if (iniAreaRangeString != null && !iniAreaRangeString.isEmpty()) {
                        iniAreaRangeValue = Double.parseDouble(iniAreaRangeString);
                    }

                    if (finAreaRangeString != null && !finAreaRangeString.isEmpty()) {
                        finAreaRangeValue = Double.parseDouble(finAreaRangeString);
                    }


                    String initialPriceString = intent.getStringExtra("iniPrice");
                    if (initialPriceString != null && !initialPriceString.isEmpty()) {
                        initialPriceRangeValue = Double.parseDouble(initialPriceString);
                    }

                    // Retrieve final price range string
                    String finalPriceString = intent.getStringExtra("finalPrice");
                    if (finalPriceString != null && !finalPriceString.isEmpty()) {
                        finalPriceRangeValue = Double.parseDouble(finalPriceString);
                    }

                    Log.d(TAG, "Category: " + category);
                    Log.d(TAG, "Property Type: " + propertyType);
                    Log.d(TAG, "Selected Rooms: " + selectedRooms);
                    Log.d(TAG, "Selected Bathrooms: " + selectedBathroom);
                    Log.d(TAG, "Initial Area Range: " + iniAreaRangeValue);
                    Log.d(TAG, "Final Area Range: " + finAreaRangeValue);
                    Log.d(TAG, "Initial price value " + initialPriceRangeValue);
                    Log.d(TAG, "Final price value: " + finalPriceRangeValue);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(category).child(propertyType);

                    ArrayList<HomeModel> filteredList = new ArrayList<>();
                    homeAdapter = new HomeAdapter(filteredList, this);
                    recyclerView.setAdapter(homeAdapter);

                    double finalIniAreaRangeValue = iniAreaRangeValue;
                    double finalFinAreaRangeValue = finAreaRangeValue;
                    double finalIniPriceRangeValue = initialPriceRangeValue;
                    double finalFinPriceRangeValue = finalPriceRangeValue;
                    int finalSelectedRooms = selectedRooms;
                    int finalSelectedBathroom = selectedBathroom;
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            HomeModel homeModel = snapshot.getValue(HomeModel.class);

                            if (homeModel != null) {
                                Log.d(TAG, "onChildAdded: Home model retrieved: " + homeModel.toString());

                                int roomsValue = Integer.parseInt(homeModel.getRooms());
                                int bathroomsValue = Integer.parseInt(homeModel.getBathrooms());
                                double homeAreaValue = Double.parseDouble(homeModel.getHomeAreaSize());

                                // Retrieve home price value
                                double homePriceValue = Double.parseDouble(homeModel.getPrice());

                                Log.d(TAG, "Rooms: " + roomsValue);
                                Log.d(TAG, "Bathrooms: " + bathroomsValue);
                                Log.d(TAG, "Initial Area Range: " + finalFinAreaRangeValue);
                                Log.d(TAG, "Final Area Range: " + finalIniAreaRangeValue);
                                Log.d(TAG, "Initial price value " + finalIniPriceRangeValue);
                                Log.d(TAG, "Final price value: " + finalFinPriceRangeValue);

                                // Check if the house meets the room and bathroom criteria
                                boolean meetsRoomAndBathroomCriteria;
                                if (finalSelectedRooms >= 4 && finalSelectedBathroom >= 4) {
                                    // If selected rooms and bathrooms are both 4 or more, consider all houses meeting the criteria
                                    meetsRoomAndBathroomCriteria = true;
                                } else {
                                    // Otherwise, check if the number of rooms and bathrooms match the selected criteria
                                    meetsRoomAndBathroomCriteria = roomsValue == finalSelectedRooms && bathroomsValue == finalSelectedBathroom;
                                }

                                Log.d(TAG, "Meets Room and Bathroom Criteria: " + meetsRoomAndBathroomCriteria);

                                // Check if the house meets both room/bathroom and area criteria
                                if (meetsRoomAndBathroomCriteria && homeAreaValue >= finalIniAreaRangeValue && homeAreaValue <= finalFinAreaRangeValue
                                        && homePriceValue >= finalIniPriceRangeValue && homePriceValue <= finalFinPriceRangeValue) {
                                    // Add the home model to the filtered list
                                    filteredList.add(homeModel);
                                    Log.d(TAG, "onChildAdded: Home model added to filtered list: " + homeModel.toString());
                                }
                                homeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "onChildAdded: homeModel is null");
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            // Handle child changed
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            // Handle child removed
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            // Handle child moved
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: Database Error", error.toException());
                        }
                    });
                } else if (category.equals("Commercial")) {

                    ArrayList<HomeModel> filteredList = new ArrayList<>();
                    homeAdapter = new HomeAdapter(filteredList, this);
                    recyclerView.setAdapter(homeAdapter);

                    // Retrieve the price range and area range for commercial properties
                    String iniAreaRangeString = intent.getStringExtra("iniAreaRange");
                    String finAreaRangeString = intent.getStringExtra("finAreaRange");

                    double iniAreaRangeValue = 0.0;
                    double finAreaRangeValue = 0.0;

                    if (iniAreaRangeString != null && !iniAreaRangeString.isEmpty()) {
                        iniAreaRangeValue = Double.parseDouble(iniAreaRangeString);
                    }

                    if (finAreaRangeString != null && !finAreaRangeString.isEmpty()) {
                        finAreaRangeValue = Double.parseDouble(finAreaRangeString);
                    }



                    String initialPriceString = intent.getStringExtra("iniPrice");
                    if (initialPriceString != null && !initialPriceString.isEmpty()) {
                        initialPriceRangeValue = Double.parseDouble(initialPriceString);
                    }

                    String finalPriceString = intent.getStringExtra("finalPrice");
                    if (finalPriceString != null && !finalPriceString.isEmpty()) {
                        finalPriceRangeValue = Double.parseDouble(finalPriceString);
                    }
                    double finalIniAreaRangeValue = iniAreaRangeValue;
                    double finalFinAreaRangeValue = finAreaRangeValue;
                    double finalIniPriceRangeValue = initialPriceRangeValue;
                    double finalFinPriceRangeValue = finalPriceRangeValue;

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(category).child(propertyType);

                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            HomeModel homeModel = snapshot.getValue(HomeModel.class);

                            if (homeModel != null) {
                                double homeAreaValue = Double.parseDouble(homeModel.getHomeAreaSize());
                                double homePriceValue = Double.parseDouble(homeModel.getPrice());

                                if (homeAreaValue >= finalIniAreaRangeValue && homeAreaValue <= finalFinAreaRangeValue
                                        && homePriceValue >= finalIniPriceRangeValue && homePriceValue <= finalFinPriceRangeValue) {
                                    filteredList.add(homeModel);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                        // Other ChildEventListener methods...
                    });
                }
            }

        }
    }

}

