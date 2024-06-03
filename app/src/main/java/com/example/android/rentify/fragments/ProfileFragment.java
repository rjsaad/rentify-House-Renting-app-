package com.example.android.rentify.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.rentify.R;
import com.example.android.rentify.activites.LoginActivity;
import com.example.android.rentify.activites.MyHomesActivity;
import com.example.android.rentify.activites.RealEstateAgentActivity;
import com.example.android.rentify.activites.ThemesActivity;
import com.example.android.rentify.activites.UpdateUserProfile;
import com.example.android.rentify.databinding.FragmentProfileBinding;
import com.example.android.rentify.modelclasses.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Locale;


public class ProfileFragment extends Fragment {

    TextView nameProfileTV, cityProfileTV;
    TextView logoutBtn, myHomes, realEstBtn, themesBtn, languageBtn, profileSetting , favTextView , contactUs;


    Button postBtn ;

    String uid;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        loadLocale();

        nameProfileTV = view.findViewById(R.id.profileName);
        cityProfileTV = view.findViewById(R.id.profileCity);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userReference = databaseReference.child("users").child(uid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user's data
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String name = userModel.getUserName();
                        String city = userModel.getUserCity();

                        // Update UI with user's name and city
                        nameProfileTV.setText(name);
                        cityProfileTV.setText(city);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Check if the fragment is still attached to an activity
                if (isAdded()) {
                    // Handle error
                    Toast.makeText(getActivity(), "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Logout profile
        logoutBtn = view.findViewById(R.id.profileLogoutBtn);
        logoutBtn.setOnClickListener(view1 -> {
            LogOut();
        });

        myHomes = view.findViewById(R.id.myHomesBtn);
        myHomes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyHomes();
            }
        });
        realEstBtn = view.findViewById(R.id.realEstBtn);
        realEstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RealEstateAgentActivity.class);
                startActivity(intent);
            }
        });

        themesBtn = view.findViewById(R.id.android_themes);
        themesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateThemes();
            }
        });

        languageBtn = view.findViewById(R.id.languageBtn);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();
            }
        });

        profileSetting = view.findViewById(R.id.profileSetting);
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        postBtn = view.findViewById(R.id.postPropertyProfileFrag);
        postBtn.setOnClickListener(v -> navigateToFragment(new AddFragment()));

        favTextView = view.findViewById(R.id.profileFagFavTxtView);
        favTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FavouriteFragment());
            }
        });

        contactUs = view.findViewById(R.id.contactUsPrfileFrag);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] recipients = {"rjsaad861@gmail.com"};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                intent.putExtra(Intent.EXTRA_TEXT, "Body");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });
        return view;
    }

    private void updateUserProfile() {

        String userName = nameProfileTV.getText().toString();
        String userCity = cityProfileTV.getText().toString();

        Intent intent = new Intent(getContext(), UpdateUserProfile.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userCity", userCity);
        intent.putExtra("userId", uid);
        startActivity(intent);

    }

    private void updateThemes() {
        final String themes[] = {"Default", "Dark"}; // Add your theme names here

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Theme");
        builder.setItems(themes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Apply the selected theme
                switch (which) {
//                    case 0:
//                        // Default theme
//                        getActivity().setTheme(R.style.Theme_Rentify); // Set your default theme here
//                        refreshFragment();
//                        break;
//                    case 1:
//                        // Dark theme
//                        getActivity().setTheme(R.style.Theme_Rentify_Dark); // Set your dark theme here
//                        refreshFragment();
//                        break;
                }
            }
        });

        builder.show();
    }





    private void changeLanguage() {
        final String languages[] = {"English", "Urdu"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Select language");
        mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("");
                    refreshFragment();
                } else if (which == 1) {
                    setLocale("ur");
                    refreshFragment();
                }
            }
        });
        mBuilder.create();
        mBuilder.show();
    }

    private void setLocale(String languages) {
        if (getContext() == null) {
            return; // Return if the context is null
        }
        Locale locale = new Locale(languages);
        Locale.setDefault(locale);


        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getContext().getResources().updateConfiguration(configuration, getContext().
                getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("app_lang", languages);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
        String languages = sharedPreferences.getString("app_lang", "");
        setLocale(languages);
    }

    private void refreshFragment() {

        ProfileFragment refreshedFragment = new ProfileFragment();


        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.linearlayout_frag, refreshedFragment)
                    .commit();
        }
    }

    private void LogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button, proceed with logout
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, dismiss the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }


    private void showMyHomes() {
        Intent intent = new Intent(getActivity(), MyHomesActivity.class);
        startActivity(intent);
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_frag, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
