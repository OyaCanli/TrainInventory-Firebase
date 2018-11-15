package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

public class ChosenTrainViewModel extends ViewModel {

    private final LiveData<Train> chosenTrain;

    ChosenTrainViewModel(TrainRepository trainRepo, String trainId) {
        chosenTrain = trainRepo.getTrainDetails(trainId);
    }

    public LiveData<Train> getChosenTrain() {
        return chosenTrain;
    }

}
