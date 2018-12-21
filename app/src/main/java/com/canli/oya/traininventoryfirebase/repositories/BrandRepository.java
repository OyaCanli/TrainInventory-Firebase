package com.canli.oya.traininventoryfirebase.repositories;

import android.support.annotation.NonNull;

import com.canli.oya.traininventoryfirebase.firebaselivedata.BrandListLiveData;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class BrandRepository {

    private static BrandRepository sInstance;
    private final BrandListLiveData brandList;
    private boolean brandIsUsed;
    private BrandUseListener mCallback;

    private BrandRepository() {
        Timber.d("new instance of BrandRepository");
        brandList = new BrandListLiveData(FirebaseUtils.getBrandsRef());
    }

    public static BrandRepository getInstance(){
        if (sInstance == null) {
            synchronized (BrandRepository.class) {
                sInstance = new BrandRepository();
            }
        }
        return sInstance;
    }

    public void setListener(BrandUseListener mCallback) {
        this.mCallback = mCallback;
    }

    public BrandListLiveData getBrandList() {
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

}
