package com.example.food_app.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String address;
    private String photoUrl;
    private String contact;
    private String admin;
    private boolean isBlock;
    public User(){}

    public User(String id, String email,String name,String address, String photoUrl, String contact, String admin, boolean isBlock) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.contact = contact;
        this.admin = admin;
        this.isBlock = isBlock;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String date) {
        this.admin = date;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
