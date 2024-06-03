package com.example.android.rentify.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android.rentify.R;
import com.example.android.rentify.modelclasses.RealEstateAgentModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RealEstateAgentActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri imageUri;
    private EditText nameEt, contactEt, locationEt, cnicEt, agencyNameEt;
    private Button buttonSubmit;
    private TextView imageUpload;
    private CircleImageView imageViewProfile;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_real_estate_agent);
        getSupportActionBar().hide();

        initUI();

        imageUpload.setOnClickListener(v -> pickImageFromGallery());

        buttonSubmit.setOnClickListener(v -> addAgentToFirebase());
    }

    private void initUI() {
        nameEt = findViewById(R.id.editTextName);
        contactEt = findViewById(R.id.editTextContact);
        locationEt = findViewById(R.id.editTextLocation);
        cnicEt = findViewById(R.id.editTextCNIC);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        agencyNameEt = findViewById(R.id.realEstateAgency);
        imageUpload = findViewById(R.id.imageUploadTxtView);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            // Load the picked image into the circular image view using Glide
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.mipmap.imagepicker) // Placeholder image while loading (optional)
                    .error(R.mipmap.imagepicker) // Image to show in case of error (optional)
                    .into(imageViewProfile);

            imageUpload.setText("Image selected"); // Update text to indicate image selection
        }
    }

    private void addAgentToFirebase() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String name = nameEt.getText().toString();
        String contact = contactEt.getText().toString();
        String location = locationEt.getText().toString();
        String cnic = cnicEt.getText().toString();
        String agencyName = agencyNameEt.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(location) || TextUtils.isEmpty(cnic) || TextUtils.isEmpty(agencyName) || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already has a profile
        databaseReference.child("realEstateAgents").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // User already has a profile
                        Toast.makeText(RealEstateAgentActivity.this, "You already have a real estate agent profile", Toast.LENGTH_SHORT).show();
                    } else {
                        // User does not have a profile, proceed to add
                        addAgentDetails(userId, name, contact, location, cnic, agencyName);
                    }
                } else {
                    // Error occurred while checking profile existence
                    Toast.makeText(RealEstateAgentActivity.this, "Failed to check profile existence", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addAgentDetails(String userId, String name, String contact, String location, String cnic, String agencyName) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference imageRef = storageReference.child("realestate_agent_images").child(userId);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                // Calculate upload progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        });

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                RealEstateAgentModel agentModel = new RealEstateAgentModel(userId, name, contact, location, cnic, agencyName, imageUrl);

                databaseReference.child("realEstateAgents").child(userId).setValue(agentModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(RealEstateAgentActivity.this, "Agent added successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Finish the activity after adding the agent
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(RealEstateAgentActivity.this, "Failed to add agent", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RealEstateAgentActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
