package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CategoryListLiveData extends FirebaseBaseLiveData<List<String>> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<String> categoryList;

    public CategoryListLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    public void removeListener() {
        Timber.d("listener removed");
        query.removeEventListener(listener);
        if (categoryList != null) categoryList.clear();
    }

    public void attachListener() {
        query.addChildEventListener(listener);
        Timber.d("listener attached");

    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (categoryList == null) categoryList = new ArrayList<>();
            categoryList.add(dataSnapshot.getValue(String.class));
            setValue(categoryList);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            categoryList.remove(dataSnapshot.getKey());
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
