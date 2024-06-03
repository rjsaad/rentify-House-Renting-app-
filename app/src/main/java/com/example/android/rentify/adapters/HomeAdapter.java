package com.example.android.rentify.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rentify.R;
import com.example.android.rentify.activites.CommercialDesc;
import com.example.android.rentify.activites.DescriptionActivity;
import com.example.android.rentify.modelclasses.HomeModel;
import com.example.android.rentify.modelclasses.RealEstateAgentModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HOME = 1;
    private static final int TYPE_COMMERCIAL = 2;

    private Context context;
    private List<HomeModel> homeModelList;
    private List<HomeModel> filterHousesList;
    private boolean showFilterHouses = false;

    public HomeAdapter(List<HomeModel> homeModelList) {
        this.homeModelList = homeModelList;
    }

    public HomeAdapter(List<HomeModel> homeModelList, Context context) {
        this.context = context;
        this.homeModelList = homeModelList;
        this.filterHousesList = new ArrayList<>(homeModelList);
    }


    public HomeAdapter(Fragment context, List<HomeModel> homeModelList) {
        this.context = context.requireContext();
        this.homeModelList = homeModelList;
    }

    @Override
    public int getItemViewType(int position) {
        HomeModel homeModel = homeModelList.get(position);
        return homeModel.getDescription() != null ? TYPE_HOME : TYPE_COMMERCIAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HOME) {
            View homeView = inflater.inflate(R.layout.list_item, parent, false);
            return new HomeViewHolder(homeView);
        } else {
            View commercialView = inflater.inflate(R.layout.comm_list_view, parent, false);
            return new CommercialViewHolder(commercialView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HomeModel homeModel = showFilterHouses ? filterHousesList.get(position) : homeModelList.get(position);

        if (holder.getItemViewType() == TYPE_HOME) {
            HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
            // Bind views for home properties
            homeViewHolder.tvCity.setText(homeModel.getCity());
            homeViewHolder.tvAddress.setText(homeModel.getAddress());
            homeViewHolder.tvPrice.setText(homeModel.getPrice());
            homeViewHolder.tVRooms.setText(homeModel.getRooms());
            homeViewHolder.tvBathrooms.setText(homeModel.getBathrooms());
            homeViewHolder.tVAreaRange.setText(homeModel.getHomeAreaSize());
            homeViewHolder.tVAreaUnit.setText(homeModel.getHomeAreaUnit());
            homeViewHolder.tVPropertyType.setText(homeModel.getPropertyType());


            List<String> imageUris = homeModel.getImageUris();
            for (String uri : imageUris) {
                Picasso.get().load(uri).into(homeViewHolder.imageView);
            }

            // Handle click for home type
            homeViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Click", "Home ImageView clicked");

                    if (v.getContext() != null && homeModel != null) {
                        if (homeModel.getImageUris() != null) {
                            Intent intent = new Intent(v.getContext(), DescriptionActivity.class);
                            intent.putStringArrayListExtra("imageUris", (ArrayList<String>) homeModel.getImageUris());
                            intent.putExtra("cityTV", homeModel.getCity());
                            intent.putExtra("priceTV", homeModel.getPrice());
                            intent.putExtra("addressTV", homeModel.getAddress());
                            intent.putExtra("roomsTV", homeModel.getRooms());
                            intent.putExtra("bathroomsTV", homeModel.getBathrooms());
                            intent.putExtra("descriptionTV", homeModel.getDescription());
                            intent.putExtra("phNoTV", homeModel.getPhoneNumber1());
                            intent.putExtra("phNo2TV", homeModel.getPhoneNumber2());
                            intent.putExtra("emailTV", homeModel.getEmailAddress());
                            intent.putExtra("propertyType", homeModel.getPropertyType());
                            intent.putExtra("areaRange", homeModel.getHomeAreaSize());
                            intent.putExtra("areaUnit", homeModel.getHomeAreaUnit());
                            intent.putExtra("documentId", homeModel.getDocumentId());
                            v.getContext().startActivity(intent);
                        } else {
                            Log.e("Click", "ImageUris is null");
                        }
                    } else {
                        Log.e("Click", "Context or homeModel is null");
                    }
                }
            });


        } else {
            CommercialViewHolder commercialViewHolder = (CommercialViewHolder) holder;
            // Bind views for commercial properties
            commercialViewHolder.tvCity.setText(homeModel.getCity());
            commercialViewHolder.tvAddress.setText(homeModel.getAddress());
            commercialViewHolder.tvPrice.setText(homeModel.getPrice());
            commercialViewHolder.tvProType.setText(homeModel.getPropertyType());
            commercialViewHolder.tvAreaRange.setText(homeModel.getHomeAreaSize());
            commercialViewHolder.tvAreaUnit.setText(homeModel.getHomeAreaUnit());

            List<String> imageUris = homeModel.getImageUris();
            if (imageUris != null) {
                for (String uri : imageUris) {
                    Picasso.get().load(uri).into(commercialViewHolder.imageView);
                }
            }


            // Handle click for commercial type
            commercialViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Click", "Commercial ImageView clicked");

                    if (v.getContext() != null && homeModel != null) {
                        if (homeModel.getImageUris() != null) {
                            Intent intent = new Intent(v.getContext(), CommercialDesc.class);
                            intent.putStringArrayListExtra("imageUris", (ArrayList<String>) homeModel.getImageUris());
                            intent.putExtra("priceTV", homeModel.getPrice());
                            intent.putExtra("addressTV", homeModel.getAddress());
                            intent.putExtra("propertyType", homeModel.getPropertyType());
                            intent.putExtra("phNoTV", homeModel.getPhoneNumber1());
                            intent.putExtra("phNo2TV", homeModel.getPhoneNumber2());
                            intent.putExtra("emailTV", homeModel.getEmailAddress());
                            intent.putExtra("areaRange", homeModel.getHomeAreaSize());
                            intent.putExtra("areaUnit", homeModel.getHomeAreaUnit());
                            intent.putExtra("documentId", homeModel.getDocumentId());
                            v.getContext().startActivity(intent);
                        } else {
                            Log.e("Click", "ImageUris is null");
                        }
                    } else {
                        Log.e("Click", "Context or homeModel is null");
                    }
                }
            });

        }


    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvCity, tvAddress, tvPrice, tvBathrooms, tVRooms, tVAreaRange, tVAreaUnit, tVPropertyType;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.plcHldIV);
            tvCity = itemView.findViewById(R.id.cityTV);
            tvAddress = itemView.findViewById(R.id.addressTV);
            tvPrice = itemView.findViewById(R.id.priceTV);
            tVRooms = itemView.findViewById(R.id.roomsTV);
            tvBathrooms = itemView.findViewById(R.id.bathroomsTV);
            tVAreaRange = itemView.findViewById(R.id.areaRangeTV);
            tVAreaUnit = itemView.findViewById(R.id.areaUnitTV);
            tVPropertyType = itemView.findViewById(R.id.proListItem);
        }
    }

    public void filterHousesByLocation(String selectedCity) {
        filterHousesList.clear(); // Clear the filtered list
        for (HomeModel home : homeModelList) {
            String homeLocation = home.getCity();
            Log.d("Filter", "Home location: " + homeLocation + ", Selected city: " + selectedCity);
            if (homeLocation != null && homeLocation.trim().equalsIgnoreCase(selectedCity.trim())) {
                filterHousesList.add(home); // Add home to filtered list if location matches
                Log.d("Filter", "Added house with location: " + homeLocation);
            } else {
                Log.d("Filter", "Skipped house with location: " + homeLocation);
            }
        }
        showFilterHouses = true; // Set flag to indicate filtered mode
        notifyDataSetChanged(); // Notify adapter of data change

        // Log the size of filterHousesList for debugging purposes
        Log.d("Filter", "Filtered list size: " + filterHousesList.size());
    }

    public void filterHousesByRooms (String selectedRooms)
    {
        filterHousesList.clear(); // Clear the filtered list
        for (HomeModel home : homeModelList) {
            String homeLocation = home.getRooms();
            Log.d("Filter", "Home location: " + homeLocation + ", Selected rooms: " + selectedRooms);
            if (homeLocation != null && homeLocation.trim().equalsIgnoreCase(selectedRooms.trim())) {
                filterHousesList.add(home); // Add home to filtered list if location matches
                Log.d("Filter", "Added house with location: " + homeLocation);
            } else {
                Log.d("Filter", "Skipped house with location: " + homeLocation);
            }
        }
        showFilterHouses = true; // Set flag to indicate filtered mode
        notifyDataSetChanged(); // Notify adapter of data change

        // Log the size of filterHousesList for debugging purposes
        Log.d("Filter", "Filtered list size: " + filterHousesList.size());
    }

    public void filterHousesByBathrooms (String selectedBaths)
    {
        filterHousesList.clear(); // Clear the filtered list
        for (HomeModel home : homeModelList) {
            String homeLocation = home.getBathrooms();
            Log.d("Filter", "Home location: " + homeLocation + ", Selected bathrooms: " + selectedBaths);
            if (homeLocation != null && homeLocation.trim().equalsIgnoreCase(selectedBaths.trim())) {
                filterHousesList.add(home); // Add home to filtered list if location matches
                Log.d("Filter", "Added house with location: " + homeLocation);
            } else {
                Log.d("Filter", "Skipped house with location: " + homeLocation);
            }
        }
        showFilterHouses = true; // Set flag to indicate filtered mode
        notifyDataSetChanged(); // Notify adapter of data change

        // Log the size of filterHousesList for debugging purposes
        Log.d("Filter", "Filtered list size: " + filterHousesList.size());
    }
    public class CommercialViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvCity, tvAddress, tvPrice, tvProType, tvAreaRange, tvAreaUnit;

        public CommercialViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.commplcHldIV);
            tvCity = itemView.findViewById(R.id.commcityTV);
            tvAddress = itemView.findViewById(R.id.commaddressTV);
            tvPrice = itemView.findViewById(R.id.commpriceTV);
            tvProType = itemView.findViewById(R.id.proCommercialList);
            tvAreaRange = itemView.findViewById(R.id.commlistAreaRange);
            tvAreaUnit = itemView.findViewById(R.id.commlistAreaUnit);

        }
    }


    public void showAllAgents() {
        showFilterHouses = false; // Set flag to indicate all agents mode
        notifyDataSetChanged(); // Notify adapter of data change
    }

    @Override
    public int getItemCount() {
        return showFilterHouses ? filterHousesList.size() : homeModelList.size();
    }


}

