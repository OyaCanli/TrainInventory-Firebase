package com.canli.oya.traininventoryfirebase.utils;

import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;
import com.canli.oya.traininventoryfirebase.viewmodel.ChosenTrainFactory;

public class InjectorUtils {

    public static ChosenTrainFactory provideChosenTrainFactory(int trainId){
        return new ChosenTrainFactory(TrainRepository.getInstance(), trainId);
    }

}
