package com.audit.CorporateAuditSystem;

public class User {

    private String username;
    private String password;
    private String role;

    // NEW
    private String profileImage;


    // Existing constructor
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    // NEW constructor
    public User(
            String username,
            String password,
            String role,
            String profileImage) {

        this.username = username;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getRole() {
        return role;
    }


    // NEW getter
    public String getProfileImage() {
        return profileImage;
    }


    @Override
    public String toString() {
        return "Username: " + username
                + ", Role: " + role;
    }
}