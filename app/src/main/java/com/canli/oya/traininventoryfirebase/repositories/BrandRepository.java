package com.canli.oya.traininventoryfirebase.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BrandRepository {

    private static BrandRepository sInstance;
    private static final String TAG = "BrandRepository";
    private final LiveData<List<Brand>> brandList;
    private boolean brandIsUsed;
    private BrandUseListener mCallback;

    private BrandRepository(BrandUseListener listener) {
        Log.d(TAG, "new instance of BrandRepository");
        FirebaseQueryLiveData brandSnapshot = new FirebaseQueryLiveData(FirebaseUtils.getBrandsRef());
        brandList = Transformations.map(brandSnapshot, new Deserializer());
        mCallback = listener;
    }

    public static BrandRepository getInstance(BrandUseListener listener){
        if (sInstance == null) {
            synchronized (BrandRepository.class) {
                sInstance = new BrandRepository(listener);
            }
        }
        return sInstance;
    }

    private class Deserializer implements Function<DataSnapshot, List<Brand>> {
        @Override
        public List<Brand> apply(DataSnapshot dataSnapshot) {
            List<Brand> brands = new ArrayList<>();
            for(DataSnapshot data : dataSnapshot.getChildren()){
                brands.add(data.getValue(Brand.class));
            }
            return brands;
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

    public void deleteBrand(String brandName){
        FirebaseUtils.getBrandsRef().child(brandName).removeValue();
    }

    public void checkIfThisBrandUsed(String brandName){
        //Check whether this brand is used by some trains
        FirebaseUtils.getTrainsInBrandsRef().child(brandName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                brandIsUsed = dataSnapshot.getValue() != null;
                mCallback.onBrandUseCaseReturned(brandIsUsed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public interface BrandUseListener{
        void onBrandUseCaseReturned(boolean isBrandUsed);
    }

}
