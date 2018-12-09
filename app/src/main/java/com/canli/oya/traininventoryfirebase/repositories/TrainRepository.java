package com.canli.oya.traininventoryfirebase.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.firebaselivedata.ChosenTrainLiveData;
import com.canli.oya.traininventoryfirebase.firebaselivedata.FetchOnceLiveData;
import com.canli.oya.traininventoryfirebase.firebaselivedata.SearchMapLiveData;
import com.canli.oya.traininventoryfirebase.firebaselivedata.TrainListLiveData;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.model.Train;
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
    private String trainPushId;
    private TrainListLiveData minimalTrains;
    private SearchMapLiveData searchLookUp;

    private TrainRepository() {
        Log.d(TAG, "new instance of TrainRepository");
    }

    public static TrainRepository getInstance() {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository();
            }
        }
        return sInstance;
    }

    ////////////////// ALL TRAINS //////////////////////////
    public TrainListLiveData getAllMinimalTrains() {
        if(minimalTrains == null){
            minimalTrains = new TrainListLiveData(FirebaseUtils.getMinimalTrainsRef());
            minimalTrains.attachListener();
        }
        return minimalTrains;
    }

    ////////////// TRAIN DETAILS /////////////////////////

    public LiveData<Train> getTrainDetails(String trainId) {
        ChosenTrainLiveData chosenTrainSource = new ChosenTrainLiveData(FirebaseUtils.getFullTrainsRef().child(trainId));
        return Transformations.map(chosenTrainSource, new ChosenTrainDeserializer());
    }

    private class ChosenTrainDeserializer implements Function<DataSnapshot, Train> {
        @Override
        public Train apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Train.class);
        }
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

        //Put an entry in searchLookUp node, with trainIds as the key and concatenation of fields to look up as a value.
        String searchText = concatenateFields(train.getTrainName(), train.getModelReference(), train.getDescription());
        FirebaseUtils.getSearchLookUpRef().child(trainPushId).setValue(searchText);
    }

    private String concatenateFields(String trainName, String reference, String description){
        return trainName + " " + reference + " " + description;
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

        //Edit the entry in searchLookUp node, with trainIds as the key and concatenation of fields to look up as a value.
        String searchText = concatenateFields(train.getTrainName(), train.getModelReference(), train.getDescription());
        FirebaseUtils.getSearchLookUpRef().child(trainPushId).setValue(searchText);
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
        childUpdates.put(FirebaseUtils.getMinimalTrainsPath(trainId), nullTrainValues);
        childUpdates.put(FirebaseUtils.getTrainsInCategoriesPath(train.getCategoryName(), trainId), nullTrainValues);
        childUpdates.put(FirebaseUtils.getTrainsInBrandsPath(train.getBrandName(), trainId), nullTrainValues);
        FirebaseUtils.getDatabaseUserRef().updateChildren(childUpdates);

        //Delete the corresponding entry for search
        FirebaseUtils.getSearchLookUpRef().child(trainId).removeValue();

        //Delete train image, if exists
        deleteImage(train.getImageUri());
    }

    public void deleteImage(String imageUrl) {
        //Delete train image, if exists
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
        FetchOnceLiveData livedata = new FetchOnceLiveData(FirebaseUtils.getTrainsInBrandsRef().child(brandName));
        return Transformations.map(livedata, new TrainListDeserializer());
    }

    public LiveData<List<MinimalTrain>> getTrainsFromThisCategory(String category) {
        FetchOnceLiveData livedata = new FetchOnceLiveData(FirebaseUtils.getTrainsInCategoriesRef().child(category));
        return Transformations.map(livedata, new TrainListDeserializer());
    }

    public LiveData<Map<String, String>> loadSearchLookUp(){
        if (searchLookUp == null) {
            searchLookUp = new SearchMapLiveData(FirebaseUtils.getSearchLookUpRef());
            searchLookUp.attachListener();
        }
        return searchLookUp;
    }

    ////////////////////////// SEARCH //////////////////////////////////

    public void setConfigurationChange(boolean configurationChange) {
        if (minimalTrains != null) {
            minimalTrains.setChangingConfigutations(configurationChange);
        }
        if (searchLookUp != null) {
            searchLookUp.setChangingConfigutations(configurationChange);
        }
    }

    public void removeListener() {
        if (minimalTrains != null) {
            minimalTrains.removeListener();
        }
        if (searchLookUp != null) {
            searchLookUp.removeListener();
        }
    }

    public void attachListener() {
        if (minimalTrains != null) {
            minimalTrains.attachListener();
        }
        if (searchLookUp != null) {
            searchLookUp.attachListener();
        }
    }

    private class TrainListDeserializer implements Function<DataSnapshot, List<MinimalTrain>> {
        @Override
        public List<MinimalTrain> apply(DataSnapshot dataSnapshot) {
            List<MinimalTrain> trains = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                trains.add(data.getValue(MinimalTrain.class));
            }
            return trains;
        }
    }

}
