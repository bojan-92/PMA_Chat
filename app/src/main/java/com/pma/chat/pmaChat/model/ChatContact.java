package com.pma.chat.pmaChat.model;


public class ChatContact {

    private Long id;

    private String name;

    private String phoneNumber;

    public ChatContact() {
        // needed by ormlite
    }

    public ChatContact(Long id, String name, String number) {
        this.id = id;
        this.name = name;
        this.phoneNumber = number;
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
                '}';
    }
}
