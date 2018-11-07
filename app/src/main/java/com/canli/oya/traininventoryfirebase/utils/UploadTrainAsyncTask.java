package com.canli.oya.traininventoryfirebase.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventoryfirebase.data.model.MinimalTrain;
import com.canli.oya.traininventoryfirebase.data.model.Train;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UploadTrainAsyncTask extends AsyncTask<Train, Void, Void> {

    private static final String TAG = "UploadImageAsynctask";

    @Override
    protected Void doInBackground(Train... trains) {
        final Train train = trains[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(train.getImageUri());
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] bytes = stream.toByteArray();

        final StorageReference photoRef = FirebaseUtils.getUserPhotosRef();

        photoRef.putBytes(bytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    train.setImageUri(downloadUri.toString());
                    //Push the full train object
                    DatabaseReference TRAINS_REF = FirebaseUtils.getFullTrainsRef();
                    String trainKey = TRAINS_REF.push().getKey();
                    TRAINS_REF.child(trainKey).setValue(train);
                    //Push the minimal train object in multiple locations
                    MinimalTrain minimalTrain = FirebaseUtils.getMinimalVersion(train);
                    Map<String, Object> trainValues = minimalTrain.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(FirebaseUtils.getMinimalTrainsPath(trainKey), trainValues);
                    childUpdates.put(FirebaseUtils.getTrainsInCategoriesPath(minimalTrain.getCategoryName(), trainKey), trainValues);
                    childUpdates.put(FirebaseUtils.getTrainsInBrandsPath(minimalTrain.getBrandName(), trainKey), trainValues);
                    FirebaseUtils.getDatabaseUserRef().updateChildren(childUpdates);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "photo is not successfully uploaded");
            }
        });
        return null;
    }

}
