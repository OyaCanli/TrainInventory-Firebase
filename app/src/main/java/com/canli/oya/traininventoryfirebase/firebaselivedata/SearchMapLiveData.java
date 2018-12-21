package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class SearchMapLiveData extends FirebaseBaseLiveData<Map<String, String>> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private Map<String, String> searchLookup = new HashMap<>();

    public SearchMapLiveData(DatabaseReference ref) {
        this.query = ref;
        Timber.d("new instance created");
    }

    public void removeListener() {
        query.removeEventListener(listener);
        Timber.d("listener removed");
        searchLookup.clear();
    }

    public void attachListener() {
        query.addChildEventListener(listener);
        Timber.d("listener attached");
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            searchLookup.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            setValue(searchLookup);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            searchLookup.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            setValue(searchLookup);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String trainID = dataSnapshot.getKey();
            searchLookup.remove(trainID);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("Can't listen to query " + query + databaseError.toException());
        }
    }
}
