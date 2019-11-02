package com.example.adopy.Utilities.Models;

import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PetModel implements Serializable {
    @SerializedName("name")
    private String Name;
    @SerializedName("kind")
    private String Kind;
    @SerializedName("bitmapUrl")
    private String BitmapUrl;
    @SerializedName("location")
    private String Location;
    @SerializedName("age")
    private Double Age;
    @SerializedName("gender")
    private com.example.adopy.Utilities.Interfaces_and_Emuns.Gender Gender;
    @SerializedName("price")
    private String Price;
    @SerializedName("owner")
    private String PostOwner;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("Info")
    private String Info;


    public PetModel(String kind, String name, Double age, Gender gender, String bitmapUrl) {
        Kind = kind;
        Name = name;
        Age = age;
        Gender = gender;
        BitmapUrl = bitmapUrl;
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

    public String getBitmapUri() {
        return BitmapUrl;
    }

    public void setBitmapUrl(String bitmapUrl) {
        BitmapUrl = bitmapUrl;
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

    public String getPostOwner() {
        return PostOwner;
    }

    public void setPostOwner(String postOwner) {
        PostOwner = postOwner;
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
}
