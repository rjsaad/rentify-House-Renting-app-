package com.example.android.rentify.fragments;


import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.rentify.R;
import com.example.android.rentify.modelclasses.HomeModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 2;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private EditText etAddress, etPrice, etRooms, etBathrooms, etDescription, etPhoneNumber1, etPhoneNumber2, etEmailAddress, etAreaSize;
    private Spinner citySpinner;
    private Uri imageUri;
    private String selectedPropertyType;

    private Button btnPost, btnReset, apartmentBtn, firstFloorBtn, flatBtn, farmHouseBtn, pentHouseBtn, townHouseBtn, hostelBtn, mallsBtn, smallOfficesBtn, officesBtn, smallShopsBtn, shopsBtn;

    public TextView galleryTV;

    public ImageView imageView;

    private TextView homesLayoutBtn, commercialLayoutBtn;

    private LinearLayout homeLayout, commercialLayout, layout1, layout2, layout3, layout4;

    Spinner spinnerAreaUnits;

    private HomeModel homeModel ;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        initUI(view);


        homesLayoutBtn = view.findViewById(R.id.homeLayoutBtn);
        homesLayoutBtn.setOnClickListener(v -> onHomeLayoutButtonClick());
        commercialLayoutBtn = view.findViewById(R.id.commercialLayoutBtn);
        commercialLayoutBtn.setOnClickListener(v -> onCommercialLayoutButtonClick());

        homeLayout = view.findViewById(R.id.homeLayout);
        commercialLayout = view.findViewById(R.id.commercialLayout);
        layout1 = view.findViewById(R.id.layout1);
        layout2 = view.findViewById(R.id.layout2);
        layout3 = view.findViewById(R.id.layout3);
        layout4 = view.findViewById(R.id.layout4);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAllFields() && validateSpinners()) {

                    uploadImages();
                } else {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        galleryTV.setOnClickListener(v -> pickImages());

        btnReset.setOnClickListener(v -> clearFields());

        homesLayoutBtn.performClick();

        return view;
    }

    // Click handler for homeLayoutBtn
    public void onHomeLayoutButtonClick() {
        homeLayout.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);

        commercialLayout.setVisibility(View.GONE);

        commercialLayoutBtn.setTextColor(getResources().getColor(R.color.your_unselected_color));
        commercialLayoutBtn.setTypeface(null, Typeface.NORMAL);


        homesLayoutBtn.setTextColor(getResources().getColor(R.color.your_selected_color));
        homesLayoutBtn.setTypeface(null, Typeface.BOLD);
    }

    // Click handler for commercialLayoutBtn
    public void onCommercialLayoutButtonClick() {
        homeLayout.setVisibility(View.GONE);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.VISIBLE);
        layout4.setVisibility(View.VISIBLE);
        commercialLayout.setVisibility(View.VISIBLE);

        homesLayoutBtn.setTextColor(getResources().getColor(R.color.your_unselected_color));
        homesLayoutBtn.setTypeface(null, Typeface.NORMAL);


        commercialLayoutBtn.setTextColor(getResources().getColor(R.color.your_selected_color));
        commercialLayoutBtn.setTypeface(null, Typeface.BOLD);

    }


    private void updatePropertyTypeButtonColors() {
        // List of all property type buttons
        Button[] propertyTypeButtons = {apartmentBtn, firstFloorBtn, flatBtn, farmHouseBtn, pentHouseBtn, townHouseBtn,
                hostelBtn, mallsBtn, smallOfficesBtn, officesBtn, smallShopsBtn, shopsBtn};

        for (Button button : propertyTypeButtons) {
            if (button.getText().toString().equalsIgnoreCase(selectedPropertyType)) {
                // Change the text color for the selected property type button
                button.setTextColor(getResources().getColor(R.color.your_selected_color)); // Change to your color
                button.setTypeface(null, Typeface.BOLD);
            } else {
                // Change the text color for other property type buttons
                button.setTextColor(getResources().getColor(R.color.your_unselected_color)); // Change to your color
                button.setTypeface(null, Typeface.NORMAL);
            }
        }

        // Update text color for property type buttons in the non-active layout
        Button[] nonActiveButtons = (homeLayout.getVisibility() == View.VISIBLE) ?
                new Button[]{hostelBtn, mallsBtn, smallOfficesBtn, officesBtn, smallShopsBtn, shopsBtn} :
                new Button[]{apartmentBtn, firstFloorBtn, flatBtn, farmHouseBtn, pentHouseBtn, townHouseBtn};

        for (Button button : nonActiveButtons) {
            button.setTextColor(getResources().getColor(R.color.your_unselected_color)); // Change to your color
            button.setTypeface(null, Typeface.NORMAL);
        }
    }


    private void storeDataInDatabase(List<String> imageUrls) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : null;

        if (userId == null) {
            // Handle the case where the user is not authenticated
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String homeProperty = selectedPropertyType;

        DatabaseReference newChildRef = database.getReference();
        String documentId = newChildRef.push().getKey(); // Get the autogenerated ID

        if (documentId == null) {
            // Handle the case where the document ID is not generated
            Toast.makeText(getActivity(), "Failed to generate document ID", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check the property type and set the appropriate node reference
        if (isPropertyTypeInHomes(homeProperty)) {
            databaseReference = database.getReference("Homes").child(homeProperty);
        } else if (isPropertyTypeInCommercial(homeProperty)) {
            databaseReference = database.getReference("Commercial").child(homeProperty);
        } else {
            // Handle the case where the property type is not recognized
            Toast.makeText(getActivity(), "Invalid property type", Toast.LENGTH_SHORT).show();
            return;
        }






        String homeAreaSize = etAreaSize.getText().toString().trim();
        String homeAreaUnit = spinnerAreaUnits.getSelectedItem().toString().trim();

        String homeCity = citySpinner.getSelectedItem().toString().trim();
        String homeAddress = etAddress.getText().toString().trim();
        String homePrice = etPrice.getText().toString().trim();
        String homeEmailAddress = etEmailAddress.getText().toString().trim();
        String homePhoneNumber1 = etPhoneNumber1.getText().toString().trim();
        String homePhoneNumber2 = etPhoneNumber2.getText().toString().trim();


        // Additional fields specific to the homes layout
        String homeRooms = "";
        String homeBathrooms = "";
        String homeDescription = "";


        if (homeLayout.getVisibility() == View.VISIBLE) {
            homeRooms = etRooms.getText().toString().trim();
            homeBathrooms = etBathrooms.getText().toString().trim();
            homeDescription = etDescription.getText().toString().trim();

        }

        // Create the appropriate data model based on the layout type
        if (isPropertyTypeInHomes(homeProperty)) {
            homeModel = new HomeModel(documentId , homeAddress, homeBathrooms, homeCity, homeDescription,
                    homeEmailAddress, imageUrls, homePhoneNumber1, homePhoneNumber2, homePrice,
                    homeProperty, homeRooms, userId, homeAreaSize, homeAreaUnit);

             newChildRef = databaseReference.push();
           documentId = newChildRef.getKey(); // Get the autogenerated ID
            homeModel.setDocumentId(documentId);
            newChildRef.setValue(homeModel);
        } else if (isPropertyTypeInCommercial(homeProperty)) {
             homeModel = new HomeModel(documentId , homeAddress, homeCity, homePrice, imageUrls, homeProperty, userId, homeAreaSize,
                    homeAreaUnit, homeEmailAddress, homePhoneNumber1, homePhoneNumber2);


             newChildRef = databaseReference.push();
            documentId = newChildRef.getKey(); // Get the autogenerated ID
            homeModel.setDocumentId(documentId);
            newChildRef.setValue(homeModel);
        }
        // Set the document ID as a field in the data model
        clearFields();

    }

    private boolean isPropertyTypeInHomes(String propertyType) {
        // Convert the selected property type to lowercase before comparison
        String lowercasePropertyType = propertyType.toLowerCase();

        // Define the property types that belong to the 'Homes' node
        String[] homesPropertyTypes = {"apartment", "first floor", "flat", "farm house", "pent house", "town house"};

        for (String type : homesPropertyTypes) {
            if (type.equalsIgnoreCase(lowercasePropertyType)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPropertyTypeInCommercial(String propertyType) {
        // Convert the selected property type to lowercase before comparison
        String lowercasePropertyType = propertyType.toLowerCase();

        // Define the property types that belong to the 'Commercial' node
        String[] commercialPropertyTypes = {"hostel", "malls", "small offices", "offices", "small shops", "shops"};

        for (String type : commercialPropertyTypes) {
            if (type.equalsIgnoreCase(lowercasePropertyType)) {
                return true;
            }
        }

        return false;
    }

    private void setPropertyTypeButtonClick(Button button, final String propertyType) {

        button.setOnClickListener(v -> {
            selectedPropertyType = propertyType;
            handlePropertyTypeButtonClick(propertyType);

            // Update the text color for all property type buttons
            updatePropertyTypeButtonColors();
        });
    }

    private void handlePropertyTypeButtonClick(String propertyType) {
        // Handle the click event here
        Toast.makeText(getActivity(), "Property Type: " + propertyType, Toast.LENGTH_SHORT).show();


    }


    private void initUI(View view) {
        apartmentBtn = view.findViewById(R.id.apartment);
        firstFloorBtn = view.findViewById(R.id.firstFloor);
        flatBtn = view.findViewById(R.id.flat);
        farmHouseBtn = view.findViewById(R.id.farmhouse);
        pentHouseBtn = view.findViewById(R.id.penthouse);
        townHouseBtn = view.findViewById(R.id.townhouse);
        hostelBtn = view.findViewById(R.id.hostel);
        mallsBtn = view.findViewById(R.id.malls);
        smallOfficesBtn = view.findViewById(R.id.smalloffices);
        officesBtn = view.findViewById(R.id.offices);
        smallShopsBtn = view.findViewById(R.id.smallShops);
        shopsBtn = view.findViewById(R.id.shops);


        // Set click listeners for property type buttons
        setPropertyTypeButtonClick(apartmentBtn, "Apartment");
        setPropertyTypeButtonClick(firstFloorBtn, "First Floor");
        setPropertyTypeButtonClick(flatBtn, "Flat");
        setPropertyTypeButtonClick(farmHouseBtn, "Farm House");
        setPropertyTypeButtonClick(pentHouseBtn, "Pent House");
        setPropertyTypeButtonClick(townHouseBtn, "Town House");
        setPropertyTypeButtonClick(hostelBtn, "Hostel");
        setPropertyTypeButtonClick(mallsBtn, "Malls");
        setPropertyTypeButtonClick(smallOfficesBtn, "Small Offices");
        setPropertyTypeButtonClick(officesBtn, "Offices");
        setPropertyTypeButtonClick(smallShopsBtn, "Small Shops");
        setPropertyTypeButtonClick(shopsBtn, "Shops");


        imageView = view.findViewById(R.id.imageGallery);
        galleryTV = view.findViewById(R.id.selectImgeTV);
        citySpinner = view.findViewById(R.id.citySpinner);
        etAddress = view.findViewById(R.id.enter_address);
        etPrice = view.findViewById(R.id.enter_price);
        btnPost = view.findViewById(R.id.btn_post);
        etRooms = view.findViewById(R.id.enter_rooms);
        etDescription = view.findViewById(R.id.enter_discription);
        etPhoneNumber1 = view.findViewById(R.id.enter_phoneNumber);
        etPhoneNumber2 = view.findViewById(R.id.enter_phoneNumber2);
        etEmailAddress = view.findViewById(R.id.enter_emailAddress);
        etBathrooms = view.findViewById(R.id.enter_bathrooms);
        etAreaSize = view.findViewById(R.id.enter_range);
        spinnerAreaUnits = view.findViewById(R.id.rangeSpinner);
        btnReset = view.findViewById(R.id.resetAll);
    }

    private void clearFields() {
        citySpinner.setSelection(0);
        etAddress.getText().clear();
        etPrice.getText().clear();
        etRooms.getText().clear();
        etBathrooms.getText().clear();
        etDescription.getText().clear();
        etPhoneNumber1.getText().clear();
        etPhoneNumber2.getText().clear();
        etEmailAddress.getText().clear();
        etAreaSize.getText().clear();
        spinnerAreaUnits.setSelection(0);
        imageView.setImageResource(0);
        imageUri = null;
    }

    private void pickImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }


    private void uploadImages() {
        if (validateSpinners()) {
            if (!imageUris.isEmpty()) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("File Uploader");
                progressDialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                List<String> imageUrls = new ArrayList<>(); // Store image URLs here
                boolean[] errorOccurred = {false}; // Flag to track if an error occurs during upload

                for (int i = 0; i < imageUris.size(); i++) {
                    Uri imageUri = imageUris.get(i);
                    StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + "_" + i);

                    final int finalI = i;  // Making it effectively final for lambda expression
                    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (uri != null) {
                                String imageUrl = uri.toString();
                                imageUrls.add(imageUrl); // Add image URL to the list

                                if (finalI == imageUris.size() - 1) {
                                    // Dismiss the progress dialog when the last image is uploaded
                                    progressDialog.dismiss();
                                    if (!errorOccurred[0]) {
                                        // Only store data in the database if no error occurred during image upload
                                        storeDataInDatabase(imageUrls); // Pass the list of image URLs
                                        Toast.makeText(getActivity().getApplicationContext(), "Images Uploaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "Error occurred during image upload", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // Handle the case where the URI is null
                                progressDialog.dismiss();
                                errorOccurred[0] = true; // Set the error flag
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnProgressListener(snapshot -> {
                        float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: " + (int) percent + "%");
                    }).addOnFailureListener(exception -> {
                        // Handle the case where an error occurs during image upload
                        progressDialog.dismiss();
                        errorOccurred[0] = true; // Set the error flag
                        Toast.makeText(getActivity().getApplicationContext(), "Error occurred during image upload", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(getActivity(), "No images selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please select a city and property type", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the list of selected image URIs from the Intent
            List<Uri> selectedImageUris = new ArrayList<>();

            if (data.getClipData() != null) {
                // User selected multiple images
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                // User selected a single image
                Uri imageUri = data.getData();
                selectedImageUris.add(imageUri);
            }

            // Do something with the selected image URIs
            handleSelectedImages(selectedImageUris);
        }
    }

    private void handleSelectedImages(List<Uri> selectedImageUris) {
        if (selectedImageUris != null && !selectedImageUris.isEmpty()) {
            imageUris.clear();
            imageUris.addAll(selectedImageUris);

            // Display the first selected image in the ImageView (you can customize this part)
            imageView.setImageURI(imageUris.get(0));
        }
    }


    private boolean validateAllFields() {
        boolean isValid = true;

        isValid = isValid && validateField(etAddress, "Address is required");
        isValid = isValid && validateField(etPrice, "Price is required");
        isValid = isValid && validateField(etEmailAddress, "Email Address is required");
        isValid = isValid && validateField(etPhoneNumber1, "Phone Number is required");
        isValid = isValid && validateField(etPhoneNumber2, "Phone Number is required");
        isValid = isValid && validateSpinners();
        if (homeLayout.getVisibility() == View.VISIBLE) {
            isValid = isValid && validateField(etRooms, "Rooms is required");
            isValid = isValid && validateField(etBathrooms, "Bathrooms is required");
            isValid = isValid && validateField(etDescription, "Description is required");
            isValid = isValid && validatePhoneNumber(etPhoneNumber1);
            isValid = isValid && validatePhoneNumber(etPhoneNumber2);
        }

        return isValid;
    }

    private boolean validateField(EditText editText, String errorMessage) {
        String value = editText.getText().toString().trim();

        if (TextUtils.isEmpty(value)) {
            editText.setError(errorMessage);
            editText.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSpinners() {
        boolean isValid = true;

        if (citySpinner.getSelectedItemPosition() == 0) {
            // No city selected
            Toast.makeText(getActivity(), "Please select a city", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePhoneNumber(EditText editText) {
        String phoneNumber = editText.getText().toString().trim();

        Log.d("PhoneNumberLength", "Length: " + phoneNumber.length()); // Add this line for debugging

        if (phoneNumber.length() != 11) {
            editText.setError("Phone Number must be exactly 11 digits");
            editText.requestFocus();
            return false;
        }

        return true;
    }


}






