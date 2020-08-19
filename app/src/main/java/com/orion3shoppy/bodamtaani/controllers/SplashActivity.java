package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orion3shoppy.bodamtaani.R;

import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;


public class SplashActivity extends AppCompatActivity {
    Context context;
    Handler handler;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users_ref = db.collection(COL_USERS);
    String UID;
    FirebaseUser firebaseUser_x;
    boolean user_exists =false;
    LinearLayout linear_none;
    ProgressBar progress_loading;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_splash_screen);
        context = this;
        linear_none= (LinearLayout) findViewById(R.id.linear_none);
        progress_loading= (ProgressBar) findViewById(R.id.progress_loading);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_x = firebaseAuth.getCurrentUser();


        if (firebaseUser_x != null) {
            UID = firebaseUser_x.getUid();
        }

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (!TextUtils.isEmpty(UID)) {
                    UID= firebaseAuth.getUid();
                }


            }
        };

        user_selection ();

    }


    private void user_selection (){

//        DialogController dialogController =new DialogController(context);
//        dialogController.dialog_show("Authenticating.. ");
        progress_loading.setVisibility(View.VISIBLE);
        Log.d("dddddddd","ddddddddd"+UID);

        if(TextUtils.isEmpty(UID)){
//
//            handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    navigate_to_acitivity(2);
//                }
//            }, 3000);

            return;
        }

        users_ref.document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        navigate_to_acitivity(1);
                    } else {


                        navigate_to_acitivity(2);
                    }
                } else {
                    Toast.makeText(context,"Check your internet connection and proceeded",Toast.LENGTH_SHORT).show();
                    linear_none.setVisibility(View.VISIBLE);
                    progress_loading.setVisibility(View.GONE);
                }
            }
        });

    }

    public void navigate_to_acitivity (int is_there){
        if(is_there ==1){

            Intent intent = new Intent(SplashActivity.this, ActivityHomePage.class);
            startActivity(intent);
            finish();

            progress_loading.setVisibility(View.GONE);

        }else if(is_there ==2) {

            Intent intent = new Intent(SplashActivity.this, ActivityAuthentication.class);
            startActivity(intent);
            finish();

            progress_loading.setVisibility(View.GONE);
        }



    }

}
