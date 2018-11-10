package com.canli.oya.traininventoryfirebase.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Train implements Serializable {

    private String trainId;
    private String trainName;
    private String modelReference;
    private String brandName;
    private String categoryName;
    private int quantity;
    private String imageUri;
    private String description;
    private String location;
    private String scale;

    public Train() {
    }

    public Train(String trainId, String trainName, String modelReference, String brandName, String categoryName, int quantity, String imageUri, String description, String location, String scale) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.modelReference = modelReference;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.imageUri = imageUri;
        this.description = description;
        this.location = location;
        this.scale = scale;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getModelReference() {
        return modelReference;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getScale() {
        return scale;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setModelReference(String modelReference) {
        this.modelReference = modelReference;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainName='" + trainName + '\'' +
                ", modelReference='" + modelReference + '\'' +
                ", brandName='" + brandName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", quantity=" + quantity +
                ", imageUri='" + imageUri + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", scale='" + scale + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("trainId", trainId);
        result.put("trainName", trainName);
        result.put("modelReference", modelReference);
        result.put("brandName", brandName);
        result.put("categoryName", categoryName);
        result.put("quantity", quantity);
        result.put("imageUri", imageUri);
        result.put("description", description);
        result.put("location", location);
        result.put("scale", scale);

        return result;
    }
}
