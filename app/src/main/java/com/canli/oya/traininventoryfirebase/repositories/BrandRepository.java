package com.canli.oya.traininventoryfirebase.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrandRepository {

    private static BrandRepository sInstance;
    private static final String TAG = "BrandRepository";
    private final LiveData<List<Brand>> brandList;

    private BrandRepository() {
        Log.d(TAG, "new instance of BrandRepository");
        FirebaseQueryLiveData brandSnapshot = new FirebaseQueryLiveData(FirebaseUtils.getBrandsRef());
        brandList = Transformations.map(brandSnapshot, new Deserializer());
    }

    public static BrandRepository getInstance(){
        if (sInstance == null) {
            synchronized (BrandRepository.class) {
                sInstance = new BrandRepository();
            }
        }
        return sInstance;
    }

    private class Deserializer implements Function<DataSnapshot, List<Brand>> {
        @Override
        public List<Brand> apply(DataSnapshot dataSnapshot) {
            List<Brand> categories = new ArrayList<>();
            for(DataSnapshot data : dataSnapshot.getChildren()){
                categories.add(data.getValue(Brand.class));
            }
            return categories;
        }
    }

    public LiveData<List<Brand>> getBrandList() {
        return brandList;
    }

    public void insertBrand(final Brand brand){
        //photo upload
        FirebaseUtils.getBrandsRef().child(brand.getBrandName()).setValue(brand);
    }

    public void updateBrand(final Brand brand){

    }

    public void deleteBrand(final Brand brand){

    }

    public boolean isThisBrandUsed(final String brandName){
        return false;
    }
}
