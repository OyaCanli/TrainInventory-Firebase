package com.canli.oya.traininventoryfirebase.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.adapters.BrandAdapter;
import com.canli.oya.traininventoryfirebase.databinding.FragmentListBinding;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.repositories.BrandRepository;
import com.canli.oya.traininventoryfirebase.utils.Constants;
import com.canli.oya.traininventoryfirebase.viewmodel.MainViewModel;
import com.firebase.ui.auth.AuthUI;

import java.util.List;

import timber.log.Timber;

public class BrandListFragment extends Fragment implements BrandAdapter.BrandItemClickListener,
        BrandRepository.BrandUseListener {

    private List<Brand> brands;
    private BrandAdapter adapter;
    private MainViewModel mViewModel;
    private FragmentListBinding binding;
    private Brand brandToErase;
    private CoordinatorLayout coordinator;
    private final Handler handler = new Handler();
    private boolean mIsEmpty;
    private Runnable setEmpty = new Runnable() {
        @Override
        public void run() {
            Timber.d("Runnable setEmpty is executed");
            if (mIsEmpty) {
                binding.included.setIsEmpty(true);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_from_left);
                binding.included.emptyImage.startAnimation(animation);
            }
        }
    };

    public BrandListFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false);

        setHasOptionsMenu(true);

        adapter = new BrandAdapter(this);
        binding.included.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.included.list.setItemAnimator(new DefaultItemAnimator());
        binding.included.list.setAdapter(adapter);
        binding.included.setIsLoading(true);
        binding.included.setIsEmpty(false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mViewModel.initializeBrandRepo(this);
        mViewModel.getBrandList().observe(getActivity(), new Observer<List<Brand>>() {
            @Override
            public void onChanged(@Nullable List<Brand> brandEntries) {
                if (brandEntries != null) {
                    Timber.d("onChange is called,data is not null");
                    binding.included.setIsLoading(false);
                    if (brandEntries.isEmpty()) {
                        Timber.d("onChange is called, list is empty");
                        binding.included.setEmptyMessage(getString(R.string.no_brands_found));
                        setIsEmpty(true);
                        handler.postDelayed(setEmpty, 300);
                    } else {
                        setIsEmpty(false);
                        Timber.d("onChange is called, list is not empty");
                        binding.included.emptyImage.clearAnimation();
                        adapter.setBrands(brandEntries);
                        brands = brandEntries;
                        binding.included.setIsEmpty(false);
                    }
                } else {
                    Timber.d("onChange is called but data is null");
                }
            }
        });

        getActivity().setTitle(getString(R.string.all_brands));

        coordinator = getActivity().findViewById(R.id.coordinator);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                //First take a backup of the brand to erase
                brandToErase = brands.get(position);

                //Check whether this brand is used in trains table.
                mViewModel.isThisBrandUsed(brandToErase.getBrandName());
            }
        }).attachToRecyclerView(binding.included.list);
    }

    private void setIsEmpty(boolean isEmpty) {
        mIsEmpty = isEmpty;
    }

    private void openAddBrandFragment() {
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            openAddBrandFragment();
        } else if (itemId == R.id.sign_out) {
            AuthUI.getInstance().signOut(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBrandItemClicked(View view, Brand clickedBrand) {
        switch (view.getId()) {
            case R.id.brand_item_web_icon: {
                openWebSite(clickedBrand);
                break;
            }
            case R.id.brand_item_train_icon: {
                showTrainsFromThisBrand(clickedBrand);
                break;
            }
            case R.id.brand_item_edit_icon: {
                editBrand(clickedBrand);
                break;
            }
        }
    }

    private void editBrand(Brand clickedBrand) {
        mViewModel.setChosenBrand(clickedBrand);
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.EDIT_CASE);
        addBrandFrag.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }

    private void showTrainsFromThisBrand(Brand clickedBrand) {
        TrainListFragment trainListFrag = new TrainListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.TRAINS_OF_BRAND);
        args.putString(Constants.BRAND_NAME, clickedBrand.getBrandName());
        trainListFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainListFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit();
    }

    private void openWebSite(Brand clickedBrand) {
        String urlString = clickedBrand.getWebUrl();
        Uri webUri = null;
        if (!TextUtils.isEmpty(urlString)) {
            try {
                webUri = Uri.parse(urlString);
            } catch (Exception e) {
                Timber.e(e.toString());
            }
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(webUri);
            if (webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(webIntent);
            }
        }
    }

    @Override
    public void onBrandUseCaseReturned(boolean isBrandUsed) {
        if(isBrandUsed){
            //If brand is used, show a warning an do not delete the brand
            Toast.makeText(getActivity(), getString(R.string.warning_brand_used), Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        } else {
            //If it is not used, delete the brand
            deleteBrand();
        }
    }

    public void deleteBrand() {
        mViewModel.deleteBrand(brandToErase.getBrandName());

        //Show a snack bar for undoing delete
        Snackbar snackbar = Snackbar
                .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.insertBrand(brandToErase);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.window_background));
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}

