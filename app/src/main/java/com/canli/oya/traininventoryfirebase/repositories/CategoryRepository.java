package com.canli.oya.traininventoryfirebase.repositories;

import android.support.annotation.NonNull;

import com.canli.oya.traininventoryfirebase.firebaselivedata.CategoryListLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private final CategoryListLiveData categoryList;
    private boolean categoryIsUsed;
    private CategoryUseListener mCallback;

    private CategoryRepository() {
        Timber.d("new instance of CategoryRepository");
        categoryList = new CategoryListLiveData(FirebaseUtils.getCategoriesRef());
    }

    public static CategoryRepository getInstance() {
        if (sInstance == null) {
            synchronized (CategoryRepository.class) {
                sInstance = new CategoryRepository();
            }
        }
        return sInstance;
    }

    public void setListener(CategoryUseListener mCallback) {
        this.mCallback = mCallback;
    }

    public CategoryListLiveData getCategoryList() {
        return categoryList;
    }

    public void insertCategory(final String category) {
        FirebaseUtils.getCategoriesRef().child(category).setValue(category);
    }

    public void deleteCategory(final String category) {
        FirebaseUtils.getCategoriesRef().child(category).removeValue();
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
