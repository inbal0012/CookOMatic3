package com.example.adopy.Utilities.Models;

import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PetModel implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String Name;
    @SerializedName("kind")
    private String Kind;
    @SerializedName("imageUri")
    private String imageUri;
    @SerializedName("age")
    private Double Age;
    @SerializedName("gender")
    private com.example.adopy.Utilities.Interfaces_and_Emuns.Gender Gender;
    @SerializedName("location")
    private String Location;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("Info")
    private String Info;
    @SerializedName("price")
    private String Price;
    @SerializedName("PostOwnerId")
    private String postOwnerId;

    //id, Name, Kind, imageUri, Age, Gender, Location, latitude, longitude, Info, Price, postOwnerId


    public PetModel(String id, String kind, String name, Double age, Gender gender, String imageUri) {
        this.id = id;
        Kind = kind;
        Name = name;
        Age = age;
        Gender = gender;
        this.imageUri = imageUri;
    }

    public PetModel() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getKind() {
        return Kind;
    }

    public void setKind(String kind) {
        Kind = kind;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Double getAge() {
        return Age;
    }

    public void setAge(Double age) {
        Age = age;
    }


    public com.example.adopy.Utilities.Interfaces_and_Emuns.Gender getGender() {
        return Gender;
    }

    public void setGender(com.example.adopy.Utilities.Interfaces_and_Emuns.Gender gender) {
        Gender = gender;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = Double.parseDouble(latitude);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = Double.parseDouble(longitude);
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
