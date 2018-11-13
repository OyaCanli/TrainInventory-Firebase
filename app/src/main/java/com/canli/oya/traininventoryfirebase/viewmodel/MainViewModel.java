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

    public void updateTrainImageUrl(Train trainToUpdate){
        mTrainRepo.updateTrainImageUrl(trainToUpdate);
    }

    public void deleteTrain(Train train){
        mTrainRepo.deleteTrain(train);
    }

    ////////////// BRAND LIST //////////////////

    public void initializeBrandRepo(BrandRepository.BrandUseListener listener){
        mBrandRepo = BrandRepository.getInstance(listener);
    }

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

    public void deleteBrand(String brandName){
        mBrandRepo.deleteBrand(brandName);
    }

    public void updateBrand(Brand brand) {
        mBrandRepo.updateBrand(brand);
    }

    public void isThisBrandUsed(String brandName){
        mBrandRepo.checkIfThisBrandUsed(brandName);
    }

    //////////////// CATEGORY LIST //////////////////

    public void initializeCategoryRepo(CategoryRepository.CategoryUseListener listener){
        mCategoryRepo = CategoryRepository.getInstance(listener);
    }

    public LiveData<List<String>> getCategoryList() {
        return mCategoryRepo.getCategoryList();
    }

    public void deleteCategory(String category){
        mCategoryRepo.deleteCategory(category);
    }

    public void insertCategory(String category){
        mCategoryRepo.insertCategory(category);
    }

    public void checkIfCategoryUsed(String category){
        mCategoryRepo.checkIfCategoryUsed(category);
    }

    ///////////// SEARCH //////////////////////////
    public LiveData<List<MinimalTrain>> getTrainsFromThisBrand(String brandName){
        return mTrainRepo.getTrainsFromThisBrand(brandName);
    }

    public LiveData<List<MinimalTrain>> getTrainsFromThisCategory(String category){
        return mTrainRepo.getTrainsFromThisCategory(category);
    }

    public List<MinimalTrain> searchInTrains(String query){
        return mTrainRepo.searchInTrains(query);
    }
}
