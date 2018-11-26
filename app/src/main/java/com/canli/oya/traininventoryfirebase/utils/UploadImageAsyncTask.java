package com.canli.oya.traininventoryfirebase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.utils.firebaseutils.FirebaseUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UploadImageAsyncTask extends AsyncTask<Context, Void, Void> {

    public interface ImageUploadListener {
        void onImageUploaded(Uri imageUri, boolean loadingSuccessful);
    }

    private static final String TAG = "UploadImageAsynctask";
    private static final int IMAGE_MAX_DIMENSION = 640;
    private ImageUploadListener mCallback;
    private Uri mUri;
    private int mImageType;
    private StorageReference mPhotoRef;

    public UploadImageAsyncTask(ImageUploadListener listener, Uri uri, int imageType) {
        mCallback = listener;
        mUri = uri;
        mImageType = imageType;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        Context context = contexts[0];
        if (mUri == null) {
            return null;
        }
        Bitmap bitmap = null;
        //First resize the image
        try {
            bitmap = decodeSampledBitmapFromUri(mUri, context);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Can't find file to resize: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred during resize: " + e.getMessage());
        }

        //Then compress it
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] bytes = stream.toByteArray();

        //Get the correct storage reference according to image type
        if(mImageType == Constants.BRAND_IMAGE){
            mPhotoRef = FirebaseUtils.getBrandPhotosRef().child(mUri.getLastPathSegment());
        } else {
            mPhotoRef = FirebaseUtils.getTrainPhotosRef().child(mUri.getLastPathSegment());
        }

        //Finally push to Firebase Storage
        mPhotoRef.putBytes(bytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return mPhotoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    mCallback.onImageUploaded(downloadUri, true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "photo is not successfully uploaded");
                mCallback.onImageUploaded(null, false);
            }
        });
        return null;
    }

    //I got this resizing code from the FriendlyPix sample app of Firebase: https://github.com/firebase/friendlypix-android
    private Bitmap decodeSampledBitmapFromUri(Uri fileUri, Context context)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        InputStream stream = streamFromUri(fileUri, context);
        stream.mark(stream.available());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        stream.reset();

        // Decode bitmap with inSampleSize set
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeStream(stream, null, options);
        stream.close();

        InputStream freshStream = streamFromUri(fileUri, context);
        return BitmapFactory.decodeStream(freshStream, null, options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > IMAGE_MAX_DIMENSION || width > IMAGE_MAX_DIMENSION) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > IMAGE_MAX_DIMENSION
                    && (halfWidth / inSampleSize) > IMAGE_MAX_DIMENSION) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private InputStream streamFromUri(Uri fileUri, Context context) throws FileNotFoundException {
        return new BufferedInputStream(
                context.getContentResolver().openInputStream(fileUri));
    }

}
