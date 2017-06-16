package com.pma.chat.pmaChat.model;


public class PhoneContact {

    private Long id;

    private String lookUp;

    private String displayName;

    private String phoneNumber;

    public PhoneContact() {
    }

    public PhoneContact(Long id, String lookUp, String displayName) {
        this.id = id;
        this.lookUp = lookUp;
        this.displayName = displayName;
    }

    public PhoneContact(Long id, String lookUp, String displayName, String phoneNumber) {
        this.id = id;
        this.lookUp = lookUp;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookUp() {
        return lookUp;
    }

    public void setLookUp(String lookUp) {
        this.lookUp = lookUp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PhoneContact{" +
                "id=" + id +
                ", lookUp='" + lookUp + '\'' +
                ", displayName='" + displayName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
