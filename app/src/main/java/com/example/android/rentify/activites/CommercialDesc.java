package com.example.android.rentify.activites;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.example.android.rentify.R;
import com.example.android.rentify.adapters.ImageSliderAdapter;
import com.example.android.rentify.modelclasses.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;


import java.util.ArrayList;
import java.util.List;

public class CommercialDesc extends AppCompatActivity {

    TextView cityTV, commPrice, commAddress, commRooms, commsBathrooms, descripition, commPhNo1, commPhNo2,
            commEmail, commProperty, commHomeRange, commHomeUnit;
    Button composeEmail, dialPhone;

    ToggleButton addFavrtHouse;
    private String imageUri, price, address, rooms, bathRooms, phoneNumber1, phoneNumber2, descriptionTextView, emailAddress, city,
            property, homeArea, homeUnit, documentId;
    private ViewPager2 viewPager;
    private PageIndicatorView pageIndicatorView;
    private ImageSliderAdapter imageSliderAdapter;
    private List<String> imageUris;

    private  String category ;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOGGLE_BUTTON_COLOR_KEY = "toggle_button_color";
    private boolean currentUserIsOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        setContentView(R.layout.activity_commercial_desc);
        getSupportActionBar().hide();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        addFavrtHouse = findViewById(R.id.addfavouriteHouse);

