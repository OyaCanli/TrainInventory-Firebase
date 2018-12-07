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

import java.util.ArrayList;
import java.util.List;

public class CategoryListLiveData extends LiveData<List<String>> {
    private static final String LOG_TAG = "CategoryListLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<String> categoryList;
    private final Handler handler = new Handler();
    private boolean pendingListenerRemoval;
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            if (categoryList != null) categoryList.clear();
            pendingListenerRemoval = false;
        }
    };

    public CategoryListLiveData(DatabaseReference ref) {
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
            if (categoryList == null) categoryList = new ArrayList<>();
            categoryList.add(dataSnapshot.getValue(String.class));
            setValue(categoryList);
            Log.d(LOG_TAG, "onChildAdded. list size: " + categoryList.size());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            categoryList.remove(dataSnapshot.getKey());
            Log.d(LOG_TAG, "onChildRemoved. list size: " + categoryList.size());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
