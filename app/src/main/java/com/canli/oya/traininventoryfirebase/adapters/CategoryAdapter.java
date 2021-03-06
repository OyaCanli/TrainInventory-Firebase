package com.canli.oya.traininventoryfirebase.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.canli.oya.traininventoryfirebase.BR;
import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.databinding.CategoryItemBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private List<String> mCategoryList;
    private final CategoryItemClickListener mClickListener;

    public CategoryAdapter(CategoryItemClickListener listener) {
        mClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item,
                        parent, false);
        binding.setCategoryItemClick(mClickListener);
        return new CategoryHolder(binding);
    }

    public void setCategories(List<String> newList) {
        mCategoryList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        String currentCategory = mCategoryList.get(position);
        holder.binding.setVariable(BR.categoryName, currentCategory);
        holder.binding.setCategoryNumber(position + 1);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mCategoryList == null ? 0 : mCategoryList.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        final CategoryItemBinding binding;

        CategoryHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface CategoryItemClickListener {
        void onCategoryItemClicked(String categoryName);
    }
}
