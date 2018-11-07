package com.canli.oya.traininventoryfirebase.data.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.data.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.data.model.Train;
import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.canli.oya.traininventoryfirebase.utils.UploadTrainAsyncTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainRepository {

    private static TrainRepository sInstance;
    private List<MinimalTrain> trainList;
    private static final String TAG = "TrainRepository";
    private final LiveData<List<MinimalTrain>> minimalTrains;

    private TrainRepository() {
        Log.d(TAG, "new instance of TrainRepository");
        FirebaseQueryLiveData allTrains = new FirebaseQueryLiveData(FirebaseUtils.getMinimalTrainsRef());
        minimalTrains = Transformations.map(allTrains, new Deserializer());
    }

    public static TrainRepository getInstance() {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository();
            }
        }
        return sInstance;
    }

    private class Deserializer implements Function<DataSnapshot, List<MinimalTrain>> {
        @Override
        public List<MinimalTrain> apply(DataSnapshot dataSnapshot) {
            List<MinimalTrain> minimalTrains = new ArrayList<>();
            for(DataSnapshot data : dataSnapshot.getChildren()){
                minimalTrains.add(data.getValue(MinimalTrain.class));
            }
            return minimalTrains;
        }
    }

    public LiveData<List<MinimalTrain>> getMinimalTrains() {
        return minimalTrains;
    }

    public List<MinimalTrain> getTrainList() {
        return trainList;
    }

    public Train getChosenTrain(int trainId) {
        return null;
    }

    public void insertTrain(Train train) {
        UploadTrainAsyncTask uploadTask = new UploadTrainAsyncTask();
        uploadTask.execute(train);
    }

    public void updateTrain(Train train) {
        UploadTrainAsyncTask uploadTask = new UploadTrainAsyncTask();
        uploadTask.execute(train);
        //TODO: consider doing something different here
    }

    public void deleteTrain(Train train) {

    }

    public void getTrainsFromThisBrand(String brandName) {
        FirebaseUtils.getTrainsInBrandsRef().child(brandName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (trainList != null) {
                    trainList.clear();
                } else {
                    trainList = new ArrayList<>();
                }
                for (DataSnapshot train : dataSnapshot.getChildren()) {
                    trainList.add(train.getValue(MinimalTrain.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTrainsFromThisCategory(String category) {
        FirebaseUtils.getTrainsInCategoriesRef().child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (trainList != null) {
                    trainList.clear();
                } else {
                    trainList = new ArrayList<>();
                }
                for (DataSnapshot train : dataSnapshot.getChildren()) {
                    trainList.add(train.getValue(MinimalTrain.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<MinimalTrain> searchInTrains(String query) {
        return null;
    }

}
