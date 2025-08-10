package com.example.onlineshop.Domain;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String name;
    private String email;
    private String uid;

    private String profileImageUrl;

    public UserModel() {
    }

    public UserModel(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}