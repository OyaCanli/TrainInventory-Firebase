package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TrainListLiveData extends FirebaseBaseLiveData<List<MinimalTrain>> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<MinimalTrain> trainList;

    public TrainListLiveData(DatabaseReference ref) {
        this.query = ref;
        Timber.d("new instance created");
    }

    public void removeListener() {
        query.removeEventListener(listener);
        Timber.d("listener removed");
        if (trainList != null) trainList.clear();

    }

    public void attachListener() {
        query.addChildEventListener(listener);
        Timber.d("listener attached");
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (trainList == null) trainList = new ArrayList<>();
            trainList.add(dataSnapshot.getValue(MinimalTrain.class));
            setValue(trainList);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String trainID = dataSnapshot.getKey();
            int listSize = trainList.size();
            for (int i = 0; i < listSize; i++) {
                if (trainID.equals(trainList.get(i).getTrainId())) {
                    trainList.set(i, dataSnapshot.getValue(MinimalTrain.class));
                    setValue(trainList);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String trainID = dataSnapshot.getKey();
            for (MinimalTrain train : trainList) {
                if (trainID.equals(train.getTrainId())) {
                    trainList.remove(train);
                    setValue(trainList);
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }
}
