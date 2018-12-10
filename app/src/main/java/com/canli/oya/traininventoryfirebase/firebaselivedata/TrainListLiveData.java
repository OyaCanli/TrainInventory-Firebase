package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class TrainListLiveData extends LiveData<List<MinimalTrain>> {
    private static final String LOG_TAG = "TrainListLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<MinimalTrain> trainList;
    private boolean isChangingConfigutations;

    public TrainListLiveData(DatabaseReference ref) {
        this.query = ref;
        query.addChildEventListener(listener);
        Log.d(LOG_TAG, "new instance created");
    }

    public void removeListener() {
        if (!isChangingConfigutations) {
            query.removeEventListener(listener);
            Log.d(LOG_TAG, "listener removed");
            if (trainList != null) trainList.clear();
        }
    }

    public void attachListener() {
        if (!isChangingConfigutations) {
            query.addChildEventListener(listener);
            Log.d(LOG_TAG, "listener attached");
        } else {
            isChangingConfigutations = false;
        }
    }

    public void setChangingConfigutations(boolean changingConfigutations) {
        isChangingConfigutations = changingConfigutations;
    }


    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (trainList == null) trainList = new ArrayList<>();
            trainList.add(dataSnapshot.getValue(MinimalTrain.class));
            setValue(trainList);
            Log.d(LOG_TAG, "onChildAdded. list size: " + trainList.size());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String trainID = dataSnapshot.getKey();
            int listSize = trainList.size();
            for(int i = 0 ; i < listSize ; i++){
                if(trainID.equals(trainList.get(i).getTrainId())){
                    trainList.set(i, dataSnapshot.getValue(MinimalTrain.class));
                    setValue(trainList);
                }
            }
            Log.d(LOG_TAG, "onChildChanged. list size: " + listSize);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String trainID = dataSnapshot.getKey();
            for(MinimalTrain train : trainList){
                if(trainID.equals(train.getTrainId())){
                    trainList.remove(train);
                    setValue(trainList);
                }
            }
            Log.d(LOG_TAG, "onChildRemoved. list size: " + trainList.size());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
