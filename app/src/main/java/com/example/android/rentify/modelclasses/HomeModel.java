package com.example.android.rentify.modelclasses;

import java.util.List;

public class HomeModel {
    private String city, address, price, rooms, bathrooms, description, phoneNumber1, phoneNumber2, emailAddress, documentId,
            userId, propertyType, homeAreaSize, homeAreaUnit;
    private List<String> imageUris;

    public HomeModel() {
        // Default constructor required for Firebase
    }



    public HomeModel( String documentId , String address, String bathrooms, String city, String description, String emailAddress, List<String> imageUris,
                     String phoneNumber1, String phoneNumber2, String price, String propertyType, String rooms, String userId
            , String homeAreaSize, String homeAreaUnit) {
        this.city = city;
        this.address = address;
        this.price = price;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.description = description;
        this.phoneNumber1 = phoneNumber1;
        this.phoneNumber2 = phoneNumber2;
        this.emailAddress = emailAddress;
        this.imageUris = imageUris;
        this.propertyType = propertyType;
        this.userId = userId;
        this.homeAreaSize = homeAreaSize;
        this.homeAreaUnit = homeAreaUnit;
        this.documentId = documentId;
    }

    public HomeModel( String documentId , String address, String city, String price, List<String> imageUris, String propertyType,
                     String userId, String homeAreaSize, String homeAreaUnit,
                     String emailAddress, String phoneNumber1, String phoneNumber2 ) {
        this.address = address;
        this.city = city;
        this.price = price;
        this.imageUris = imageUris;
        this.propertyType = propertyType;
        this.userId = userId;
        this.homeAreaUnit = homeAreaUnit;
        this.homeAreaSize = homeAreaSize;
        this.emailAddress = emailAddress;
        this.phoneNumber1 = phoneNumber1;
        this.phoneNumber2 = phoneNumber2;
        this.documentId = documentId ;
    }

    public String getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(String bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getHomeAreaSize() {
        return homeAreaSize;
    }

    public void setHomeAreaSize(String homeAreaSize) {
        this.homeAreaSize = homeAreaSize;
    }

    public String getHomeAreaUnit() {
        return homeAreaUnit;
    }

    public void setHomeAreaUnit(String homeAreaUnit) {
        this.homeAreaUnit = homeAreaUnit;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
