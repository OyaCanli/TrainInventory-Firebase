package com.canli.oya.traininventoryfirebase.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryfirebase.data.model.Brand;
import com.canli.oya.traininventoryfirebase.data.model.Category;
import com.canli.oya.traininventoryfirebase.data.model.Train;
import com.canli.oya.traininventoryfirebase.data.repositories.BrandRepository;
import com.canli.oya.traininventoryfirebase.data.repositories.CategoryRepository;
import com.canli.oya.traininventoryfirebase.data.repositories.TrainRepository;
import com.canli.oya.traininventoryfirebase.data.model.MinimalTrain;

import java.util.List;

public class MainViewModel extends ViewModel {

    private List<MinimalTrain> mTrainList;
    private List<Brand> mBrandList;
    private List<String> mCategoryList;
    private TrainRepository mTrainRepo;
    private BrandRepository mBrandRepo;
    private CategoryRepository mCategoryRepo;
    private final MutableLiveData<Brand> mChosenBrand = new MutableLiveData<>();

    /////////// TRAIN LIST /////////////
    public void loadTrainList(TrainRepository trainRepo){
        if(mTrainList == null){
            mTrainList = trainRepo.getTrainList();
        }
        mTrainRepo = trainRepo;
    }

    public List<MinimalTrain> getTrainList() {
        return mTrainList;
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

    public void loadBrandList(BrandRepository brandRepo){
        if(mBrandList == null){
            mBrandList = brandRepo.getBrandList();
        }
        mBrandRepo = brandRepo;
    }

    public List<Brand> getBrandList() {
        return mBrandList;
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

    public void loadCategoryList(CategoryRepository categoryRepo){
        if(mCategoryList == null){
            mCategoryList = categoryRepo.getCategoryList();
        }
        mCategoryRepo = categoryRepo;
    }

    public List<String> getCategoryList() {
        return mCategoryList;
    }

    public void deleteCategory(Category category){
        mCategoryRepo.deleteCategory(category);
    }

    public void insertCategory(Category category){
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

    public List<Train> searchInTrains(String query){
        return mTrainRepo.searchInTrains(query);
    }
}
