package com.canli.oya.traininventoryfirebase.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.databinding.BrandItemBinding;
import com.canli.oya.traininventoryfirebase.model.Brand;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder>{

    private List<Brand> mBrandList;
    private final BrandItemClickListener mClickListener;

    public BrandAdapter(BrandItemClickListener listener) {
        mClickListener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BrandItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.brand_item,
                        parent, false);
        binding.setBrandItemClick(mClickListener);
        return new BrandViewHolder(binding);
    }

    public void setBrands(List<Brand> newList){
        mBrandList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand currentBrand = mBrandList.get(position);
        holder.binding.setBrand(currentBrand);
        holder.binding.setBrandNumber(position + 1);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mBrandList == null ? 0 : mBrandList.size();
    }

    class BrandViewHolder extends RecyclerView.ViewHolder {

        final BrandItemBinding binding;

        BrandViewHolder(BrandItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface BrandItemClickListener{
        void onBrandItemClicked(View view, Brand clickedBrand);
    }
}
