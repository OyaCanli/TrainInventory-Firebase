package com.canli.oya.traininventoryfirebase.data.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MinimalTrain implements Serializable {

    private String trainName;
    private String modelReference;
    private String brandName;
    private String categoryName;
    private String imageUri;

    public MinimalTrain() {
    }

    public MinimalTrain(String trainName, String modelReference, String brandName, String categoryName, String imageUri) {
        this.trainName = trainName;
        this.modelReference = modelReference;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
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

    public String getImageUri() {
        return imageUri;
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

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("trainName", trainName);
        result.put("modelReference", modelReference);
        result.put("brandName", brandName);
        result.put("categoryName", categoryName);
        result.put("imageUri", imageUri);

        return result;
    }
}
