package com.canli.oya.traininventoryfirebase.firebaselivedata;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class ChosenTrainLiveData extends FirebaseBaseLiveData<DataSnapshot> {

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
            Timber.e("Can't listen to reference " + reference + databaseError.toException());
        }
    }
}
