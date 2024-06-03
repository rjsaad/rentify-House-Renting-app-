package com.example.android.rentify.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.rentify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText emailEt, passwordEt;
    Button login, createAccount;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        emailEt = findViewById(R.id.email_login);
        passwordEt = findViewById(R.id.password_email);

        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.createAcc);

        progressBar = findViewById(R.id.progressbar_login);


        mAuth = FirebaseAuth.getInstance();

        //if user is already login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to the main activity
            startActivity(new Intent(this, MainActivity.class));
            finish(); // finish the current activity to prevent the user from coming back using the back button
            return; // exit the method to prevent further execution
        }

        //login_button
        login.setOnClickListener(v -> login());

        //create_account_button
        createAccount.setOnClickListener(v -> createAccount());


    }

    // login functionality
    // login functionality
    public void login() {
        progressBar.setVisibility(View.VISIBLE);

        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Exit the method if email or password is empty
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);
                            emailEt.setText("");
                            passwordEt.setText("");

                            Toast.makeText(getApplicationContext(), "Invalid Email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //create account
    public void createAccount() {
        startActivity(new Intent(LoginActivity.this, com.example.android.rentify.activites.SingUpActivity.class));
    }
}