package com.canli.oya.traininventoryfirebase.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.canli.oya.traininventoryfirebase.R;
import com.canli.oya.traininventoryfirebase.databinding.ActivitySplashBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "SplashActivity";
    private FirebaseAuth mFirebaseAuth;
    private ActivitySplashBinding binding;
    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "user null, starting sign in screen");
                //Start sign up activity
                startSignUpActivity();
            } else {
                Log.d(TAG, "user is not null, starting main activity");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_from_left);
        binding.splashLogo.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult is called");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "result ok");
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "result cancelled");
                Snackbar snackbar = Snackbar
                        .make(binding.getRoot(), "You need to sign-in in order to use the app", Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.sign_in, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startSignUpActivity();
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.window_background));
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();
            }
        }
    }

    private void startSignUpActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }
}
