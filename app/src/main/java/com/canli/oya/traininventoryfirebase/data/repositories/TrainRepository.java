package com.canli.oya.traininventoryfirebase.data.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.data.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.data.model.Train;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.canli.oya.traininventoryfirebase.utils.UploadTrainAsyncTask;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainRepository {

    private static TrainRepository sInstance;
    private List<MinimalTrain> trainList;
    private static final String TAG = "TrainRepository";

    private TrainRepository() {
        loadTrains();
    }

    public static TrainRepository getInstance() {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository();
            }
        }
        return sInstance;
    }

    private void loadTrains() {
        Log.d(TAG, "loading trains");
        trainList = new ArrayList<>();
        FirebaseUtils.getMinimalTrainsRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                trainList.add(dataSnapshot.getValue(MinimalTrain.class));
                Log.d(TAG, "train list size: " + trainList.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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

    public List<Train> searchInTrains(String query) {
        return null;
    }

}
