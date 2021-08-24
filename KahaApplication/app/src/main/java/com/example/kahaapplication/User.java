package com.example.kahaapplication;

import java.util.Date;

public class User {

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userBirthDate;
    private Boolean userIsFinder;

    public User(String userFirstName, String userLastName, String userEmail, String userPassword,
                String userPhone, String userBirthDate, boolean userIsFinder) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userBirthDate = userBirthDate;
        this.userIsFinder = userIsFinder;
    }

    public String getUserFirstName() { return userFirstName; }

    public String getUserLastName() { return userLastName; }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public String getUserPhone() { return userPhone; }

    public String getUserBirthDate() { return userBirthDate; }

    public boolean getUserIsFinder() { return userIsFinder; }
}
