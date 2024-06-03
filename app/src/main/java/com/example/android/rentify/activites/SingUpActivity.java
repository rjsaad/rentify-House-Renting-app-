package com.example.android.rentify.activites;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.rentify.R;

import com.example.android.rentify.modelclasses.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {

    TextInputEditText emailEt, passwordEt, recheckPasswordEt, userNameEt, userCityEt;
    Button button;
    ProgressBar bar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        getSupportActionBar().hide();

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        recheckPasswordEt = findViewById(R.id.recheck_password);
        button = findViewById(R.id.singup);
        bar = findViewById(R.id.progressbar);
        userNameEt = findViewById(R.id.email_name);
        userCityEt = findViewById(R.id.email_city);

        button.setOnClickListener(v -> signUp());
    }

    public void signUp() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String recheckPassword = recheckPasswordEt.getText().toString().trim();
        String name = userNameEt.getText().toString().trim();
        String city = userCityEt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || recheckPassword.isEmpty() || name.isEmpty() || city.isEmpty()) {
            // Show a Toast message if any field is empty
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        if (!password.equals(recheckPassword)) {
            // Show a Toast message if passwords do not match
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        bar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    bar.setVisibility(View.INVISIBLE);

                    // Save user data to the database
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userReference = databaseReference.child("users").child(uid);

                    UserModel userModel = new UserModel(name, city);
                    userReference.setValue(userModel);

                    Toast.makeText(getApplicationContext(), "Successfully signed up", Toast.LENGTH_SHORT).show();

                    // Navigate back to the login screen
                    Log.d("SignUpActivity", "Navigating to LoginActivity");
                    finish(); // Finish the current activity to prevent the user from coming back using the back button

                } else {
                    // If sign up fails, display a message to the user.
                    bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
