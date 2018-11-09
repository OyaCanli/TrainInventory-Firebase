package com.canli.oya.traininventoryfirebase.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.GlideApp;

import java.util.List;

public class CustomSpinAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Brand> mBrandList;

    public CustomSpinAdapter(Context context, List<Brand> list) {
        mContext = context;
        mBrandList = list;
    }

    @Override
    public int getCount() {
        return mBrandList.size();
    }

    @Override
    public Brand getItem(int position) {
        return mBrandList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.spinner_item, parent, false);
        }

        Brand currentBrand = getItem(position);

        TextView brandName_tv = convertView.findViewById(R.id.spin_item_brand_name);
        ImageView logo_iv = convertView.findViewById(R.id.spin_item_logo);

        brandName_tv.setText(currentBrand.getBrandName());
        String imageUri = currentBrand.getBrandLogoUri();
        if(imageUri == null){
            logo_iv.setVisibility(View.GONE);
        } else {
            logo_iv.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(imageUri)
                    .centerCrop()
                    .into(logo_iv);
        }

        return convertView;
    }
}
