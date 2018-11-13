package com.canli.oya.traininventoryfirebase.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainRepository {

    private static TrainRepository sInstance;
    private static final String TAG = "TrainRepository";
    private final LiveData<List<MinimalTrain>> minimalTrains;
    private String trainPushId;

    private TrainRepository() {
        Log.d(TAG, "new instance of TrainRepository");
        FirebaseQueryLiveData allTrains = new FirebaseQueryLiveData(FirebaseUtils.getMinimalTrainsRef());
        minimalTrains = Transformations.map(allTrains, new ListDeserializer());
    }

    public static TrainRepository getInstance() {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository();
            }
        }
        return sInstance;
    }

    private class ListDeserializer implements Function<DataSnapshot, List<MinimalTrain>> {
        @Override
        public List<MinimalTrain> apply(DataSnapshot dataSnapshot) {
            List<MinimalTrain> minimalTrains = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                minimalTrains.add(data.getValue(MinimalTrain.class));
            }
            return minimalTrains;
        }
    }

    public LiveData<Train> initializeObservingTrainDetails(String trainId) {
        FirebaseQueryLiveData chosenTrainSource = new FirebaseQueryLiveData(FirebaseUtils.getFullTrainsRef().child(trainId));
        return Transformations.map(chosenTrainSource, new ChosenTrainDeserializer());
    }

    private class ChosenTrainDeserializer implements Function<DataSnapshot, Train> {
        @Override
        public Train apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Train.class);
        }
    }

    public LiveData<List<MinimalTrain>> getMinimalTrains() {
        return minimalTrains;
    }

    public void insertTrain(Train train) {
        //Push the full train object
        DatabaseReference TRAINS_REF = FirebaseUtils.getFullTrainsRef();
        trainPushId = TRAINS_REF.push().getKey();
        train.setTrainId(trainPushId);
        TRAINS_REF.child(trainPushId).setValue(train);

        //Push the minimal train object in multiple locations
        MinimalTrain minimalTrain = FirebaseUtils.getMinimalVersion(train);
        Map<String, Object> trainValues = minimalTrain.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtils.getMinimalTrainsPath(trainPushId), trainValues);
        childUpdates.put(FirebaseUtils.getTrainsInCategoriesPath(minimalTrain.getCategoryName(), trainPushId), trainValues);
        childUpdates.put(FirebaseUtils.getTrainsInBrandsPath(minimalTrain.getBrandName(), trainPushId), trainValues);
        FirebaseUtils.getDatabaseUserRef().updateChildren(childUpdates);
    }

    public void updateTrain(Train train) {
        //Update the full train object
        DatabaseReference TRAINS_REF = FirebaseUtils.getFullTrainsRef();
        trainPushId = train.getTrainId();
        TRAINS_REF.child(trainPushId).setValue(train);

        //Update the minimal train object in multiple locations
        MinimalTrain minimalTrain = FirebaseUtils.getMinimalVersion(train);
        Map<String, Object> trainValues = minimalTrain.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtils.getMinimalTrainsPath(trainPushId), trainValues);
        childUpdates.put(FirebaseUtils.getTrainsInCategoriesPath(minimalTrain.getCategoryName(), trainPushId), trainValues);
        childUpdates.put(FirebaseUtils.getTrainsInBrandsPath(minimalTrain.getBrandName(), trainPushId), trainValues);
        FirebaseUtils.getDatabaseUserRef().updateChildren(childUpdates);
    }

    public void updateTrainImageUrl(Train trainToUpdate) {
        //Update the uri of the corresponding train object
        FirebaseUtils.getFullTrainsRef().child(trainPushId).child("imageUri").setValue(trainToUpdate.getImageUri());

        //Update the image url of the corresponding minimal train objects
        FirebaseUtils.getMinimalTrainsRef().child(trainPushId).child("imageUri").setValue(trainToUpdate.getImageUri());
        FirebaseUtils.getTrainsInBrandsRef().child(trainToUpdate.getBrandName()).child(trainPushId).child("imageUri").setValue(trainToUpdate.getImageUri());
        FirebaseUtils.getTrainsInCategoriesRef().child(trainToUpdate.getCategoryName()).child(trainPushId).child("imageUri").setValue(trainToUpdate.getImageUri());
    }

    public void deleteTrain(Train train) {
        String trainId = train.getTrainId();
        //Delete the full train object
        FirebaseUtils.getFullTrainsRef().child(trainId).removeValue();

        //Delete the minimal train object from multiple locations
        Map<String, Object> nullTrainValues = new HashMap<>();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtils.getMinimalTrainsPath(trainPushId), nullTrainValues);
        childUpdates.put(FirebaseUtils.getTrainsInCategoriesPath(train.getCategoryName(), trainPushId), nullTrainValues);
        childUpdates.put(FirebaseUtils.getTrainsInBrandsPath(train.getBrandName(), trainPushId), nullTrainValues);
        FirebaseUtils.getDatabaseUserRef().updateChildren(childUpdates);

        //Delete train image, if exists
        String imageUrl = train.getImageUri();
        if (imageUrl != null) {
            FirebaseUtils.getImageReferenceFromUrl(imageUrl)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "image successfully deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "error during deleting image");
                        }
                    });
        }
    }

    public LiveData<List<MinimalTrain>> getTrainsFromThisBrand(String brandName) {
        FirebaseQueryLiveData livedata = new FirebaseQueryLiveData(FirebaseUtils.getTrainsInBrandsRef().child(brandName));
        return Transformations.map(livedata, new ListDeserializer());
    }

    public LiveData<List<MinimalTrain>> getTrainsFromThisCategory(String category) {
        FirebaseQueryLiveData livedata = new FirebaseQueryLiveData(FirebaseUtils.getTrainsInCategoriesRef().child(category));
        return Transformations.map(livedata, new ListDeserializer());
    }

    public List<MinimalTrain> searchInTrains(String query) {
        //TODO:
        return null;
    }

}
