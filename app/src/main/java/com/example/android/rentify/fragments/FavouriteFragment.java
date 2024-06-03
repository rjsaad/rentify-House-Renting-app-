package com.example.android.rentify.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.rentify.R;
import com.example.android.rentify.adapters.HomeAdapter;
import com.example.android.rentify.modelclasses.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {

    List<HomeModel> homeModelList;
    LottieAnimationView lottieAnimationView ;

    private TextView notFoundTextView , titleTxtView ;

    private RecyclerView recyclerView ;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        iniUI(view);



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HomeModel> modelClassList = new ArrayList<>();
        homeModelList = new ArrayList<>();

        // Pass the FavouriteFragment and modelClassList to the HomeAdapter constructor
        HomeAdapter homeAdapter = new HomeAdapter(FavouriteFragment.this, modelClassList);
        recyclerView.setAdapter(homeAdapter);

        lottieAnimationView.setVisibility(View.VISIBLE);
        notFoundTextView.setVisibility(View.GONE);
        titleTxtView.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("FavouriteFragment", "User ID: " + userId);

            // Create a reference to the user's favorite houses in Firebase
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favourites");
            favoritesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HomeModel homeModel = snapshot.getValue(HomeModel.class);
                        if (homeModel != null && homeModel.getUserId().equals(userId)) {

                            Log.d("FavouriteFragment", "PropertyType: " + homeModel.getPropertyType());
                            modelClassList.add(homeModel);
                            homeModelList.add(homeModel);

                        }
                    }

                        homeAdapter.notifyDataSetChanged();

                        lottieAnimationView.setVisibility(View.GONE);
                        if (modelClassList.isEmpty()) {
                            notFoundTextView.setVisibility(View.VISIBLE);
                            notFoundTextView.setText("No data found.");
                            titleTxtView.setVisibility(View.GONE);
                        } else {
                            notFoundTextView.setVisibility(View.GONE);
                            titleTxtView.setVisibility(View.VISIBLE);
                        }
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavouriteFragment", "Error reading favorites", error.toException());
                }
            });
        }else {
            Log.e("FavouriteFragment", "User is null");
        }
            return view;
        }

        private void iniUI (View view)
        {
            titleTxtView = view.findViewById(R.id.titleFavHouse);
            lottieAnimationView = view.findViewById(R.id.favSearchAnimation);
            notFoundTextView = view.findViewById(R.id.favHouseNotFound);
            recyclerView = view.findViewById(R.id.favouriteRecyclerView);
        }
    }
