package com.example.android.rentify.modelclasses;

public class UserModel {
    String userName , userCity ;

    public UserModel() {

    }

    public UserModel(String userName, String userCity) {
        this.userName = userName;
        this.userCity = userCity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }
}
