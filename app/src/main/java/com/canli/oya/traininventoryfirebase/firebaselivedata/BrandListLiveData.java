package com.canli.oya.traininventoryfirebase.firebaselivedata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.canli.oya.traininventoryfirebase.model.Brand;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BrandListLiveData extends FirebaseBaseLiveData<List<Brand>> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private List<Brand> brandList;

    public BrandListLiveData(DatabaseReference ref) {
        this.query = ref;
        Timber.d("new instance created");
    }

    public void removeListener() {
        query.removeEventListener(listener);
        Timber.d("listener removed");
        if (brandList != null) brandList.clear();

    }

    public void attachListener() {
        query.addChildEventListener(listener);
        Timber.d("listener attached");
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (brandList == null) brandList = new ArrayList<>();
            brandList.add(dataSnapshot.getValue(Brand.class));
            setValue(brandList);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String brandID = dataSnapshot.getKey();
            int listSize = brandList.size();
            for (int i = 0; i < listSize; i++) {
                if (brandID.equals(brandList.get(i).getBrandName())) {
                    brandList.set(i, dataSnapshot.getValue(Brand.class));
                    setValue(brandList);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String brandId = dataSnapshot.getKey();
            for (Brand brand : brandList) {
                if (brandId.equals(brand.getBrandName())) {
                    brandList.remove(brand);
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }

}
