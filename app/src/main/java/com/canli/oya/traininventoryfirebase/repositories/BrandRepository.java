package com.canli.oya.traininventoryfirebase.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.firebaselivedata.BrandListLiveData;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BrandRepository {

    private static BrandRepository sInstance;
    private static final String TAG = "BrandRepository";
    private BrandListLiveData brandList;
    private boolean brandIsUsed;
    private BrandUseListener mCallback;

    private BrandRepository(BrandUseListener listener) {
        Log.d(TAG, "new instance of BrandRepository");
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

    public BrandListLiveData getBrandList() {
        if (brandList == null) {
            brandList = new BrandListLiveData(FirebaseUtils.getBrandsRef());
            brandList.attachListener();
        }
        return brandList;
    }

    public void insertBrand(final Brand brand){
        //photo upload
        FirebaseUtils.getBrandsRef().child(brand.getBrandName()).setValue(brand);
    }

    public void updateBrandImageUrl(Brand brand){
        FirebaseUtils.getBrandsRef().child(brand.getBrandName()).child("brandLogoUri").setValue(brand.getBrandLogoUri());
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

    public void setConfigurationChange(boolean configurationChange) {
        if (brandList != null) {
            brandList.setChangingConfigutations(configurationChange);
        }
    }

    public void removeListener() {
        if (brandList != null) {
            brandList.removeListener();
        }
    }

    public void attachListener() {
        if (brandList != null) {
            brandList.attachListener();
        }
    }

}
