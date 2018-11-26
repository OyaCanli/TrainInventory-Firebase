package com.canli.oya.traininventoryfirebase.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.firebaselivedata.CategoryListLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private static final String TAG = "CategoryRepository";
    private final CategoryListLiveData categoryList;
    private boolean categoryIsUsed;
    private CategoryUseListener mCallback;

    private CategoryRepository(CategoryUseListener listener) {
        Log.d(TAG, "new instance of CategoryRepository");
        categoryList = new CategoryListLiveData(FirebaseUtils.getCategoriesRef());
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

    public CategoryListLiveData getCategoryList() {
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
