package com.example.harsimar.hospi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


import static android.content.ContentValues.TAG;


/**
 * Created by shivaraju on 7/4/18.
 */

public class Login_Activity extends AppCompatActivity{

    public static final String PREF_NAME = "DotSlash-Hospi";
    public static final String IS_LOGIN = "is_logged_in";
    int PRIVATE_MODE = 0;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN=1;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);

        mAuth = FirebaseAuth.getInstance();

        mPref = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        mEditor = mPref.edit();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {

                    startActivity(new Intent(Login_Activity.this, Login_Activity.class));
                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(Login_Activity.this, "You got an ERROR", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });
    }


    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //Toast.makeText(Login_Activity.this, "asdsfsdsdfsfsfsfsf", Toast.LENGTH_SHORT).show();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Toast.makeText(Login_Activity.this, "asdsfsdfsf", Toast.LENGTH_SHORT).show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


                mEditor.clear();
                mEditor.putString(IS_LOGIN, "true");
                mEditor.commit();
                Toast.makeText(Login_Activity.this, "Logged in", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ProfileActivity.class));
                finish();

            } else {
                Toast.makeText(Login_Activity.this, "not Logged in", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}

