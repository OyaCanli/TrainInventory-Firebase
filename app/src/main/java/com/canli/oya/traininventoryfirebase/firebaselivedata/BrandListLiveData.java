package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.model.Brand;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class BrandListLiveData extends LiveData<List<Brand>> {
    private static final String LOG_TAG = "BrandListLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<Brand> brandList;
    private boolean isChangingConfigutations;

    public BrandListLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    public void removeListener() {
        if (!isChangingConfigutations) {
            query.removeEventListener(listener);
            Log.d(LOG_TAG, "listener removed");
            if (brandList != null) brandList.clear();
        }
    }

    public void attachListener() {
        if (!isChangingConfigutations) {
            query.addChildEventListener(listener);
            Log.d(LOG_TAG, "listener attached");
        } else {
            isChangingConfigutations = false;
        }
    }

    public void setChangingConfigutations(boolean changingConfigutations) {
        isChangingConfigutations = changingConfigutations;
    }


    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (brandList == null) brandList = new ArrayList<>();
            brandList.add(dataSnapshot.getValue(Brand.class));
            setValue(brandList);
            Log.d(LOG_TAG, "onChildAdded. list size: " + brandList.size());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String brandID = dataSnapshot.getKey();
            int listSize = brandList.size();
            for(int i = 0 ; i < listSize ; i++){
                if(brandID.equals(brandList.get(i).getBrandName())){
                    brandList.set(i, dataSnapshot.getValue(Brand.class));
                    setValue(brandList);
                }
            }
            Log.d(LOG_TAG, "onChildChanged. list size: " + listSize);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String brandId = dataSnapshot.getKey();
            for(Brand brand : brandList){
                if(brandId.equals(brand.getBrandName())){
                    brandList.remove(brand);
                }
            }
            Log.d(LOG_TAG, "onChildRemoved. list size: " + brandList.size());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }

}
