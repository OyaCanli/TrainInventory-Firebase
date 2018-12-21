package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;

import timber.log.Timber;

abstract class FirebaseBaseLiveData<T> extends LiveData<T> {

    private boolean isChangingConfigurations;

    @Override
    protected void onActive() {
        Timber.d("onActive");
        if (!isChangingConfigurations) {
            attachListener();
        }
    }

    public void setChangingConfigutations(boolean changingConfigutations) {
        isChangingConfigurations = changingConfigutations;
        Timber.d("set changing configurations: " + changingConfigutations);
    }

    abstract void removeListener();

    abstract void attachListener();

    @Override
    protected void onInactive() {
        Timber.d("onInactive");
        if (!isChangingConfigurations) {
            removeListener();
        }
    }
}
