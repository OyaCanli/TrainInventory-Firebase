package com.canli.oya.traininventoryfirebase.adapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.utils.GlideApp;

public class BindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_gallery)
                .into(view);
    }
}
