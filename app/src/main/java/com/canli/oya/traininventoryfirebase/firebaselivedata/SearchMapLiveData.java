package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class SearchMapLiveData extends LiveData<Map<String, String>> {
    private static final String LOG_TAG = "SearchMapLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private Map<String, String> searchLookup = new HashMap<>();
    private final Handler handler = new Handler();
    private boolean pendingListenerRemoval;
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            searchLookup.clear();
            pendingListenerRemoval = false;
        }
    };

    public SearchMapLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        if (pendingListenerRemoval) {
            handler.removeCallbacks(removeListener);
        } else {
            query.addChildEventListener(listener);
        }
        pendingListenerRemoval = false;
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        handler.postDelayed(removeListener, 2000);
        pendingListenerRemoval = true;
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            searchLookup.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            setValue(searchLookup);
            Log.d(LOG_TAG, "onChildAdded. list size: " + searchLookup.size());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            searchLookup.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            setValue(searchLookup);
            Log.d(LOG_TAG, "onChildChanged. list size: " + searchLookup.size());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String trainID = dataSnapshot.getKey();
            searchLookup.remove(trainID);
            Log.d(LOG_TAG, "onChildRemoved. list size: " + searchLookup.size());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
