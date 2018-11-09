package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

public class ChosenTrainViewModel extends ViewModel {
    private final Train chosenTrain;

    ChosenTrainViewModel(TrainRepository trainRepo, int trainId) {
        chosenTrain = trainRepo.getChosenTrain(trainId);
    }

    public Train getChosenTrain() {
        return chosenTrain;
    }

}
