package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

public class ChosenTrainFactory extends ViewModelProvider.NewInstanceFactory {

    private final int mTrainId;
    private TrainRepository mTrainRepo;

    public ChosenTrainFactory(TrainRepository trainRepo, int trainId) {
        mTrainId = trainId;
        mTrainRepo = trainRepo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChosenTrainViewModel(mTrainRepo, mTrainId);
    }
}