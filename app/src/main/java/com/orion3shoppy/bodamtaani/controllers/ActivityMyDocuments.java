package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.SplashActivity;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelDriversDocuments;
import com.orion3shoppy.bodamtaani.models.ModelTrips;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_RIDERS_DOCUMENT;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_document_review_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_photo_of_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_photo_of_licence;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_photo_of_passport;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STORAGE_BODA_DRIVER_DOCS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_is_online;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_photo_url;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_phone;


public class ActivityMyDocuments extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser_x;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);
//    private CollectionReference drivers_document_ref = db.collection(COL_DRIVERS);



    Context context;
    String UID = "";

    String driver_name;
    String driver_id_number;
    String driver_licence_number;
    String car_reg_no;
    int driver_status;
    String photo_of_passport;
    String photo_of_id;
    String photo_of_licence;
    double drivers_balance;


    ImageView img_id_photo, img_licence_photo, img_passport;

    public final int RequestPermissionCode = 1;
    public final int PICK_IMAGE = 2;
    public int IMAGE_TO_PICK = 0;
    String photo_path_ID;
    String photo_path_passport;
    String photo_path_licence;
    String shared_photo_url;
    DialogController dialogController;
    TextView tv_id_display,tv_passport_display,tv_licence_display,tv_top_info;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_documents);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        context = this;

        img_id_photo = (ImageView) findViewById(R.id.img_id_photo);
        img_licence_photo = (ImageView) findViewById(R.id.img_licence_photo);
        img_passport = (ImageView) findViewById(R.id.img_passport);

        tv_id_display= (TextView) findViewById(R.id.tv_id_display);
        tv_passport_display= (TextView) findViewById(R.id.tv_passport_display);
        tv_licence_display= (TextView) findViewById(R.id.tv_licence_display);
        tv_top_info= (TextView) findViewById(R.id.tv_top_info);
        dialogController = new DialogController(context);

        context = this;


        UID = GetFirebaseUserID();


        select_driver_info();


    }



    public void add_id(View view) {
        IMAGE_TO_PICK = 1;
        add_promo_image();


    }

    public void add_passport(View view) {
        IMAGE_TO_PICK = 2;
        add_promo_image();

    }
    public void add_licence(View view) {
        IMAGE_TO_PICK = 3;

        add_promo_image();

    }





    private void select_driver_info() {

        drivers_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                if (documentSnapshot.exists()) {
                    ModelDrivers note = documentSnapshot.toObject(ModelDrivers.class);


                    driver_name = note.getDriver_name();
                     driver_id_number= note.getDriver_id_number();
                     driver_licence_number= note.getDriver_licence_number();
                     car_reg_no= note.getCar_reg_no();
                     driver_status= note.getDriver_status();
                     photo_of_passport= note.getPhoto_of_passport();
                     photo_of_id= note.getPhoto_of_id();
                     photo_of_licence= note.getPhoto_of_licence();
                     drivers_balance= note.getDrivers_balance();

                    tv_id_display= (TextView) findViewById(R.id.tv_id_display);
                    tv_passport_display= (TextView) findViewById(R.id.tv_passport_display);
                    tv_licence_display= (TextView) findViewById(R.id.tv_licence_display);

                    if(!TextUtils.isEmpty(photo_of_id)){

                        tv_id_display.setText("Change my ID");

                        Glide.with(context)
                                .load(photo_of_id)
                                .into(img_id_photo);
                    }else {
                        tv_id_display.setText("Add my ID");
                    }

                    if(!TextUtils.isEmpty(photo_of_passport)){

                        tv_passport_display.setText("Change my Passport");
                        Glide.with(context)
                                .load(photo_of_passport)
                                .into(img_passport);
                    }else {
                        tv_passport_display.setText("Add my Passport");
                    }
                    if(!TextUtils.isEmpty(photo_of_licence)){

                        tv_licence_display.setText("Change my Licence");
                        Glide.with(context)
                                .load(photo_of_licence)
                                .into(img_licence_photo);
                    }else {
                        tv_licence_display.setText("Add my Licence");
                    }

                    if(TextUtils.isEmpty(photo_of_licence) && TextUtils.isEmpty(photo_of_passport) && TextUtils.isEmpty(photo_of_id)){
                        tv_top_info.setText("All your documents are up to date.");
                    }else {
                        tv_top_info.setText("Some of your documents are missing.");
                    }










                }


            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    private void upload_image_to_firebase(final String file_Path) {
        dialogController.dialog_show("0%");
        final StorageReference ref = mStorageRef.child(STORAGE_BODA_DRIVER_DOCS + UUID.randomUUID().toString() + ".jpg");
        Uri file = Uri.fromFile(new File(file_Path));
        UploadTask uploadTask = ref.putFile(file);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String storage_uri = downloadUri.toString();
                    shared_photo_url= downloadUri.toString();


                    Map<String, Object> note_pref = new HashMap<>();

                    if (IMAGE_TO_PICK == 1) {
                        note_pref.put(DRIVERS_photo_of_id, storage_uri);
                        note_pref.put(DRIVERS_document_review_status, 1);

                    } else if (IMAGE_TO_PICK == 2) {
                        note_pref.put(DRIVERS_photo_of_passport, storage_uri);
                        note_pref.put(DRIVERS_document_review_status, 1);
                    } else if (IMAGE_TO_PICK == 3) {
                        note_pref.put(DRIVERS_photo_of_licence, storage_uri);
                        note_pref.put(DRIVERS_document_review_status, 1);
                    }


                    drivers_ref.document(UID).set(note_pref, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Image has been uploaded", Toast.LENGTH_SHORT).show();


                            if (IMAGE_TO_PICK == 1) {

                                Glide.with(context)
                                        .load(shared_photo_url)
                                        .into(img_id_photo);

                            } else if (IMAGE_TO_PICK == 2) {

                                Glide.with(context)
                                        .load(shared_photo_url)
                                        .into(img_passport);

                            } else if (IMAGE_TO_PICK == 3) {


                                Glide.with(context)
                                        .load(shared_photo_url)
                                        .into(img_licence_photo);

                            }


                        }
                    });


                } else {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();


                }
            }
        });

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                dialogController.update_messaging("This might take a minute.. " + (int) progress + "%");
//                progressDialog.setMessage("This might take a minute.. " + (int) progress + "%");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                dialogController.dialog_dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Promotion image could not be uploaded", Toast.LENGTH_SHORT).show();
                dialogController.dialog_dismiss();
            }
        });

    }


    public void run_permission_checks() {

        if (!CheckingPermissionIsEnabledOrNot()) {
            RequestMultiplePermission();
        }
    }

    public void add_promo_image() {

        run_permission_checks();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent(), "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column

                    if (IMAGE_TO_PICK == 1) {
                        photo_path_ID = cursor.getString(columnIndex);
                        upload_image_to_firebase(photo_path_ID);
                    } else if (IMAGE_TO_PICK == 2) {
                        photo_path_passport = cursor.getString(columnIndex);
                        upload_image_to_firebase(photo_path_passport);
                    } else if (IMAGE_TO_PICK == 3) {
                        photo_path_licence = cursor.getString(columnIndex);
                        upload_image_to_firebase(photo_path_licence);
                    }

                    cursor.close();
                    // Set the Image in ImageView after decoding the String


                }
                break;


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {


            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean finelocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarsePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (finelocationPermission && coarsePermission && writePermission && readPermission) {
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
//                        finish();
//                        startActivity(getIntent());

                    }
                }

        }
    }


    public boolean CheckingPermissionIsEnabledOrNot() {

        int finelocPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int coarselocPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int writePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int readPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);


        return
                finelocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        coarselocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        readPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        writePermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE

        }, RequestPermissionCode);

    }


}