        // Set the initial color based on the stored value
        int storedColor = preferences.getInt(TOGGLE_BUTTON_COLOR_KEY, R.color.your_unselected_color);
        addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(this, storedColor));

        viewPager = findViewById(R.id.viewPager);
        pageIndicatorView = findViewById(R.id.pageindicatorview);



        Intent intent = getIntent();
        if (intent != null) {
            imageUris = intent.getStringArrayListExtra("imageUris");
            price = intent.getStringExtra("priceTV");
            address = intent.getStringExtra("addressTV");
            property = intent.getStringExtra("propertyType");
            emailAddress = intent.getStringExtra("emailTV");
            phoneNumber1 = intent.getStringExtra("phNoTV");
            phoneNumber2 = intent.getStringExtra("phNo2TV");
            homeArea = intent.getStringExtra("areaRange");
            homeUnit = intent.getStringExtra("areaUnit");
            documentId = intent.getStringExtra("documentId");

            setupImageSlider();

            commPrice = findViewById(R.id.commPriceAc);
            commAddress = findViewById(R.id.commAddressAc);
            commProperty = findViewById(R.id.commProTypAc);
            commEmail = findViewById(R.id.commEmail);
            commPhNo1 = findViewById(R.id.commPhNO);
            commPhNo2 = findViewById(R.id.commPhNO2);
            commHomeRange = findViewById(R.id.commAreaRange);
            commHomeUnit = findViewById(R.id.commAreaUnit);


            commPrice.setText(price);
            commAddress.setText(address);
            commProperty.setText(property);
            commAddress.setText(emailAddress);
            commPhNo1.setText(phoneNumber1);
            commPhNo2.setText(phoneNumber2);
            commHomeRange.setText(homeArea);
            commHomeUnit.setText(homeUnit);

            if (imageUris != null && !imageUris.isEmpty()) {
                pageIndicatorView.setCount(imageUris.size());
            }

            // Add this block to update PageIndicatorView when the page changes
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    pageIndicatorView.setSelection(position);
                }
            });
        }
        //email intent
        composeEmail = findViewById(R.id.composeEmail);
        composeEmail.setOnClickListener(view -> openApp(Intent.ACTION_SENDTO, "mailto:"));
        //dial phone intent
        dialPhone = findViewById(R.id.dialPhone);
        dialPhone.setOnClickListener(view -> openApp(Intent.ACTION_DIAL, "tel:"));

        //add favourite house to firebase
        addFavrtHouse = findViewById(R.id.addfavouriteHouse);

        final boolean[] isFavorite = {false};
        addFavrtHouse.setOnClickListener(view -> {
            // Get the user ID from Firebase Authentication
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference favoritesRef = database.getReference("Favourites");

                // Check if the commercial property is already a favorite
                favoritesRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String propertyKey = null;

                        // Check if the commercial property is already a favorite
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HomeModel commercialProperty = snapshot.getValue(HomeModel.class);
                            if (commercialProperty != null && commercialProperty.getUserId().equals(userId)) {
                                // The commercial property is already a favorite, remove it
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CommercialDesc.this, "Property removed from favorites", Toast.LENGTH_SHORT).show();

                                            // Toggle the state of the favorite button
                                            isFavorite[0] = !isFavorite[0];

                                            // Change the backgroundTintList based on the state
                                            int colorResId = isFavorite[0] ? R.color.your_selected_color : R.color.your_unselected_color;
                                            addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(CommercialDesc.this, colorResId));
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the failure to remove the commercial property
                                            Toast.makeText(CommercialDesc.this, "Error removing property from favorites", Toast.LENGTH_SHORT).show();
                                        });
                                isFavorite[0] = true;
                                propertyKey = snapshot.getKey();
                                break;
                            }
                        }

                        if (!isFavorite[0]) {
                            // The property is not a favorite, add it
                            String newFavoriteKey = favoritesRef.push().getKey();

                            HomeModel newFavorite;

                            // Check the property type and use the appropriate constructor
                            if ("Apartment".equals(property) || "First Floor".equals(property) ||
                                    "Flat".equals(property) || "Farm House".equals(property)
                                    || "Pent House".equals(property) || "Town House".equals(property)) {
                                // Use the constructor for homes
                                newFavorite = new HomeModel(documentId, address, bathRooms, city, descriptionTextView, emailAddress, imageUris,
                                        phoneNumber1, phoneNumber2, price, property, rooms, userId, homeArea, homeUnit);
                            } else {
                                // Use the constructor for commercial properties
                                newFavorite = new HomeModel(documentId, address, city, price, imageUris, property, userId,
                                        homeArea, homeUnit, emailAddress, phoneNumber1, phoneNumber2);
                            }

                            favoritesRef.child(newFavoriteKey).setValue(newFavorite)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CommercialDesc.this, "Property added to favorites", Toast.LENGTH_SHORT).show();

                                        // Toggle the state of the favorite button
                                        isFavorite[0] = !isFavorite[0];

                                        // Change the backgroundTintList based on the state
                                        int colorResId = isFavorite[0] ? R.color.your_selected_color : R.color.your_unselected_color;
                                        addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(CommercialDesc.this, colorResId));

                                        // Save the selected color in SharedPreferences
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt(TOGGLE_BUTTON_COLOR_KEY, colorResId);
                                        editor.apply();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure to add the property to favorites
                                        Toast.makeText(CommercialDesc.this, "Error adding property to favorites", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                        Toast.makeText(CommercialDesc.this, "Error checking favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // User not authenticated, handle accordingly (e.g., redirect to login)
                Toast.makeText(CommercialDesc.this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkOwnership();
    }

    private void openApp(String action, String data) {
        // Create an intent with the specified action and data
        Intent intent = new Intent(action);
        intent.setData(Uri.parse(data));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line

        // Check if there's an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the activity with the intent
            startActivity(intent);
        }
    }

    private void setupImageSlider() {
        // Set up the ViewPager2
        if (imageUris != null) {
            imageSliderAdapter = new ImageSliderAdapter(this, imageUris);
            viewPager.setAdapter(imageSliderAdapter);

        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case R.id.action_update:
                // Handle Update action
                update();
                return true;

            case R.id.action_delete:
                // Handle Delete action
                showDeleteConfirmationDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void checkOwnership() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference houseRef = database.getReference("Homes").child(userId);

            houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HomeModel house = dataSnapshot.getValue(HomeModel.class);
                    if (house != null && house.getUserId().equals(userId)) {
                        currentUserIsOwner = true ;

                        // Current user is the owner, show update and delete options
                        invalidateOptionsMenu();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem updateItem = menu.findItem(R.id.action_update);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);

        // Set visibility based on ownership
        updateItem.setVisible(currentUserIsOwner);
        deleteItem.setVisible(currentUserIsOwner);

        return super.onPrepareOptionsMenu(menu);
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this house?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User clicked Yes, delete the house from Firebase
                    deleteFromFirebase();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User clicked No, do nothing or handle accordingly
                })
                .show();
    }

    private void deleteFromFirebase() {
        // Get the reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(category).child(property);

        // Use the reference to remove the specific house data based on documentId
        databaseReference.child(documentId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Handle the success case (house deleted)
                    Toast.makeText(CommercialDesc.this, "House deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can finish the activity or navigate to another screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case (error deleting house)
                    Toast.makeText(CommercialDesc.this, "Error deleting house from Firebase", Toast.LENGTH_SHORT).show();
                });
    }


    private void update() {

        category = "Commercial";

        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("price", price);
        intent.putExtra("documentId", documentId);
        intent.putExtra("address", address);
        intent.putExtra("propertyTYpe", property);
        intent.putExtra("email", emailAddress);
        intent.putExtra("phoneNumber1", phoneNumber1);
        intent.putExtra("phoneNumber2", phoneNumber2);
        intent.putExtra("areaRange", homeArea);
        intent.putExtra("areaUnit", homeUnit);
        intent.putExtra("category" , category);
        startActivity(intent);
    }


}