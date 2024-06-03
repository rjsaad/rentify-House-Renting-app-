package com.example.android.rentify.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.rentify.R;
import com.example.android.rentify.activites.HouseFilterActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;


public class SearchFragment extends Fragment {
    private Button[] homeBtn, commBtn, roomsBtn, bathRoomsBtn;
    private String selectedPropertyType, category, selectedRoom, selectedBathroom;

    private Button backBtn;

    private EditText iniAreaRange, finAreaRange, iniPriceRange, finalPriceRange;
    private TextView homesTextView, commercialTextView, resetBtn, roomsTitle, bathroomsTitle;
    View roomsView, bathroomsView;


    HorizontalScrollView homesHorLayout, commercialHorLayout;

    SearchableSpinner searchableSpinner;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        inatalizeViews(view);
        setButtonClickListeners(view);

        // Manually trigger the click event for "Homes" text view
        homesTextView.performClick();
        return view;
    }

    private void inatalizeViews(View view) {
        homeBtn = new Button[]{
                view.findViewById(R.id.allPropBtn),
                view.findViewById(R.id.apparPropBtn),
                view.findViewById(R.id.firstFloorPropBtn),
                view.findViewById(R.id.farmPropBtn),
                view.findViewById(R.id.pentPropBtn),
                view.findViewById(R.id.townPropBtn),
                view.findViewById(R.id.flatPropBtn)
        };
        commBtn = new Button[]{
                view.findViewById(R.id.allcommPropBtn),
                view.findViewById(R.id.hostelPropBtn),
                view.findViewById(R.id.mallsPropBtn),
                view.findViewById(R.id.smallofficesPropBtn),
                view.findViewById(R.id.officesPropBtn),
                view.findViewById(R.id.smallShopsPropBtn),
                view.findViewById(R.id.shopsPropBtn)
        };
        roomsBtn = new Button[]{
                view.findViewById(R.id.oneBedBtn),
                view.findViewById(R.id.twoBedBtn),
                view.findViewById(R.id.threeBedBtn),
                view.findViewById(R.id.fourBedBtn)
        };
        bathRoomsBtn = new Button[]{
                view.findViewById(R.id.oneBathBtn),
                view.findViewById(R.id.twoBathBtn),
                view.findViewById(R.id.threeBathBtn),
                view.findViewById(R.id.fourBathBtn)
        };

        homesHorLayout = view.findViewById(R.id.homeshorlayout);
        commercialHorLayout = view.findViewById(R.id.commercialHorLayout);
        backBtn = view.findViewById(R.id.backButton);
        homesTextView = view.findViewById(R.id.homesSearchTxtView);
        commercialTextView = view.findViewById(R.id.commercialSearchTxtView);
        resetBtn = view.findViewById(R.id.resetBtn);
        iniAreaRange = view.findViewById(R.id.intFragAreaRange);
        finAreaRange = view.findViewById(R.id.finalFragAreaRange);
        searchableSpinner = view.findViewById(R.id.rangeSearchableSpinner);
        iniPriceRange = view.findViewById(R.id.iniPriceRange);
        finalPriceRange = view.findViewById(R.id.finalPricRange);
        roomsTitle = view.findViewById(R.id.roomsTitle);
        bathroomsTitle = view.findViewById(R.id.bathroomsTitle);
        roomsView = view.findViewById(R.id.roomsView);
        bathroomsView = view.findViewById(R.id.bathroomsView);


    }


    private void setButtonClickListeners(View view) {
        for (Button button : homeBtn) {
            setPropertyTypeButtonColor(button);
        }

        for (Button button : commBtn) {
            setPropertyTypeButtonColor(button);
        }


        for (Button button : roomsBtn) {
            button.setOnClickListener(v -> {
                selectedRoom = button.getText().toString();
                updateRoomButtonColors();
            });
        }

        for (Button button : bathRoomsBtn) {
            button.setOnClickListener(v -> {
                selectedBathroom = button.getText().toString();
                updateBathroomButtonColors();
            });
        }


        Button searchButton = view.findViewById(R.id.srchFiltrBtn); // Replace with your actual search button ID
        searchButton.setOnClickListener(v -> {

            String iniAreaRangeValue = iniAreaRange.getText().toString();
            String finAreaRangeValue = finAreaRange.getText().toString();
            String iniPriceValue = iniPriceRange.getText().toString();
            String finalPriceValue = finalPriceRange.getText().toString();

            final Object selectedItem = searchableSpinner.getSelectedItem();
            final String searchableSpinnerValue = selectedItem != null ? selectedItem.toString() : "";
            if (category == null ||
                    iniAreaRangeValue == null || finAreaRangeValue == null ||
                    iniPriceValue == null || finalPriceValue == null || searchableSpinnerValue == null) {
                Toast.makeText(getContext(), "Some fields are null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.equals("Homes")) {

                startPropertyListActivityHomes(category, selectedPropertyType, selectedRoom, iniAreaRangeValue, finAreaRangeValue, searchableSpinnerValue, iniPriceValue, finalPriceValue);
            } else {

                startPropertyListActivityCommercial(category, selectedPropertyType, iniAreaRangeValue, finAreaRangeValue, searchableSpinnerValue, iniPriceValue, finalPriceValue);
            }
        });


        backBtn.setOnClickListener(v -> navigateToFragment(new HomeFragment()));

        homesTextView.setOnClickListener(v -> {
            homesHorLayout.setVisibility(View.VISIBLE);
            commercialHorLayout.setVisibility(View.GONE);
            showRoomsAndBathrooms();

            homesTextView.setTextColor(getResources().getColor(R.color.your_selected_color));
            homesTextView.setTypeface(null, Typeface.BOLD);

            commercialTextView.setTextColor(getResources().getColor(R.color.your_unselected_color));
            commercialTextView.setTypeface(null, Typeface.NORMAL);
        });

        commercialTextView.setOnClickListener(v -> {
            homesHorLayout.setVisibility(View.GONE);
            commercialHorLayout.setVisibility(View.VISIBLE);
            hideRoomsAndBathrooms();

            homesTextView.setTextColor(getResources().getColor(R.color.your_unselected_color));
            homesTextView.setTypeface(null, Typeface.NORMAL);


            commercialTextView.setTextColor(getResources().getColor(R.color.your_selected_color));
            commercialTextView.setTypeface(null, Typeface.BOLD);
        });
        resetBtn.setOnClickListener(v -> resetButtonColors());
    }

    private void resetButtonColors() {
        resetButtonGroupColors(homeBtn);
        resetButtonGroupColors(commBtn);
        resetButtonGroupColors(roomsBtn);
        resetButtonGroupColors(bathRoomsBtn);
    }

    private void resetButtonGroupColors(Button[] buttons) {
        for (Button button : buttons) {
            button.setTextColor(getResources().getColor(R.color.your_unselected_color));
            finalPriceRange.setText("");
            iniPriceRange.setText("");
            finAreaRange.setText("");
            iniAreaRange.setText("");
            button.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setPropertyTypeButtonColor(Button button) {
        button.setOnClickListener(v -> {
            selectedPropertyType = button.getText().toString();
            category = "";
            if (isHomeLayoutSelected()) {
                category = "Homes";
                updatePropertyTypeButtonColors(homeBtn);
            } else if (isCommercialLayoutSelected()) {
                category = "Commercial";
                updatePropertyTypeButtonColors(commBtn);
            }
        });
    }

    private boolean isHomeLayoutSelected() {
        return homesHorLayout.getVisibility() == View.VISIBLE;
    }

    private boolean isCommercialLayoutSelected() {
        return commercialHorLayout.getVisibility() == View.VISIBLE;
    }

    private void updatePropertyTypeButtonColors(Button[] buttons) {
        for (Button button : buttons) {
            if (button.getText().toString().equalsIgnoreCase(selectedPropertyType)) {
                button.setTextColor(getResources().getColor(R.color.your_selected_color));
                button.setTypeface(null, Typeface.BOLD);
            } else {
                button.setTextColor(getResources().getColor(R.color.your_unselected_color));
                button.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void updateRoomButtonColors() {
        for (Button button : roomsBtn) {
            if (button.getText().toString().equalsIgnoreCase(selectedRoom)) {
                button.setTextColor(getResources().getColor(R.color.your_selected_color));
                button.setTypeface(null, Typeface.BOLD);
            } else {
                button.setTextColor(getResources().getColor(R.color.your_unselected_color));
                button.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void hideRoomsAndBathrooms() {
        // Hide rooms and bathrooms views
        for (Button button : roomsBtn) {
            button.setVisibility(View.GONE);
        }
        for (Button button : bathRoomsBtn) {
            button.setVisibility(View.GONE);
        }
        roomsTitle.setVisibility(View.GONE);
        bathroomsTitle.setVisibility(View.GONE);
        roomsView.setVisibility(View.GONE);
        bathroomsView.setVisibility(View.GONE);
    }

    private void showRoomsAndBathrooms() {
        // Hide rooms and bathrooms views
        for (Button button : roomsBtn) {
            button.setVisibility(View.VISIBLE);
        }
        for (Button button : bathRoomsBtn) {
            button.setVisibility(View.VISIBLE);
        }
        roomsTitle.setVisibility(View.VISIBLE);
        bathroomsTitle.setVisibility(View.VISIBLE);
        roomsView.setVisibility(View.VISIBLE);
        bathroomsView.setVisibility(View.VISIBLE);
    }

    private void updateBathroomButtonColors() {
        for (Button button : bathRoomsBtn) {
            if (button.getText().toString().equalsIgnoreCase(selectedBathroom)) {
                button.setTextColor(getResources().getColor(R.color.your_selected_color));
                button.setTypeface(null, Typeface.BOLD);
            } else {
                button.setTextColor(getResources().getColor(R.color.your_unselected_color));
                button.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void startPropertyListActivityHomes(String category, String propertyType, String selectedRoom, String iniRangeVal, String finRangeVal, String searchableSpinnerValue, String iniPrice, String finalPrice) {

            Intent intent = new Intent(getActivity(), HouseFilterActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("propertyType", propertyType);
            intent.putExtra("selectedRoom", Integer.parseInt(selectedRoom));
            intent.putExtra("selectedBathroom", Integer.parseInt(selectedBathroom));
            intent.putExtra("iniAreaRange", iniRangeVal);
            intent.putExtra("finAreaRange", finRangeVal);
            intent.putExtra("searchableSpinnerValue", searchableSpinnerValue);
            intent.putExtra("iniPrice", iniPrice);
            intent.putExtra("finalPrice", finalPrice);
            startActivity(intent);
        }



    private void startPropertyListActivityCommercial(String category, String propertyType, String iniRangeVal, String finRangeVal, String searchableSpinnerValue, String iniPrice, String finalPrice) {
        {
             {
                Intent intent = new Intent(getActivity(), HouseFilterActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("propertyType", propertyType);
                intent.putExtra("iniAreaRange", iniRangeVal);
                intent.putExtra("finAreaRange", finRangeVal);
                intent.putExtra("searchableSpinnerValue", searchableSpinnerValue);
                intent.putExtra("iniPrice", iniPrice);
                intent.putExtra("finalPrice", finalPrice);
                startActivity(intent);
            }
        }

    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_frag, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}



