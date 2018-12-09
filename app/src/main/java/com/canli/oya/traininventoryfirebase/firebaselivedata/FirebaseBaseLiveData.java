package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

abstract class FirebaseBaseLiveData<T> extends LiveData<T> {
    private static final String LOG_TAG = "TrainListLiveData";

    private final Handler handler = new Handler();
    private boolean pendingListenerRemoval;
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            removePendingListener();
            pendingListenerRemoval = false;
        }
    };

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        if (pendingListenerRemoval) {
            handler.removeCallbacks(removeListener);
        } else {
            attachListener();
        }
        pendingListenerRemoval = false;
    }

    abstract void removePendingListener();

    abstract void attachListener();

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        handler.postDelayed(removeListener, 2000);
        pendingListenerRemoval = true;
        removePendingListener();
    }
}
