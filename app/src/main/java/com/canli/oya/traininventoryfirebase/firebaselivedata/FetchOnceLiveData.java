package com.canli.oya.traininventoryfirebase.firebaselivedata;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class FetchOnceLiveData extends FirebaseBaseLiveData<DataSnapshot> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FetchOnceLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    void removeListener() {
        query.removeEventListener(listener);
    }

    @Override
    void attachListener() {
        query.addListenerForSingleValueEvent(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("Can't listen to query " + query + databaseError.toException());
        }
    }
}
