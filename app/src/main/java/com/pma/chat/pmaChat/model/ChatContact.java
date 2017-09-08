package com.pma.chat.pmaChat.model;


import java.io.Serializable;

public class ChatContact implements Serializable {

    private Long id;

    // name from phone contacts
    private String name;

    // name from firebase
    private String firebaseName;

    private String email;

    private String phoneNumber;

    private String firebaseUserId;

    public ChatContact() {
    }

    public ChatContact(Long id, String name, String firebaseName, String email, String phoneNumber, String firebaseUserId) {
        this.id = id;
        this.name = name;
        this.firebaseName = firebaseName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firebaseUserId = firebaseUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirebaseName() {
        return firebaseName;
    }

    public void setFirebaseName(String firebaseName) {
        this.firebaseName = firebaseName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
    }

    @Override
    public String toString() {
        return "ChatContact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firebaseUserId='" + firebaseUserId + '\'' +
                '}';
    }
}
