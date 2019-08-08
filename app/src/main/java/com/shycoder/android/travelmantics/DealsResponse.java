package com.shycoder.android.travelmantics;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DealsResponse {
    private String title;
    private String price;
    private String description;
    private String imageUrl;

    public DealsResponse() {
    }

    public DealsResponse(String title, String description, String price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }
}