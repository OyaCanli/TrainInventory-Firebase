package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryfirebase.firebaselivedata.BrandListLiveData;
import com.canli.oya.traininventoryfirebase.firebaselivedata.CategoryListLiveData;
import com.canli.oya.traininventoryfirebase.firebaselivedata.TrainListLiveData;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.repositories.BrandRepository;
import com.canli.oya.traininventoryfirebase.repositories.CategoryRepository;
import com.canli.oya.traininventoryfirebase.repositories.TrainRepository;

import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private TrainRepository mTrainRepo;
    private BrandRepository mBrandRepo;
    private CategoryRepository mCategoryRepo;
    private final MutableLiveData<Brand> mChosenBrand = new MutableLiveData<>();

    public MainViewModel() {
        mTrainRepo = TrainRepository.getInstance();
    }

    /////////// TRAIN LIST /////////////

    public TrainListLiveData getTrainList() {
        return mTrainRepo.getAllMinimalTrains();
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

    public void deleteUnusedImage(String imageUrlToDelete){
        mTrainRepo.deleteImage(imageUrlToDelete);
    }

    public void deleteTrain(Train train){
        mTrainRepo.deleteTrain(train);
    }

    ////////////// BRAND LIST //////////////////

    public void initializeBrandRepo(BrandRepository.BrandUseListener listener){
        mBrandRepo = BrandRepository.getInstance();
        mBrandRepo.setListener(listener);
    }

    public BrandListLiveData getBrandList() {
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
        mBrandRepo.insertBrand(brand);
    }

    public void updateBrandImageUrl(Brand brandToUpdate){
        mBrandRepo.updateBrandImageUrl(brandToUpdate);
    }

    public void isThisBrandUsed(String brandName){
        mBrandRepo.checkIfThisBrandUsed(brandName);
    }

    //////////////// CATEGORY LIST //////////////////

    public void initializeCategoryRepo(CategoryRepository.CategoryUseListener listener){
        mCategoryRepo = CategoryRepository.getInstance();
        mCategoryRepo.setListener(listener);
    }

    public CategoryListLiveData getCategoryList() {
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

    public LiveData<Map<String, String>> loadSearchLookUp(){
        return mTrainRepo.loadSearchLookUp();
    }
}
