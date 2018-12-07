package com.canli.oya.traininventoryfirebase.adapters;

import android.databinding.BindingAdapter;
import android.view.View;
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

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
