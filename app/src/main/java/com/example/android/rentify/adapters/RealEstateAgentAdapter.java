package com.example.android.rentify.adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.rentify.R;
import com.example.android.rentify.modelclasses.RealEstateAgentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RealEstateAgentAdapter extends RecyclerView.Adapter<RealEstateAgentAdapter.AgentViewHolder> {
    private List<RealEstateAgentModel> agentList;
    private List<RealEstateAgentModel> filteredAgentList;
    private Context context;
    private FirebaseUser currentUser;
    private boolean showFilteredAgents = false;


    public RealEstateAgentAdapter(List<RealEstateAgentModel> agentList, Context context) {
        this.agentList = agentList;
        this.filteredAgentList = new ArrayList<>(agentList);
        this.context = context;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current user
    }

    @NonNull
    @Override
    public AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.real_estate_agent_profile_list_view, parent, false);
        return new AgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentViewHolder holder, int position) {
        RealEstateAgentModel realEstateAgentModel;
        if (showFilteredAgents) {
            realEstateAgentModel = filteredAgentList.get(position);
        } else {
            realEstateAgentModel = agentList.get(position);
        }

        // Set the data to the ViewHolder
        holder.agencyNameTextView.setText(realEstateAgentModel.getAgencyName());
        holder.addressTextView.setText(realEstateAgentModel.getLocation());
        holder.contactTextView.setText(realEstateAgentModel.getContact());
        holder.nameTextView.setText(realEstateAgentModel.getName());
        holder.averageRating.setText(String.valueOf(realEstateAgentModel.getAverageRating()));
        Glide.with(context)
                .load(realEstateAgentModel.getImageUrl()) // Pass the image URL here
                .placeholder(R.mipmap.imagepicker) // Placeholder image while loading (optional)
                .error(R.mipmap.imagepicker) // Image to show in case of error (optional)
                .into(holder.imageViewProfile);


        // Set initial rating
        holder.ratingBar.setRating(realEstateAgentModel.getAverageRating());
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            holder.submitButton.setVisibility(View.VISIBLE);
        });
        // Set up RatingBar
        holder.submitButton.setOnClickListener(view -> {
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Check if the user has already rated this agent
                DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference()
                        .child("userRatings").child(realEstateAgentModel.getUserId()).child(userId);

                userRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            float rating = holder.ratingBar.getRating();

                            // Update the rating in Firebase
                            // Update the rating in Firebase
                            DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference()
                                    .child("realEstateAgents").child(realEstateAgentModel.getUserId());

                            float totalRating = realEstateAgentModel.getAverageRating() * realEstateAgentModel.getTotalRatings();
                            totalRating += rating;
                            realEstateAgentModel.setTotalRatings(realEstateAgentModel.getTotalRatings() + 1);
                            realEstateAgentModel.setAverageRating(totalRating / realEstateAgentModel.getTotalRatings());

                        // Update Firebase with new rating
                            agentRef.child("averageRating").setValue(realEstateAgentModel.getAverageRating());

                        // Update the RecyclerView item with the new data
                            agentList.set(holder.getAdapterPosition(), realEstateAgentModel);
                            notifyItemChanged(holder.getAdapterPosition());


                            // Store the user's rating for this agent to prevent duplicate ratings
                            userRatingRef.setValue(true);

                            // Update the RecyclerView item with the new data
                            notifyItemChanged(holder.getAdapterPosition());

                            // Disable the RatingBar and submit button after rating submission
                            holder.ratingBar.setIsIndicator(true);
                            holder.submitButton.setEnabled(false);

                            // Show a toast message confirming the rating submission
                            Toast.makeText(context, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // User has already rated this agent, show a message or take appropriate action
                            Toast.makeText(context, "You have already rated this agent.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            } else {
                // Handle the case where the user is not logged in
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public static class AgentViewHolder extends RecyclerView.ViewHolder {
        TextView agencyNameTextView, addressTextView, contactTextView, nameTextView , averageRating;
        RatingBar ratingBar;
        Button submitButton;
        CircleImageView imageViewProfile ;

        public AgentViewHolder(@NonNull View itemView) {
            super(itemView);
            agencyNameTextView = itemView.findViewById(R.id.agentAGENCY);
            addressTextView = itemView.findViewById(R.id.agentADDRESS);
            contactTextView = itemView.findViewById(R.id.agentCONTACT);
            nameTextView = itemView.findViewById(R.id.agentNAME);
            ratingBar = itemView.findViewById(R.id.agentRating);
            submitButton = itemView.findViewById(R.id.submitRating);
            averageRating = itemView.findViewById(R.id.agentAverageRating);
            imageViewProfile = itemView.findViewById(R.id.profileImage);
        }
    }
    // Method to filter agents based on location
    public void filterAgentsByLocation(String selectedCity) {
        filteredAgentList.clear(); // Clear the filtered list
        for (RealEstateAgentModel agent : agentList) {
            String agentLocation = agent.getLocation();
            if (agentLocation != null && agentLocation.trim().equalsIgnoreCase(selectedCity.trim())) {
                filteredAgentList.add(agent); // Add agent to filtered list if location matches
                Log.d("Filter", "Added agent with location: " + agentLocation);
            } else {
                Log.d("Filter", "Skipped agent with location: " + agentLocation);
            }
        }
        showFilteredAgents = true; // Set flag to indicate filtered mode
        notifyDataSetChanged(); // Notify adapter of data change

        // Log the size of filteredAgentList for debugging purposes
        Log.d("Filter", "Filtered list size: " + filteredAgentList.size());
    }
    public void filterAgentsByRating(float selectedRating) {
        filteredAgentList.clear(); // Clear the filtered list
        for (RealEstateAgentModel agent : agentList) {
            if (agent.getAverageRating() == selectedRating) {
                filteredAgentList.add(agent); // Add agent to filtered list if its rating is greater than or equal to the selected rating
                Log.d("Filter", "Added agent with rating: " + agent.getAverageRating());
            } else {
                Log.d("Filter", "Skipped agent with rating: " + agent.getAverageRating());
            }
        }
        showFilteredAgents = true; // Set flag to indicate filtered mode
        notifyDataSetChanged(); // Notify adapter of data change
    }






    public void showAllAgents() {
        showFilteredAgents = false; // Set flag to indicate all agents mode
        notifyDataSetChanged(); // Notify adapter of data change
    }
    @Override
    public int getItemCount() {
        if (showFilteredAgents) {
            return filteredAgentList.size(); // Return filtered list size if filtering is applied
        } else {
            return agentList.size(); // Return all agents if no filtering is applied
        }
    }


}

