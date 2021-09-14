package com.example.kahaapplication;

import java.util.Date;

public class User {

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private String userBirthDate;
    private String userDescription;
    private Boolean userIsFinder;

    public User(String userFirstName, String userLastName, String userEmail,
                String userPhone, String userBirthDate, String userDescription, boolean userIsFinder) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userBirthDate = userBirthDate;
        this.userDescription = userDescription;
        this.userIsFinder = userIsFinder;
    }

    public String getUserFirstName() { return userFirstName; }

    public String getUserLastName() { return userLastName; }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getUserPhone() { return userPhone; }

    public String getUserBirthDate() { return userBirthDate; }

    public String getUserDescription() { return userDescription; }

    public boolean getUserIsFinder() { return userIsFinder; }
}
