package com.example.adopy.Utilities.Models;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String age;
    private String gender;
    private String city;

    public User(String id, String username, String imageURL, String age, String gender, String city) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.age = age;
        this.gender = gender;
        this.city = city;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
