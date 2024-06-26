package com.example.food_app.model;

import java.io.Serializable;

public class Food implements Serializable {
    private int id;
    private String title;
    private double price;
    private String category;
    private String status;
    private String description;
    private int photo;
    private int quantity;
    private String photoString;
    public Food() {}

    public Food(int id, String title, double price, String category, String status, String description, int photo, int quantity, String photoString) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.status = status;
        this.description = description;
        this.photo = photo;
        this.quantity = quantity;
        this.photoString = photoString;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getPhotoString() {
        return photoString;
    }

    public void setPhotoString(String photoString) {
        this.photoString = photoString;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", photo=" + photo +
                ", quantity=" + quantity +
                '}';
    }
}
