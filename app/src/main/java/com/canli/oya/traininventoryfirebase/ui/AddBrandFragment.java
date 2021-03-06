package com.canli.oya.traininventoryfirebase.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.databinding.FragmentAddBrandBinding;
import com.canli.oya.traininventoryfirebase.model.Brand;
import com.canli.oya.traininventoryfirebase.utils.BitmapUtils;
import com.canli.oya.traininventoryfirebase.utils.Constants;
import com.canli.oya.traininventoryfirebase.utils.UploadImageAsyncTask;
import com.canli.oya.traininventoryfirebase.viewmodel.MainViewModel;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class AddBrandFragment extends Fragment implements View.OnClickListener, UploadImageAsyncTask.ImageUploadListener {

    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private Uri mLogoUri;
    private int mUsersChoice;
    private boolean isEdit;
    private Context mContext;
    private FragmentAddBrandBinding binding;
    private MainViewModel mViewModel;
    private boolean imageClicked;
    private Brand mBrandToUpdate;
    private Brand mChosenBrand;

    private final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
            mUsersChoice = item;
        }
    };

    public AddBrandFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_brand, container, false);

        //Set click listeners
        binding.addBrandSaveBtn.setOnClickListener(this);
        binding.addBrandImage.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)) { //This is the "edit" case
            isEdit = true;
            mViewModel.getChosenBrand().observe(AddBrandFragment.this, new Observer<Brand>() {
                @Override
                public void onChanged(@Nullable Brand brand) {
                    populateFields(brand);
                    mChosenBrand = brand;
                }
            });
        }

        if (!isEdit) {
            //Request focus on the first edit text
            binding.addBrandEditBrandName.requestFocus();
        } else {
            //Request focus on the web edit text(brand name edittext will be disabled in edit mode)
            binding.addBrandEditWeb.requestFocus();
        }

    }

    private void populateFields(Brand brand) {
        binding.setChosenBrand(brand);
        binding.setIsEdit(true);
    }

    @Override
    public void onClick(View v) {
        //If save is clicked
        if (v.getId() == R.id.addBrand_saveBtn) {
            saveBrand();
        } else {
            //If add photo is clicked
            imageClicked = true;
            openImageDialog();
        }
    }

    private void saveBrand() {
        //Get brand name from edit text
        String brandName = binding.addBrandEditBrandName.getText().toString().trim();

        //Get web address from edit text
        String webAddress = binding.addBrandEditWeb.getText().toString().trim();

        //If there is a uri for logo image, parse it to string
        if (imageClicked && mLogoUri != null) {
            UploadImageAsyncTask uploadImageTask = new UploadImageAsyncTask(this, mLogoUri, Constants.BRAND_IMAGE);
            uploadImageTask.execute(getActivity());
        }

        if (isEdit) {
            //Construct a new Brand object from this data with ID included
            mBrandToUpdate = new Brand(brandName, mChosenBrand.getBrandLogoUri(), webAddress);
            mViewModel.updateBrand(mBrandToUpdate);

        } else {
            //Construct a new Brand object from this data (without ID)
            mBrandToUpdate = new Brand(brandName, null, webAddress);
            //Insert to database in a background thread
            mViewModel.insertBrand(mBrandToUpdate);
        }

        Toast.makeText(getActivity(), R.string.brand_Saved, Toast.LENGTH_SHORT).show();

        //Remove fragment
        Fragment parentFrag = getParentFragment();
        Fragment currentInstance;
        if (parentFrag instanceof AddTrainFragment) {
            currentInstance = getFragmentManager().findFragmentById(R.id.childFragContainer);
        } else {
            currentInstance = getFragmentManager().findFragmentById(R.id.brandlist_addFrag_container);
        }

        //
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            focusedView.clearFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(currentInstance)
                .commit();

    }

    private void openImageDialog() {

        //Opens a dialog which lets the user choose either adding a photo from gallery or taking a new picture.
        String[] dialogOptions = getActivity().getResources().getStringArray(R.array.dialog_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image_from);
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mUsersChoice) {
                    case 0: {
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // If you do not have permission, request it
                            AddBrandFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constants.REQUEST_STORAGE_PERMISSION);
                        } else {
                            // Launch the camera if the permission exists
                            openCamera();
                        }
                        break;
                    }
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        pickImageDialog = builder.create();
        pickImageDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), Constants.PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(getActivity());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                mLogoUri = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLogoUri);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickImageDialog.dismiss();
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Glide.with(mContext)
                        .load(mLogoUri)
                        .into(binding.addBrandImage);
            } else {
                BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
            }
        } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mLogoUri = data.getData();
                Glide.with(mContext)
                        .load(mLogoUri)
                        .into(binding.addBrandImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case Constants.REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    /*This is for solving the weird behaviour of child fragments during exit.
    I found this solution from this SO entry and adapted to my case:
    https://stackoverflow.com/questions/14900738/nested-fragments-disappear-during-transition-animation*/
    private static final Animation dummyAnimation = new AlphaAnimation(1,1);
    static{
        dummyAnimation.setDuration(500);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(!enter && getParentFragment() instanceof BrandListFragment){
            return dummyAnimation;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onImageUploaded(Uri imageUri, boolean loadingSuccessful) {
        Timber.d("onImageUploaded called");
        if(loadingSuccessful){
            if(isEdit) {
                String previousUrl = mBrandToUpdate.getBrandLogoUri();
                mBrandToUpdate.setBrandLogoUri(imageUri.toString());
                mViewModel.updateBrandImageUrl(mBrandToUpdate);
                mViewModel.deleteUnusedImage(previousUrl);
            } else {
                mBrandToUpdate.setBrandLogoUri(imageUri.toString());
                mViewModel.updateBrandImageUrl(mBrandToUpdate);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_during_image_loading), Toast.LENGTH_SHORT).show();
        }
    }
}
