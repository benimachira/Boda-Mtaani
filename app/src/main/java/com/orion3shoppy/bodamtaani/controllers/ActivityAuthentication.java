package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.orion3shoppy.bodamtaani.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.sanitizePhoneNumber;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_account_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_email_adress;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_firebase_service_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_photo_url;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_phone;


public class ActivityAuthentication extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ActivityAuthentication";
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGN_IN_PHONE = 2;
    private static final int REQUEST_CHECK_SETTINGS =3;
    String name, email;
    String idToken;


    private static final String KEY_UID = "user_id";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users_ref = db.collection(COL_USERS);
    private FirebaseAuth firebaseAuth;
    String UID;
    FirebaseUser firebaseUser_x;
    private FirebaseAuth.AuthStateListener authStateListener;
    String local_UID = "";
    Context context;

    DialogController dialogC;


    String phone_number = "";
    String photo_url = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        context = this;
        dialogC = new DialogController(context);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_x = firebaseAuth.getCurrentUser();

        UID = GetFirebaseUserID();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (!TextUtils.isEmpty(UID)) {
                    UID = firebaseAuth.getUid();
                }


            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogC.dialog_show("Initiating Logging in...");

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });


    }

    private void select_user() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_x = firebaseAuth.getCurrentUser();
        if (firebaseUser_x != null) {
            UID = firebaseUser_x.getUid();
        } else {
            UID = "";
        }

        users_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("Errorsss", "Listen failed.", e);

                    return;
                }

                if (!documentSnapshot.exists()) {


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {

                        final String user_name = string_null_treatment(user.getDisplayName());
                        final String email_adress = string_null_treatment(user.getEmail());
                        String phone_no = string_null_treatment(user.getPhoneNumber());
                        final String user_id = string_null_treatment(user.getUid());
                        final Uri uri = user.getPhotoUrl();
                        phone_no = sanitizePhoneNumber(phone_no);


                        if (uri != null) {
                            photo_url = string_null_treatment(uri.toString());
                        } else {
                            photo_url = "";
                        }


                        Map<String, Object> note_pref = new HashMap<>();
                        note_pref.put(USERS_user_id, UID);
                        note_pref.put(USERS_user_phone, phone_no);
                        note_pref.put(USERS_email_adress, email_adress);
                        note_pref.put(USERS_user_name, user_name);
                        note_pref.put(USERS_photo_url, photo_url);


                        users_ref.document(UID).set(note_pref, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityAuthentication.this, ActivityHomePage.class);
                                startActivity(intent);
                                finish();
                            }
                        });


                    }


                } else {

                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityAuthentication.this, ActivityHomePage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public String string_null_treatment(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            return s;
        }
    }


    public void login_with_phone(View view) {

        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()))
                .setLogo(R.mipmap.ic_launcher)
                .build();

        startActivityForResult(intent, RC_SIGN_IN_PHONE);
//        show_bottom_sheets();

//        login_with_phone_number(phone_number);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {


            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);

            } else {
                dialogC.dialog_dismiss();
                Toast.makeText(getBaseContext(), "Authentication Failed, try again", Toast.LENGTH_LONG).show();
            }


        }

        if (requestCode == RC_SIGN_IN_PHONE) {

            if (resultCode == RESULT_OK) {

                select_user();

                Log.d("ewwwwwwwwwwww","ddddddddddd "+RESULT_OK);
            } else {

                dialogC.dialog_dismiss();
                Toast.makeText(getBaseContext(), "Phone Authentication Failed, try again", Toast.LENGTH_LONG).show();
            }

        }



    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Google sign in
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        } else {
            dialogC.dialog_dismiss();
            Toast.makeText(this, "Login Unsuccessful" + result, Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            select_user();
                        } else {
                            dialogC.dialog_dismiss();
                            Toast.makeText(context, "Login Unsuccessful" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
//        if (authStateListener != null){
//            FirebaseAuth.getInstance().signOut();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }




}
