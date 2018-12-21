package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

import timber.log.Timber;

public class ChosenTrainViewModel extends ViewModel {

    private MediatorLiveData<Train> currentTrainMediator = new MediatorLiveData();

    ChosenTrainViewModel(TrainRepository trainRepo, String trainId) {
        Timber.d("constructor of viewmodel");
        final LiveData<Train> chosenTrain = trainRepo.getTrainDetails(trainId);
        currentTrainMediator.addSource(chosenTrain, new Observer<Train>() {
            @Override
            public void onChanged(@Nullable Train train) {
                Timber.d("change in chosenTrain triggers change in mediatorlivedata");
                if (train != null) {
                    currentTrainMediator.setValue(train);
                }
            }
        });
    }

    public LiveData<Train> getChosenTrain() {
        return currentTrainMediator;
    }

    public void setChosenTrain(Train chosenTrain) {
        currentTrainMediator.setValue(chosenTrain);
    }
}
