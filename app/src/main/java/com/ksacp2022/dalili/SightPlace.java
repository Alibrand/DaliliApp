package com.ksacp2022.dalili;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class SightPlace {
    String id;
    String name;
    String description;
    String image_url;
    List<String> gallery_images=new ArrayList<>();
    List<String> likes=new ArrayList<>();
    GeoPoint location;
    public SightPlace() {
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getGallery_images() {
        return gallery_images;
    }

    public void setGallery_images(List<String> gallery_images) {
        this.gallery_images = gallery_images;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
