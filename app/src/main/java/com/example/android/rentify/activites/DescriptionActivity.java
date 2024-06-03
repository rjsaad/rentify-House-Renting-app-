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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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


import java.util.List;

public class DescriptionActivity extends AppCompatActivity {

    TextView cityTV, desPrice, desAddress, desRooms, desBathrooms, descripition, desPhNo1, desPhNo2, desEmail, desProperty,
            descAreaSize, desAreaUnit;
    Button composeEmail, dialPhone, whatsappBtn;
    ToggleButton addFavrtHouse;
    private String price, address, rooms, bathRooms, phoneNumber1, phoneNumber2, descriptionTextView, emailAddress, city,
            property, homeArea, homeUnit, documentId;

    String category;

    private ViewPager2 viewPager;
    private PageIndicatorView pageIndicatorView;
    private ImageSliderAdapter imageSliderAdapter;
    private List<String> imageUris;

    private boolean isFavorite = false;
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
        setContentView(R.layout.activity_description);
        getSupportActionBar().hide();
        initializeViews();
        setupViewPager();
        setupIntentData();
        setupListeners();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Set the initial color based on the stored value
        int storedColor = preferences.getInt(TOGGLE_BUTTON_COLOR_KEY, R.color.your_unselected_color);
        addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(this, storedColor));
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

    private void setupIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            imageUris = intent.getStringArrayListExtra("imageUris");
            city = intent.getStringExtra("cityTV");
            price = intent.getStringExtra("priceTV");
            address = intent.getStringExtra("addressTV");
            rooms = intent.getStringExtra("roomsTV");
            bathRooms = intent.getStringExtra("bathroomsTV");
            descriptionTextView = intent.getStringExtra("descriptionTV");
            phoneNumber1 = intent.getStringExtra("phNoTV");
            phoneNumber2 = intent.getStringExtra("phNo2TV");
            emailAddress = intent.getStringExtra("emailTV");
            property = intent.getStringExtra("propertyType");
            homeArea = intent.getStringExtra("areaRange");
            homeUnit = intent.getStringExtra("areaUnit");
            documentId = intent.getStringExtra("documentId");

            desPrice.setText(price);
            desAddress.setText(address);
            desRooms.setText(rooms);
            desBathrooms.setText(bathRooms);
            descripition.setText(descriptionTextView);
            desPhNo1.setText(phoneNumber1);
            desPhNo2.setText(phoneNumber2);
            desEmail.setText(emailAddress);
            desProperty.setText(property);
            descAreaSize.setText(homeArea);
            desAreaUnit.setText(homeUnit);

        }
    }

    private void setupListeners() {
        //email intent
        composeEmail = findViewById(R.id.composeEmail);
        composeEmail.setOnClickListener(view -> openApp(Intent.ACTION_SENDTO, "mailto:"));

        //dial phone intent
        dialPhone = findViewById(R.id.dialPhone);
        dialPhone.setOnClickListener(view -> openApp(Intent.ACTION_DIAL, "tel:"));

        //whatsapp intent
        whatsappBtn = findViewById(R.id.whatsappBtn);
        whatsappBtn.setOnClickListener(view -> {
            // Call the openApp method with the appropriate action and data
            openApp(Intent.ACTION_VIEW, "https://api.whatsapp.com/send?phone=PHONE_NUMBER");
        });

        //add favourite house to firebase
        addFavrtHouse = findViewById(R.id.addfavouriteHouse);
        //add favourite house to firebase
        addFavrtHouse = findViewById(R.id.addfavouriteHouse);
        // ...

        addFavrtHouse = findViewById(R.id.addfavouriteHouse);

        final boolean[] isFavorite = {false}; // Using an array to make it effectively final

        addFavrtHouse.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference favoritesRef = database.getReference("Favourites");

                // Check if the house is already a favorite
                favoritesRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String houseKey = null;

                        // Check if the house is already a favorite
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HomeModel house = snapshot.getValue(HomeModel.class);
                            if (house != null && house.getCity() != null && house.getCity().equals(city) && house.getUserId().equals(userId)) {
                                // The house is already a favorite, remove it
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DescriptionActivity.this, "House removed from favorites", Toast.LENGTH_SHORT).show();

                                            // Toggle the state of the favorite button
                                            isFavorite[0] = !isFavorite[0];

                                            // Change the backgroundTintList based on the state
                                            int colorResId = isFavorite[0] ? R.color.your_selected_color : R.color.your_unselected_color;
                                            addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(DescriptionActivity.this, colorResId));

                                            // Save the selected color in SharedPreferences
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putInt(TOGGLE_BUTTON_COLOR_KEY, colorResId);
                                            editor.apply();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the failure to remove the house
                                            Toast.makeText(DescriptionActivity.this, "Error removing house from favorites", Toast.LENGTH_SHORT).show();
                                        });
                                isFavorite[0] = true;
                                houseKey = snapshot.getKey();
                                break;
                            }
                        }

                        if (!isFavorite[0]) {
                            // The property is not a favorite, add it
                            String newFavoriteKey = favoritesRef.push().getKey();

                            HomeModel newFavorite;

                            // Check the property type and use the appropriate constructor
                            if (property.equals("Apartment") || property.equals("First Floor") || property.equals("Flat") || property.equals("Farm House")
                                    || property.equals("Pent House") || property.equals("Town House")) {
                                // Use the constructor for homes
                                newFavorite = new HomeModel(documentId, address, bathRooms, city, descriptionTextView, emailAddress, imageUris,
                                        phoneNumber1, phoneNumber2, price, property, rooms, userId, homeArea, homeUnit);
                            } else {
                                // Use the constructor for commercial properties
                                newFavorite = new HomeModel(documentId, address, city, price, imageUris, property, userId, homeArea, homeUnit,
                                        emailAddress, phoneNumber1, phoneNumber2);
                            }

                            favoritesRef.child(newFavoriteKey).setValue(newFavorite)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(DescriptionActivity.this, "Property added to favorites", Toast.LENGTH_SHORT).show();

                                        // Toggle the state of the favorite button
                                        isFavorite[0] = !isFavorite[0];

                                        // Change the backgroundTintList based on the state
                                        int colorResId = isFavorite[0] ? R.color.your_selected_color : R.color.your_unselected_color;
                                        addFavrtHouse.setBackgroundTintList(ContextCompat.getColorStateList(DescriptionActivity.this, colorResId));

                                        // Save the selected color in SharedPreferences
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt(TOGGLE_BUTTON_COLOR_KEY, colorResId);
                                        editor.apply();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure to add the property to favorites
                                        Toast.makeText(DescriptionActivity.this, "Error adding property to favorites", Toast.LENGTH_SHORT).show();
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                        Toast.makeText(DescriptionActivity.this, "Error checking favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // User not authenticated, handle accordingly (e.g., redirect to login)
                Toast.makeText(DescriptionActivity.this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            }
        });

