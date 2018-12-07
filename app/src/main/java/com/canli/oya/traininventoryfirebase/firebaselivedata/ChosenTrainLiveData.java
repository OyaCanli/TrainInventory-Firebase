package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ChosenTrainLiveData extends FirebaseBaseLiveData<DataSnapshot> {
    private static final String LOG_TAG = "ChosenTrainLiveData";

    private final DatabaseReference reference;
    private final MyValueEventListener listener = new MyValueEventListener();

    public ChosenTrainLiveData(DatabaseReference ref) {
        this.reference = ref;
    }

    @Override
    void removePendingListener() {
        reference.removeEventListener(listener);
    }

    @Override
    void attachListener() {
        reference.addValueEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to reference " + reference, databaseError.toException());
        }
    }
}
