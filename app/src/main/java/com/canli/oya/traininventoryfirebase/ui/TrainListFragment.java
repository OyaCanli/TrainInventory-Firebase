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
import android.widget.Toast;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.adapters.TrainAdapter;
import com.canli.oya.traininventoryfirebase.databinding.FragmentTrainListBinding;
import com.canli.oya.traininventoryfirebase.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.utils.Constants;
import com.canli.oya.traininventoryfirebase.viewmodel.MainViewModel;
import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class TrainListFragment extends Fragment implements TrainAdapter.TrainItemClickListener {

    private TrainAdapter mAdapter;
    private List<MinimalTrain> mTrainList;
    private FragmentTrainListBinding binding;
    private Map<String, String> mSearchLookUp;

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
        binding.setIsLoading(true);
        binding.setIsEmpty(false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainViewModel mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        Bundle bundle = getArguments();
        //If the list will be used for showing selected trains
        if (bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)) {
            String requestType = bundle.getString(Constants.INTENT_REQUEST_CODE);
            switch (requestType) {
                case Constants.TRAINS_OF_BRAND: {
                    String brandName = bundle.getString(Constants.BRAND_NAME);
                    getActivity().setTitle(getString(R.string.trains_of_the_brand, brandName));
                    mViewModel.getTrainsFromThisBrand(brandName).observe(getActivity(), new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries != null) {
                                Timber.d("onChange is called,data is not null");
                                binding.setIsLoading(false);
                                if (trainEntries.isEmpty()) {
                                    Timber.d("onChange is called, list is empty");
                                    binding.setIsEmpty(true);
                                    binding.setEmptyMessage(getString(R.string.no_train_for_this_brand));
                                } else {
                                    Timber.d("onChange is called, list is not empty");
                                    binding.setIsEmpty(false);
                                    mAdapter.setTrains(trainEntries);
                                    mTrainList = trainEntries;
                                }
                            } else {
                                Timber.d("onChange is called but data is null");
                            }
                        }
                    });
                    break;
                }
                case Constants.TRAINS_OF_CATEGORY: {
                    String categoryName = bundle.getString(Constants.CATEGORY_NAME);
                    getActivity().setTitle(getString(R.string.all_from_this_Category, categoryName));
                    mViewModel.getTrainsFromThisCategory(categoryName).observe(getActivity(), new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries != null) {
                                binding.setIsLoading(false);
                                if (trainEntries.isEmpty()) {
                                    binding.setIsEmpty(true);
                                    binding.setEmptyMessage(getString(R.string.no_items_for_this_category));
                                } else {
                                    binding.setIsEmpty(false);
                                    mAdapter.setTrains(trainEntries);
                                    mTrainList = trainEntries;
                                }
                            }
                        }
                    });
                    break;
                }
                default: {
                    //If the list is going to be use for showing all trains, which is the default behaviour
                    getActivity().setTitle(getString(R.string.all_trains));
                    mViewModel.getTrainList().observe(getActivity(), new Observer<List<MinimalTrain>>() {
                        @Override
                        public void onChanged(@Nullable List<MinimalTrain> trainEntries) {
                            if (trainEntries != null) {
                                binding.setIsLoading(false);
                                if (trainEntries.isEmpty()) {
                                    binding.setIsEmpty(true);
                                    binding.setEmptyMessage(getString(R.string.no_trains_found));
                                } else {
                                    binding.setIsEmpty(false);
                                    mAdapter.setTrains(trainEntries);
                                    mTrainList = trainEntries;
                                }
                            }
                        }
                    });
                }
            }
        }
        mViewModel.loadSearchLookUp().observe(getActivity(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(@Nullable Map<String, String> searchMap) {
                mSearchLookUp = searchMap;
            }
        });
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
        List<MinimalTrain> filteredTrains;
        if (query == null || "".equals(query)) {
            filteredTrains = mTrainList;
            mAdapter.setTrains(filteredTrains);
            mAdapter.notifyDataSetChanged();
        } else {
            filteredTrains = searchInTrains(query);
            mAdapter.setTrains(filteredTrains);
            mAdapter.notifyDataSetChanged();
            if (filteredTrains.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.no_results), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            openAddTrainFragment();
        } else if (itemId == R.id.sign_out) {
            AuthUI.getInstance().signOut(getActivity());
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

    public List<MinimalTrain> searchInTrains(final String query) {
        //Iterate through the searchLookUp map and save the trainIDs for the items which contain the query text
        ArrayList<String> resultIds = new ArrayList<>();
        for (Map.Entry<String, String> pair : mSearchLookUp.entrySet()) {
            if (pair.getValue().toLowerCase().contains(query.toLowerCase())) {
                resultIds.add(pair.getKey());
            }
        }
        //Once you have trainIds, get the corresponding minimal train versions
        List<MinimalTrain> filteredTrainList = new ArrayList<>();
        for (MinimalTrain minimalTrain : mTrainList) {
            if (resultIds.contains(minimalTrain.getTrainId())) {
                Timber.d("trainID" + minimalTrain.getTrainId());
                filteredTrainList.add(minimalTrain);
            }
        }
        return filteredTrainList;
    }

    public void scrollToTop() {
        binding.list.smoothScrollToPosition(0);
    }
}
