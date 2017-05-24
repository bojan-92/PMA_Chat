package com.pma.chat.pmaChat.model;

import java.util.Calendar;


public class UserInfo {

    private String firstName;

    private String lastName;

  //  private Calendar birthday;

    public UserInfo() {
    }

    public UserInfo(String firstName, String lastName, Calendar birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
    //    this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
/*
    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }*/
}
