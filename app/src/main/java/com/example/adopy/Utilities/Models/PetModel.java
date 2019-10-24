package com.example.adopy.Utilities.Models;

import android.graphics.Bitmap;

import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PetModel implements Serializable {
    @SerializedName("name")
    private String Name;
    @SerializedName("kind")
    private String Kind;
    //@SerializedName("bitmap")
    //private android.graphics.Bitmap Bitmap;
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


    public PetModel(String kind, String name, Double age, Gender gender, android.graphics.Bitmap bitmap) {
        Kind = kind;
        Name = name;
        Age = age;
        Gender = gender;
        //Bitmap = bitmap;
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

    public android.graphics.Bitmap getBitmap() {
        return Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8); //Bitmap;
    }

    public void setBitmap(android.graphics.Bitmap bitmap) {
        //Bitmap = bitmap;
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
}