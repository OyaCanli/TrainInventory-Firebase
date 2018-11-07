package com.canli.oya.traininventoryfirebase.data.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.utils.FirebaseQueryLiveData;
import com.canli.oya.traininventoryfirebase.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private static final String TAG = "CategoryRepository";
    private final LiveData<List<String>> categoryList;

    private CategoryRepository() {
        Log.d(TAG, "new instance of CategoryRepository");
        FirebaseQueryLiveData categorySnapshot = new FirebaseQueryLiveData(FirebaseUtils.getCategoriesRef());
        categoryList = Transformations.map(categorySnapshot, new Deserializer());
    }

    public static CategoryRepository getInstance() {
        if (sInstance == null) {
            synchronized (CategoryRepository.class) {
                sInstance = new CategoryRepository();
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

    public boolean isThisCategoryUsed(String category) {
        return false;
    }
}
