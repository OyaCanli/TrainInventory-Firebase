package com.canli.oya.traininventoryfirebase.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.model.Train;
import com.canli.oya.traininventoryfirebase.databinding.FragmentTrainDetailsBinding;
import com.canli.oya.traininventoryfirebase.utils.Constants;
import com.canli.oya.traininventoryfirebase.utils.InjectorUtils;
import com.canli.oya.traininventoryfirebase.viewmodel.ChosenTrainFactory;
import com.canli.oya.traininventoryfirebase.viewmodel.ChosenTrainViewModel;
import com.canli.oya.traininventoryfirebase.viewmodel.MainViewModel;

public class TrainDetailsFragment extends Fragment {

    private FragmentTrainDetailsBinding binding;
    private Train mChosenTrain;
    private String mTrainId;
    private MainViewModel mainViewModel;

    public TrainDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_details, container, false);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTrainId = bundle.getString(Constants.TRAIN_ID);
        }
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        ChosenTrainFactory factory = InjectorUtils.provideChosenTrainFactory(mTrainId);
        ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
        viewModel.getChosenTrain().observe(this, new Observer<Train>() {
            @Override
            public void onChanged(@Nullable Train train) {
                if (train != null) {
                    populateUI(train);
                    mChosenTrain = train;
                }
            }
        });
    }

    private void populateUI(Train chosenTrain) {
        getActivity().setTitle(chosenTrain.getTrainName());
        binding.setChosenTrain(chosenTrain);
        binding.executePendingBindings();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_train_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                openAlertDialogForDelete();
                break;
            }
            case R.id.action_edit: {
                AddTrainFragment addTrainFrag = new AddTrainFragment();
                Bundle args = new Bundle();
                args.putString(Constants.TRAIN_ID, mTrainId);
                addTrainFrag.setArguments(args);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage(R.string.do_you_want_to_delete);
        builder.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTrain();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }

    private void deleteTrain() {
        mainViewModel.deleteTrain(mChosenTrain);
        getFragmentManager().popBackStack();
    }

}
