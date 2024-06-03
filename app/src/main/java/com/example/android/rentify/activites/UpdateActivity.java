package com.example.android.rentify.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.rentify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    EditText updatedPrice, updatedAddress, updatedEmail, updatedPhNo1, updatedPhNo2, updatedHomeArea, updatedDescription,
            updatedRooms, updatedBathRooms;
    Spinner updatedHomeUnit;
    Button updateButton;

    String propertyType, propertyId, category;
    List<String> updatedImageUris;

    DatabaseReference propertyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        intiUI();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            updatedPrice.setText(extras.getString("price"));
            updatedAddress.setText(extras.getString("address"));
            updatedRooms.setText(extras.getString("rooms"));
            updatedBathRooms.setText(extras.getString("bathrooms"));
            updatedDescription.setText(extras.getString("description"));
            updatedEmail.setText(extras.getString("email"));
            updatedPhNo1.setText(extras.getString("phoneNumber1"));
            updatedPhNo2.setText(extras.getString("phoneNumber2"));
            updatedHomeArea.setText(extras.getString("areaRange"));

            category = extras.getString("category");

            propertyId = extras.getString("documentId");
            // Retrieve property type from intent and store it in a variable
            propertyType = extras.getString("propertyTYpe");

            Log.d("UpdateActivity", "Property ID: " + propertyId);
            // Set selected item in spinner
            String selectedHomeUnit = extras.getString("areaUnit");
            if (selectedHomeUnit != null) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.area_units, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                updatedHomeUnit.setAdapter(adapter);
                int position = adapter.getPosition(selectedHomeUnit);
                updatedHomeUnit.setSelection(position);
            }
        }
        if ("Commercial".equals(category)) {
            updatedRooms.setVisibility(View.GONE);
            updatedBathRooms.setVisibility(View.GONE);
            updatedDescription.setVisibility(View.GONE);
        }
        // Set onClickListener for updateButton
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(updatedPrice, updatedAddress, updatedRooms, updatedBathRooms, updatedDescription, updatedEmail, updatedPhNo1, updatedPhNo2, updatedHomeArea, updatedHomeUnit);
            }
        });

        // Check if the category is "Commercial" and hide corresponding fields

    }

    private void updateData(EditText updatedPrice, EditText updatedAddress, EditText updatedRooms, EditText updatedBathRooms, EditText updatedDescription, EditText updatedEmail, EditText updatedPhNo1,
                            EditText updatedPhNo2, EditText updatedHomeArea, Spinner updatedHomeUnit) {

        String address = updatedAddress.getText().toString();
        String price = updatedPrice.getText().toString();
        String email = updatedEmail.getText().toString();
        String phoneNumber1 = updatedPhNo1.getText().toString();
        String phoneNumber2 = updatedPhNo2.getText().toString();
        String homeAreaSize = updatedHomeArea.getText().toString();
        String homeAreaUnit = updatedHomeUnit.getSelectedItem().toString();

        HashMap<String, Object> updatedHouse = new HashMap<>();
        updatedHouse.put("address", address);
        updatedHouse.put("price", price);
        updatedHouse.put("email", email);
        updatedHouse.put("phoneNumber1", phoneNumber1);
        updatedHouse.put("phoneNumber2", phoneNumber2);
        updatedHouse.put("homeAreaSize", homeAreaSize);
        updatedHouse.put("homeAreaUnit", homeAreaUnit);
        // Check if the category is not "Commercial" before including these fields
        if (!"Commercial".equals(category)) {
            String rooms = updatedRooms.getText().toString();
            String bathrooms = updatedBathRooms.getText().toString();
            String description = updatedDescription.getText().toString();

            updatedHouse.put("rooms", rooms);
            updatedHouse.put("bathrooms", bathrooms);
            updatedHouse.put("description", description);
        }


        propertyRef = FirebaseDatabase.getInstance().getReference(category).child(propertyType);
        propertyRef.child(propertyId).updateChildren(updatedHouse).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    updatedAddress.setText("");
                    updatedPrice.setText("");
                    updatedEmail.setText("");
                    updatedPhNo1.setText("");
                    updatedPhNo2.setText("");
                    updatedHomeArea.setText("");
                    if (!"Commercial".equals(category)) {
                        updatedRooms.setText("");
                        updatedBathRooms.setText("");
                        updatedDescription.setText("");
                    }
                    updatedHomeUnit.setSelection(0);
                    Toast.makeText(UpdateActivity.this, "Update successful", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(UpdateActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void intiUI() {
        updatedPrice = findViewById(R.id.editTextUpdatedPrice);
        updatedAddress = findViewById(R.id.editTextUpdatedAddress);
        updatedEmail = findViewById(R.id.editTextUpdatedEmail);
        updatedPhNo1 = findViewById(R.id.editTextUpdatedPhNo1);
        updatedPhNo2 = findViewById(R.id.editTextUpdatedPhNo2);
        updateButton = findViewById(R.id.buttonUpdate);
        updatedHomeArea = findViewById(R.id.editTextUpdatedHomeArea);
        updatedDescription = findViewById(R.id.editTextUpdateddDescription);
        updatedHomeUnit = findViewById(R.id.editTextUpdatedHomeUnit);
        updatedRooms = findViewById(R.id.editTextUpdatedRooms);
        updatedBathRooms = findViewById(R.id.editTextUpdatedBathRooms);
    }


}






