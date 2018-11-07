package com.canli.oya.traininventoryfirebase.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.data.model.Train;
import com.canli.oya.traininventoryfirebase.databinding.TrainItemBinding;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder>{

    private List<Train> mTrainList;
    private final TrainItemClickListener mClickListener;

    public TrainAdapter(TrainItemClickListener listener) {
        mClickListener = listener;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TrainItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.train_item,
                        parent, false);
        binding.setTrainItemClick(mClickListener);
        return new TrainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        Train currentTrain = mTrainList.get(position);
        holder.binding.setTrain(currentTrain);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mTrainList == null ? 0 : mTrainList.size();
    }

    public void setTrains(List<Train> newList){
        mTrainList = newList;
        notifyDataSetChanged();
    }

    class TrainViewHolder extends RecyclerView.ViewHolder{

        final TrainItemBinding binding;

        TrainViewHolder(TrainItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface TrainItemClickListener {
        void onListItemClick();
    }
}
