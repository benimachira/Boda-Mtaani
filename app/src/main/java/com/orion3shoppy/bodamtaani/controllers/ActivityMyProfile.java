package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.orion3shoppy.bodamtaani.R;

import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.util.HashMap;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.fix_display_null_strings;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.fix_null_strings;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_is_online;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_photo_url;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_phone;


public class ActivityMyProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser_x;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users_ref = db.collection(COL_USERS);
    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);


    Context context;
    String UID = "";

    ImageView img_pic_min;


    TextView tv_name_profile, tv_phone_no, tv_phone_status, tv_phone_name_status, tv_name_edit, tv_phone_no_edit, tv_logout;

    DialogController dialogController;

    String user_id;
    String user_name;
    String user_phone;
    int account_status;
    int access_level;
    int account_type;
    int is_online;
    int is_merchant;
    String email_adress;
    String photo_url;
    String firebase_service_id;
    CardView card_boda_driver_account;

    TextView tv_boda_acc_state, tv_boda_balance, tv_total_trips;


    String driver_name;
    String driver_id_number;
    String driver_licence_number;
    String car_reg_no;
    int driver_status;
    String photo_of_passport;
    String photo_of_id;
    String photo_of_licence;
    double drivers_balance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_name_profile = (TextView) findViewById(R.id.tv_name_profile);
        tv_phone_no = (TextView) findViewById(R.id.tv_phone_no);
        tv_phone_status = (TextView) findViewById(R.id.tv_phone_status);
        tv_phone_name_status = (TextView) findViewById(R.id.tv_phone_name_status);
        tv_name_edit = (TextView) findViewById(R.id.tv_name_edit);
        tv_phone_no_edit = (TextView) findViewById(R.id.tv_phone_no_edit);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        img_pic_min = (ImageView) findViewById(R.id.img_pic_min);
        card_boda_driver_account = (CardView) findViewById(R.id.card_boda_driver_account);
        tv_boda_acc_state = (TextView) findViewById(R.id.tv_boda_acc_state);
        tv_boda_balance = (TextView) findViewById(R.id.tv_boda_balance);
        tv_total_trips = (TextView) findViewById(R.id.tv_total_trips);

        context = this;
        firebaseAuth = FirebaseAuth.getInstance();


        UID = GetFirebaseUserID();


        select_user();

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogController = new DialogController(context);
                dialogController.dialog_show("Signing out... ");

                Map<String, Object> note_pref1 = new HashMap<>();
                note_pref1.put(USERS_is_online, 0);

                users_ref.document(UID).set(note_pref1, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();


                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        if (getIntent().getBooleanExtra("EXIT", false)) {
                            finish();
                        }
                        dialogController.dialog_dismiss();
                        Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogController.dialog_dismiss();
                        Toast.makeText(context, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }

    public void add_documents(View view) {

        Intent intent = new Intent(context, ActivityMyDocuments.class);
        startActivity(intent);

    }

    public void withdraw_to_mpesa(View view) {

    }

    public void edit_phone(View view) {

        add_material_dialog(2);
    }

    public void edit_name(View view) {
        add_material_dialog(1);
    }

    public void add_material_dialog(final int edit_type) {

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_bio, null);
        dialogBuilder.setView(dialogView);


        final Button add_an_item = (Button) dialogView.findViewById(R.id.add_an_item);
        final EditText ed_name = (EditText) dialogView.findViewById(R.id.ed_name);
        final EditText ed_phone_no = (EditText) dialogView.findViewById(R.id.ed_phone_no);
        final TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        final TextInputLayout lay_phone = (TextInputLayout) dialogView.findViewById(R.id.lay_phone);


        if (edit_type == 1) {
            ed_name.setVisibility(View.VISIBLE);
            lay_phone.setVisibility(View.GONE);

            tv_title.setText("Edit your name");

            ed_name.setText(user_name);
        } else if (edit_type == 2) {
            ed_name.setVisibility(View.GONE);
            lay_phone.setVisibility(View.VISIBLE);
            tv_title.setText("Edit your phone number");

            ed_phone_no.setText("" + user_phone);

        }


        // autocomplete_tv.getAdapter().getItem_name()

        dialogBuilder.setCancelable(true);
        final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();


        add_an_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = ed_name.getText().toString();
                String phone_no = ed_phone_no.getText().toString();
                Map<String, Object> note_pref2 = new HashMap<>();

                if (edit_type == 1) {
                    if (TextUtils.isEmpty(name)) {
                        ed_name.setError("Please enter your name");
                        return;

                    }
                    note_pref2.put(USERS_user_name, name);
                } else if (edit_type == 2) {

                    if (TextUtils.isEmpty(phone_no)) {
                        ed_phone_no.setError("Please enter your phone number");
                        return;

                    }
                    note_pref2.put(USERS_user_phone, phone_no);

                }


                users_ref.document(UID).set(note_pref2, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error , try again", Toast.LENGTH_SHORT).show();
                    }
                });


                b.dismiss();

            }
        });


        b.show();

    }


    private void select_user() {

        users_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }


                if (documentSnapshot.exists()) {
                    ModelUsers note = documentSnapshot.toObject(ModelUsers.class);

                    user_id = note.getUser_id();
                    user_name = note.getUser_name();
                    user_phone = note.getUser_phone();
                    account_status = note.getAccount_status();
                    access_level = note.getAccess_level();
                    account_type = note.getAccount_type();
                    is_online = note.getIs_online();
                    is_merchant = note.getIs_merchant();
                    email_adress = note.getEmail_adress();
                    photo_url = note.getPhoto_url();
                    firebase_service_id = note.getFirebase_service_id();


                    if (TextUtils.isEmpty(user_name)) {
                        tv_phone_name_status.setText("Add your name");
                    } else {
                        tv_phone_name_status.setText("Edit your name");
                    }

                    if (TextUtils.isEmpty(user_phone)) {
                        tv_phone_status.setText("Add your phone number");
                    } else {
                        tv_phone_status.setText("Edit your phone number");
                    }
                    tv_phone_no.setText("Phone: " + fix_display_null_strings(user_phone));
                    tv_name_profile.setText("Name: " + fix_display_null_strings(user_name));
                    tv_name_edit.setText("" + fix_display_null_strings(user_name));
                    tv_phone_no_edit.setText("" + fix_display_null_strings(user_phone));

                    Glide.with(context)
                            .load(photo_url)
                            .into(img_pic_min);

                    if (is_merchant == 1) {
                        card_boda_driver_account.setVisibility(View.VISIBLE);
                        select_driver_info();
                    }else {
                        card_boda_driver_account.setVisibility(View.GONE);
                    }


                }
            }
        });

    }

    private void select_driver_info() {

        drivers_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("Errorsss", "Listen failed.", e);

                    return;
                }

                if (documentSnapshot.exists()) {
                    ModelDrivers note = documentSnapshot.toObject(ModelDrivers.class);
                    driver_name = note.getDriver_name();
                    driver_id_number = note.getDriver_id_number();
                    driver_licence_number = note.getDriver_licence_number();
                    car_reg_no = note.getCar_reg_no();
                    driver_status = note.getDriver_status();
                    photo_of_passport = note.getPhoto_of_passport();
                    photo_of_id = note.getPhoto_of_id();
                    photo_of_licence = note.getPhoto_of_licence();
                    drivers_balance = note.getDrivers_balance();

                    if (driver_status == 1) {
                        tv_boda_acc_state.setText("Acc. status: ACTIVE");


                    } else if (driver_status == 0) {
                        tv_boda_acc_state.setText("Acc. status: INACTIVE");

                    } else if (driver_status == 2) {
                        tv_boda_acc_state.setText("Acc. status: SUSPENDED");

                    }
                    tv_boda_balance.setText("Acc. balance: " + drivers_balance);
                    tv_total_trips.setText("No of trips: ");


                }


            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }


}
