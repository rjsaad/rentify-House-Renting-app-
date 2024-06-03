package com.example.android.rentify.modelclasses;

import android.net.Uri;

public class RealEstateAgentModel {
    private String userId;
    private String name;
    private String contact;
    private String location;
    private String cnicNo;
    private String agencyName;
    private float averageRating;
    private int totalRatings;
    private String imageUrl;

    public RealEstateAgentModel() {
    }

    public RealEstateAgentModel(String userId, String name, String contact, String location, String cnicNo, String agencyName, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.cnicNo = cnicNo;
        this.agencyName = agencyName;
        this.imageUrl = imageUrl;
        this.averageRating = 0.0f; // Default rating
        this.totalRatings = 0; // Initial total ratings
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCnicNo() {
        return cnicNo;
    }

    public void setCnicNo(String cnicNo) {
        this.cnicNo = cnicNo;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
