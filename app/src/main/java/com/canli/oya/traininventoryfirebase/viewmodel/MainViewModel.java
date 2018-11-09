package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.repositories.BrandRepository;
import com.canli.oya.traininventoryfirebase.repositories.CategoryRepository;
import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

import java.util.List;

public class MainViewModel extends ViewModel {

    private TrainRepository mTrainRepo;
    private BrandRepository mBrandRepo;
    private CategoryRepository mCategoryRepo;
    private final MutableLiveData<Brand> mChosenBrand = new MutableLiveData<>();

    public MainViewModel() {
        mCategoryRepo = CategoryRepository.getInstance();
        mBrandRepo = BrandRepository.getInstance();
        mTrainRepo = TrainRepository.getInstance(); //TODO: Consider changing this, because no need to load all trains in startup
    }

    /////////// TRAIN LIST /////////////

    public LiveData<List<MinimalTrain>> getTrainList() {
        return mTrainRepo.getMinimalTrains();
    }

    public void insertTrain(Train train){
        mTrainRepo.insertTrain(train);
    }

    public void updateTrain(Train train){
        mTrainRepo.updateTrain(train);
    }

    public void deleteTrain(Train train){
        mTrainRepo.deleteTrain(train);
    }

    ////////////// BRAND LIST //////////////////

    public LiveData<List<Brand>> getBrandList() {
        return mBrandRepo.getBrandList();
    }

    public LiveData<Brand> getChosenBrand() {
        return mChosenBrand;
    }

    public void setChosenBrand(Brand chosenBrand){
        mChosenBrand.setValue(chosenBrand);
    }

    public void insertBrand(Brand brand){
        mBrandRepo.insertBrand(brand);
    }

    public void deleteBrand(Brand brand){
        mBrandRepo.deleteBrand(brand);
    }

    public void updateBrand(Brand brand) {
        mBrandRepo.updateBrand(brand);
    }

    public boolean isThisBrandUsed(String brandName){
        return mBrandRepo.isThisBrandUsed(brandName);
    }

    //////////////// CATEGORY LIST //////////////////

    public LiveData<List<String>> getCategoryList() {
        return mCategoryRepo.getCategoryList();
    }

    public void deleteCategory(String category){
        mCategoryRepo.deleteCategory(category);
    }

    public void insertCategory(String category){
        mCategoryRepo.insertCategory(category);
    }

    public boolean isThisCategoryUsed(String category){
        return mCategoryRepo.isThisCategoryUsed(category);
    }

    ///////////// SEARCH //////////////////////////
    /*public List<Train> getTrainsFromThisBrand(String brandName){
        return mTrainRepo.getTrainsFromThisBrand(brandName);
    }

    public List<Train> getTrainsFromThisCategory(String category){
        return mTrainRepo.getTrainsFromThisCategory(category);
    }*/

    public List<MinimalTrain> searchInTrains(String query){
        return mTrainRepo.searchInTrains(query);
    }
}
