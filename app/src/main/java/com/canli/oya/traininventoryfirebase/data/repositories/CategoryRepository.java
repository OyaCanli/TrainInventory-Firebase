package com.canli.oya.traininventoryfirebase.data.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.data.model.Category;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private List<String> categoryList;
    private static final String TAG = "CategoryRepository";

    private CategoryRepository() {
        loadCategories();
    }

    public static CategoryRepository getInstance() {
        if (sInstance == null) {
            synchronized (CategoryRepository.class) {
                sInstance = new CategoryRepository();
            }
        }
        return sInstance;
    }

    private void loadCategories() {
        Log.d(TAG, "loading categories");
        categoryList = new ArrayList<>();
        FirebaseUtils.getCategoriesRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                categoryList.add(dataSnapshot.getValue(Category.class).getCategoryName());
                Log.d(TAG, "category list size: " + categoryList.size());
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

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void insertCategory(final Category category) {
        FirebaseUtils.getCategoriesRef().child(category.getCategoryName()).setValue(category);
    }

    public void deleteCategory(final Category category) {

    }

    public boolean isThisCategoryUsed(String category) {
        return false;
    }
}
