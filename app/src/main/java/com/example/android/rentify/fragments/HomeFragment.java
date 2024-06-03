package com.example.android.rentify.fragments;


import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.android.rentify.R;
import com.example.android.rentify.activites.RealAgentsProfileActivity;
import com.example.android.rentify.activites.propertyListActivity;
import com.google.android.material.textfield.TextInputEditText;


public class HomeFragment extends Fragment implements View.OnClickListener {

    EditText searchView;
    private Button postProperty , rlEstAgentPro ;

    private String category = null;

    private TextView[] homeTextViews, commTextViews;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeTextViews(view);



        postProperty = view.findViewById(R.id.postProperty);
        postProperty.setOnClickListener(view1 -> navigateToFragment(new AddFragment()));

        // Search view for moving
        searchView = view.findViewById(R.id.homeFragSearhview);
        searchView.setOnClickListener(v -> navigateToFragment(new AddFragment()));

        // move to real Estate agents profile
        rlEstAgentPro= view.findViewById(R.id.seeRealEstateAgent);
        rlEstAgentPro.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , RealAgentsProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }



    private void initializeTextViews(View view) {
        homeTextViews = new TextView[]{
                view.findViewById(R.id.homesApartment),
                view.findViewById(R.id.homesFlat),
                view.findViewById(R.id.homesFirstFloor),
                view.findViewById(R.id.homesFarmHouse),
                view.findViewById(R.id.homesPent),
                view.findViewById(R.id.homesTown)
        };

        commTextViews = new TextView[]{
                view.findViewById(R.id.commHostel),
                view.findViewById(R.id.commMalls),
                view.findViewById(R.id.commSmallOffices),
                view.findViewById(R.id.commOffices),
                view.findViewById(R.id.commSmallShops),
                view.findViewById(R.id.commShops)
        };

        setTextViewClickListener(homeTextViews, "Homes");
        setTextViewClickListener(commTextViews, "Commercial");
    }

    private void setTextViewClickListener(TextView[] textViews, String category) {
        for (TextView textView : textViews) {
            textView.setOnClickListener(this);
            textView.setTag(category);
        }
    }

    @Override
    public void onClick(View view) {
        String propertyType = ((TextView) view).getText().toString();
        category = (String) view.getTag();
        filterDataAndStartActivity(category, propertyType);
    }

    private void filterDataAndStartActivity(String category, String propertyType) {
        Intent intent = new Intent(getActivity(), propertyListActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("propertyType", propertyType);
        startActivity(intent);
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_frag, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}

