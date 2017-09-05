package com.pma.chat.pmaChat.model;


import java.io.Serializable;

public class ChatContact implements Serializable {

    private Long id;

    private String name;

    private String phoneNumber;

    private String firebaseUserId;

    public ChatContact() {
    }

    public ChatContact(Long id, String firebaseUserId, String name, String number) {
        this.id = id;
        this.firebaseUserId = firebaseUserId;
        this.name = name;
        this.phoneNumber = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
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

    @Override
    public String toString() {
        return "ChatContact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firebaseUserId='" + firebaseUserId + '\'' +
                '}';
    }
}
