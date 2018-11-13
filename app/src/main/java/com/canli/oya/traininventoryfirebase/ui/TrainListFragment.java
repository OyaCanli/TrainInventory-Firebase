package com.canli.oya.traininventoryfirebase.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.adapters.TrainAdapter;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.databinding.FragmentTrainListBinding;
import com.canli.oya.traininventoryfirebase.utils.AppExecutors;
import com.canli.oya.traininventoryfirebase.utils.Constants;
import com.canli.oya.traininventoryfirebase.viewmodel.MainViewModel;

import java.util.List;

public class TrainListFragment extends Fragment implements TrainAdapter.TrainItemClickListener {

    private TrainAdapter mAdapter;
    private List<MinimalTrain> mTrainList;
    private List<MinimalTrain> filteredTrains;
    private FragmentTrainListBinding binding;
    private MainViewModel mViewModel;

    public TrainListFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_list, container, false);
        setHasOptionsMenu(true);

        //Set recycler view
        mAdapter = new TrainAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.list.setItemAnimator(new DefaultItemAnimator());
        binding.list.setAdapter(mAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        Bundle bundle = getArguments();
        //If the list will be used for showing selected trains
        if (bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)) {
            String requestType = bundle.getString(Constants.INTENT_REQUEST_CODE);
            switch(requestType){
                case Constants.TRAINS_OF_BRAND: {
                    String brandName = bundle.getString(Constants.BRAND_NAME);
                    getActivity().setTitle(getString(R.string.trains_of_the_brand, brandName));
                    mViewModel.getTrainsFromThisBrand(brandName).observe(TrainListFragment.this, new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries == null || trainEntries.isEmpty()) {
                                binding.setIsEmpty(true);
                                binding.setEmptyMessage(getString(R.string.no_train_for_this_brand));
                            } else {
                                binding.setIsEmpty(false);
                                mAdapter.setTrains(trainEntries);
                                mTrainList = trainEntries;
                            }
                        }
                    });
                    break;
                }
                case Constants.TRAINS_OF_CATEGORY:{
                    String categoryName = bundle.getString(Constants.CATEGORY_NAME);
                    getActivity().setTitle(getString(R.string.all_from_this_Category, categoryName));
                    mViewModel.getTrainsFromThisCategory(categoryName).observe(TrainListFragment.this, new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries == null || trainEntries.isEmpty()) {
                                binding.setIsEmpty(true);
                                binding.setEmptyMessage(getString(R.string.no_items_for_this_category));
                            } else {
                                binding.setIsEmpty(false);
                                mAdapter.setTrains(trainEntries);
                                mTrainList = trainEntries;
                            }
                        }
                    });
                    break;
                }
                default: {
                    //If the list is going to be use for showing all trains, which is the default behaviour
                    getActivity().setTitle(getString(R.string.all_trains));
                    mViewModel.getTrainList().observe(TrainListFragment.this, new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries == null || trainEntries.isEmpty()) {
                                binding.setIsEmpty(true);
                                binding.setEmptyMessage(getString(R.string.no_trains_found));
                            } else {
                                binding.setIsEmpty(false);
                                mAdapter.setTrains(trainEntries);
                                mTrainList = trainEntries;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onListItemClick(String trainId) {
        TrainDetailsFragment trainDetailsFrag = new TrainDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.TRAIN_ID, trainId);
        trainDetailsFrag.setArguments(args);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, trainDetailsFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_and_add, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //added filter to list (dynamic change input text)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterTrains(query);
                return false;
            }
        });
    }

    private void filterTrains(final String query) {
        if (query == null || "".equals(query)) {
            filteredTrains = mTrainList;
            mAdapter.setTrains(filteredTrains);
            mAdapter.notifyDataSetChanged();
        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    filteredTrains = mViewModel.searchInTrains(query);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setTrains(filteredTrains);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            openAddTrainFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddTrainFragment() {
        AddTrainFragment addTrainFragment = new AddTrainFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, addTrainFragment)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit();
    }

    public void scrollToTop() {
        binding.list.smoothScrollToPosition(0);
    }

}
