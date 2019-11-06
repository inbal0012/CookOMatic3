package com.example.adopy.Utilities.Models;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class User {
    private String id;
    private String username;
    private String imageUri;
    private String age;
    private String gender;
    private String city;
    private String device_token;
    private List<PetModel> favPets;

    public User(String id, String username, String imageUri, String age, String gender, String city, String device_token) {
        this.id = id;
        this.username = username;
        this.imageUri = imageUri;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.device_token = device_token;
        if (favPets == null) {
            favPets = new List<PetModel>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(@Nullable Object o) {
                    return false;
                }

                @NonNull
                @Override
                public Iterator<PetModel> iterator() {
                    return null;
                }

                @Nullable
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public <T> T[] toArray(@Nullable T[] a) {
                    return null;
                }

                @Override
                public boolean add(PetModel petModel) {
                    return false;
                }

                @Override
                public boolean remove(@Nullable Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(@NonNull Collection<? extends PetModel> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, @NonNull Collection<? extends PetModel> c) {
                    return false;
                }

                @Override
                public boolean removeAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public PetModel get(int index) {
                    return null;
                }

                @Override
                public PetModel set(int index, PetModel element) {
                    return null;
                }

                @Override
                public void add(int index, PetModel element) {

                }

                @Override
                public PetModel remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(@Nullable Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(@Nullable Object o) {
                    return 0;
                }

                @NonNull
                @Override
                public ListIterator<PetModel> listIterator() {
                    return null;
                }

                @NonNull
                @Override
                public ListIterator<PetModel> listIterator(int index) {
                    return null;
                }

                @NonNull
                @Override
                public List<PetModel> subList(int fromIndex, int toIndex) {
                    return null;
                }
            };
        }
    }

    public User() {
        if (favPets == null) {
            favPets = new List<PetModel>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(@Nullable Object o) {
                    return false;
                }

                @NonNull
                @Override
                public Iterator<PetModel> iterator() {
                    return null;
                }

                @Nullable
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public <T> T[] toArray(@Nullable T[] a) {
                    return null;
                }

                @Override
                public boolean add(PetModel petModel) {
                    return false;
                }

                @Override
                public boolean remove(@Nullable Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(@NonNull Collection<? extends PetModel> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, @NonNull Collection<? extends PetModel> c) {
                    return false;
                }

                @Override
                public boolean removeAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public PetModel get(int index) {
                    return null;
                }

                @Override
                public PetModel set(int index, PetModel element) {
                    return null;
                }

                @Override
                public void add(int index, PetModel element) {

                }

                @Override
                public PetModel remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(@Nullable Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(@Nullable Object o) {
                    return 0;
                }

                @NonNull
                @Override
                public ListIterator<PetModel> listIterator() {
                    return null;
                }

                @NonNull
                @Override
                public ListIterator<PetModel> listIterator(int index) {
                    return null;
                }

                @NonNull
                @Override
                public List<PetModel> subList(int fromIndex, int toIndex) {
                    return null;
                }
            };
        }
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public List<PetModel> getFavPets() {
        return favPets;
    }

    public void setFavPets(List<PetModel> favPets) {
        this.favPets = favPets;
    }

    public void addToFav(PetModel petModel) {
        this.favPets.add(petModel);
    }
}
