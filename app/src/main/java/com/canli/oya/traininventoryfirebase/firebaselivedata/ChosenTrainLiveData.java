package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ChosenTrainLiveData extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "ChosenTrainLiveData";

    private final DatabaseReference reference;
    private final MyValueEventListener listener = new MyValueEventListener();
    private final Handler handler = new Handler();
    private boolean pendingListenerRemoval;
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            reference.removeEventListener(listener);
            pendingListenerRemoval = false;
        }
    };

    public ChosenTrainLiveData(DatabaseReference ref) {
        this.reference = ref;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        if (pendingListenerRemoval) {
            handler.removeCallbacks(removeListener);
        } else {
            reference.addValueEventListener(listener);
        }
        pendingListenerRemoval = false;
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        handler.postDelayed(removeListener, 2000);
        pendingListenerRemoval = true;
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
