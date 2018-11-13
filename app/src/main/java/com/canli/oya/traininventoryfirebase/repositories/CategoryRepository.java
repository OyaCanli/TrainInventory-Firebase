package com.canli.oya.traininventoryfirebase.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private static final String TAG = "CategoryRepository";
    private final LiveData<List<String>> categoryList;
    private boolean categoryIsUsed;
    private CategoryUseListener mCallback;

    private CategoryRepository(CategoryUseListener listener) {
        Log.d(TAG, "new instance of CategoryRepository");
        FirebaseQueryLiveData categorySnapshot = new FirebaseQueryLiveData(FirebaseUtils.getCategoriesRef());
        categoryList = Transformations.map(categorySnapshot, new Deserializer());
        mCallback = listener;
    }

    public static CategoryRepository getInstance(CategoryUseListener listener) {
        if (sInstance == null) {
            synchronized (CategoryRepository.class) {
                sInstance = new CategoryRepository(listener);
            }
        }
        return sInstance;
    }

    private class Deserializer implements Function<DataSnapshot, List<String>> {
        @Override
        public List<String> apply(DataSnapshot dataSnapshot) {
            List<String> categories = new ArrayList<>();
            for(DataSnapshot data : dataSnapshot.getChildren()){
                categories.add(data.getValue(String.class));
            }
            return categories;
        }
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public void insertCategory(final String category) {
        FirebaseUtils.getCategoriesRef().child(category).setValue(category);
    }

    public void deleteCategory(final String category) {

    }

    public void checkIfCategoryUsed(String category) {
        //Check whether this brand is used by some trains
        FirebaseUtils.getTrainsInCategoriesRef().child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryIsUsed = dataSnapshot.getValue() != null;
                mCallback.onCategoryUseCaseReturned(categoryIsUsed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public interface CategoryUseListener{
        void onCategoryUseCaseReturned(boolean isCategoryUsed);
    }
}
