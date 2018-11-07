package com.canli.oya.traininventoryfirebase.utils;

import com.canli.oya.traininventoryfirebase.data.repositories.BrandRepository;
import com.canli.oya.traininventoryfirebase.data.repositories.CategoryRepository;
import com.canli.oya.traininventoryfirebase.data.repositories.TrainRepository;
import com.canli.oya.traininventoryfirebase.viewmodel.ChosenTrainFactory;

public class InjectorUtils {

    public static TrainRepository provideTrainRepo(){
        return TrainRepository.getInstance();
    }

    public static BrandRepository provideBrandRepo(){
        return BrandRepository.getInstance();
    }

    public static CategoryRepository provideCategoryRepo(){
        return CategoryRepository.getInstance();
    }

    public static ChosenTrainFactory provideChosenTrainFactory(int trainId){
        return new ChosenTrainFactory(provideTrainRepo(), trainId);
    }

}