// ...


    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPagerdes);
        pageIndicatorView = findViewById(R.id.pageindicatorviewdes);
        cityTV = findViewById(R.id.cityTV);
        desPrice = findViewById(R.id.descriptinPrice);
        desAddress = findViewById(R.id.descriptinAddress);
        desRooms = findViewById(R.id.descriptinRooms);
        desBathrooms = findViewById(R.id.descriptinBathrooms);
        descripition = findViewById(R.id.descriptionTextView);
        desPhNo1 = findViewById(R.id.descriptinPhNO);
        desPhNo2 = findViewById(R.id.descriptinPhNO2);
        desEmail = findViewById(R.id.descriptinEmail);
        desProperty = findViewById(R.id.descriptinPropertyType);
        descAreaSize = findViewById(R.id.desAreaRange);
        desAreaUnit = findViewById(R.id.desAreaUnit);
    }

    private void setupViewPager() {
        Intent intent = getIntent();
        if (intent != null) {
            imageUris = intent.getStringArrayListExtra("imageUris");

            if (imageUris != null && !imageUris.isEmpty()) {
                pageIndicatorView.setCount(imageUris.size());
            }

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    pageIndicatorView.setSelection(position);
                }
            });
        }
        // Set up the ViewPager2
        imageSliderAdapter = new ImageSliderAdapter(this, imageUris);
        viewPager.setAdapter(imageSliderAdapter);
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
                        currentUserIsOwner = true;

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
                    Toast.makeText(DescriptionActivity.this, "House deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can finish the activity or navigate to another screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case (error deleting house)
                    Toast.makeText(DescriptionActivity.this, "Error deleting house from Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    private void update() {

        category = "Homes";

        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("price", price);
        intent.putExtra("rooms", rooms);
        intent.putExtra("bathrooms", bathRooms);
        intent.putExtra("description", descriptionTextView);
        intent.putExtra("documentId", documentId);
        intent.putExtra("address", address);
        intent.putExtra("propertyTYpe", property);
        intent.putExtra("email", emailAddress);
        intent.putExtra("phoneNumber1", phoneNumber1);
        intent.putExtra("phoneNumber2", phoneNumber2);
        intent.putExtra("areaRange", homeArea);
        intent.putExtra("areaUnit", homeUnit);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
