package com.canli.oya.traininventoryfirebase.utils;

import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.model.Train;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static DatabaseReference getDatabaseUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return FirebaseDatabase.getInstance().getReference().child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getFullTrainsRef() {
        return getDatabaseUserRef().child("trains");
    }

    public static DatabaseReference getMinimalTrainsRef() {
        return getDatabaseUserRef().child("minimalTrains");
    }

    public static String getMinimalTrainsPath(String trainKey) {
        return "minimalTrains/" + trainKey;
    }

    public static DatabaseReference getCategoriesRef() {
        return getDatabaseUserRef().child("categories");
    }

    public static DatabaseReference getBrandsRef() {
        return getDatabaseUserRef().child("brands");
    }

    public static String getTrainsInCategoriesPath(String categoryName, String trainKey) {
        return "trainsInCategories/" + categoryName + "/" + trainKey;
    }

    public static DatabaseReference getTrainsInCategoriesRef() {
        return getDatabaseUserRef().child("trainsInCategories");
    }

    public static DatabaseReference getTrainsInBrandsRef() {
        return getDatabaseUserRef().child("trainsInBrands");
    }

    public static String getTrainsInBrandsPath(String brandName, String trainKey) {
        return "trainsInBrands/" + brandName+ "/" + trainKey;
    }

    public static StorageReference getTrainPhotosRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return FirebaseStorage.getInstance().getReference().child(getCurrentUserId()).child("train_photos" );
        }
        return null;
    }

    public static StorageReference getBrandPhotosRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return FirebaseStorage.getInstance().getReference().child(getCurrentUserId()).child("brands" );
        }
        return null;
    }

    public static MinimalTrain getMinimalVersion(Train train){
        return new MinimalTrain(train.getTrainId(), train.getTrainName(), train.getModelReference(), train.getBrandName(), train.getCategoryName(), train.getImageUri());
    }




}
