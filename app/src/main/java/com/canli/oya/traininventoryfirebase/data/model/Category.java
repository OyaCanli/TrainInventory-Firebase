package com.canli.oya.traininventoryfirebase.data.model;

import android.support.annotation.NonNull;

public class Category {

    private String categoryName;

    public Category() {
    }

    public Category(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }
}
