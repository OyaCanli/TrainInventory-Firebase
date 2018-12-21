package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.os.Handler;

import timber.log.Timber;

abstract class FirebaseBaseLiveData<T> extends LiveData<T> {

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
        Timber.d("onActive");
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
        Timber.d("onInactive");
        handler.postDelayed(removeListener, 2000);
        pendingListenerRemoval = true;
    }
}
