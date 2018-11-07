package com.canli.oya.traininventoryfirebase.data.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.data.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class BrandRepository {

    private static BrandRepository sInstance;
    private List<Brand> brandList;
    private static final String TAG = "BrandRepository";

    private BrandRepository() {
        loadBrands();
    }

    public static BrandRepository getInstance(){
        if (sInstance == null) {
            synchronized (BrandRepository.class) {
                sInstance = new BrandRepository();
            }
        }
        return sInstance;
    }

    private void loadBrands(){
        Log.d(TAG, "loading brands");
        brandList = new ArrayList<>();
        FirebaseUtils.getBrandsRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                brandList.add(dataSnapshot.getValue(Brand.class));
                Log.d(TAG, "brand list size: " + brandList.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public List<Brand> getBrandList() {
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
