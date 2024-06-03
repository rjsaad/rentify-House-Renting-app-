package com.example.android.rentify.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.rentify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateUserProfile extends AppCompatActivity {

    private EditText userName, userCity;
    private String uID ;

    private Button updateBtn;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        setContentView(R.layout.activity_update_user_profile);
        getSupportActionBar().hide();
        initUI();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName.setText(extras.getString("userName"));
            userCity.setText(extras.getString("userCity"));

            uID = extras.getString("userId");
        }


        updateBtn = findViewById(R.id.updateUserProfile);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(userName, userCity);
            }
        });
    }

    private void update(EditText userName, EditText userCity) {

        String name = userName.getText().toString();
        String city = userCity.getText().toString();

        HashMap<String, Object> updatedHouse = new HashMap<>();
        updatedHouse.put("userName", name);
        updatedHouse.put("userCity", city);

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uID);
        userRef.updateChildren(updatedHouse).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {

                    userName.setText("");
                    userCity.setText("");

                    Toast.makeText(UpdateUserProfile.this, "Update successful", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(UpdateUserProfile.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void initUI() {

        userName = findViewById(R.id.editTextUpdateName);
        userCity = findViewById(R.id.editTextUpdateCity);

    }
}