package com.pma.chat.pmaChat.model;

import java.util.Calendar;


public class User {

    private String name;

//    private String email;

    private String phoneNumber;

    private String profileImageUri;

    public static final String USER_FCM_TOKEN_FIELD = "fcmtoken";
    private String fcmToken;


    public User() {
    }

    public User(String name, String phoneNumber, String profileImageUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImageUri = profileImageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
