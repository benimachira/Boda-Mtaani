package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelSharingInfo;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_PROMO_IMAGES;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_is_winner;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_participation_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_participation_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_participation_user_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_share_action;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STORAGE_BODA_PROMO_IMAGES;

public class ActivityShareBenefits extends AppCompatActivity {

    Context context;
    int share_action;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collection_shares = db.collection(COL_SHARING_DRAW);
    private CollectionReference promo_images = db.collection(COL_PROMO_IMAGES);
    private CollectionReference users_reference = db.collection(COL_USERS);

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    boolean can_play = false;
    androidx.appcompat.widget.Toolbar toolbar;
    String UID;


    int is_winner;
    String local_user_id;

    TextView tv_participation_status, tv_yesterday;
    DialogController dialogController;
    String date_today;
    public final int RequestPermissionCode = 1;


    String local_image_url;
    String image_filename;
    ProgressBar progress_loading;

    String user_name, phone_no, email_adress;
    String person_identity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunity_reward);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        context = this;


        tv_participation_status = (TextView) findViewById(R.id.tv_participation_status);
        tv_yesterday = (TextView) findViewById(R.id.tv_yesterday);
        progress_loading = (ProgressBar) findViewById(R.id.progress_loading);

        UID = GetFirebaseUserID();

        date_today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        dialogController = new DialogController(context);

        get_user_info();


        load_firebase();

        tv_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ActivityPreviousWinners.class);
                startActivity(intent);


            }
        });

    }

    public void get_user_info() {

        users_reference.document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ModelUsers note = documentSnapshot.toObject(ModelUsers.class);
                    user_name = note.getUser_name();
                    phone_no = note.getUser_phone();
                    email_adress = note.getEmail_adress();

                    if ((TextUtils.isEmpty(phone_no)) && (TextUtils.isEmpty(phone_no)) && (TextUtils.isEmpty(email_adress))) {
                        person_identity = "John Doe";
                        return;
                    }

                    if (!TextUtils.isEmpty(user_name)) {
                        person_identity = user_name;
                        return;
                    }

                    if (!TextUtils.isEmpty(phone_no)) {
                        person_identity = phone_no;
                        return;
                    }



                    if (!TextUtils.isEmpty(email_adress)) {
                        person_identity = email_adress;
                        return;
                    }


                }
            }
        });
    }


    public void load_firebase() {
        progress_loading.setVisibility(View.VISIBLE);
        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        collection_shares.whereEqualTo(COL_SHARING_DRAW_user_id, UID).
                whereEqualTo(COL_SHARING_DRAW_participation_day, times_tamp)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            progress_loading.setVisibility(View.GONE);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {

                            if (queryDocumentSnapshots.size() > 0) {

                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                    ModelSharingInfo note = doc.toObject(ModelSharingInfo.class);

                                    local_user_id = note.getUser_id();
                                    is_winner = note.getIs_winner();



                                    tv_participation_status.setText("Your status : Already played today");
//                            tv_yesterday.setText("See other previous winner");

                                }


                                can_play = false;

//                        admin_day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(participation_date);

                            } else {
                                can_play = true;

                                tv_participation_status.setText("Your status : You can play");
//                        tv_yesterday.setText("Previous winners");

                            }

                        }

                        progress_loading.setVisibility(View.GONE);
                        tv_yesterday.setText(R.string.yesterday_winner);
                    }
                });


    }


    public void load_promo_images(final int share_action) {

//        int random_number = new Random().nextInt(3);
        Random r = new Random();
        int min = 1;
        int max = 7;
        int random_number = r.nextInt(max - min + 1) + min;
        Log.d("wwwww", "random no " + random_number);


        promo_images.whereEqualTo("img_id", random_number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        local_image_url = doc.getString("image_name");


                    }
                    // Toast.makeText(context, "There a "+local_image_url,Toast.LENGTH_SHORT).show();
                    Log.d("wwwww", "file url " + local_image_url);

                    download_the_boy(local_image_url, share_action);
                } else {
                    local_image_url = "";
                }


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "There is no image to share " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialogController.dialog_dismiss();
            }
        });


    }


    public void share_whats_app(View view) {
        share_action = 0;
        share_starter(share_action);

    }


    public void share_facebook(View view) {
        share_action = 1;

        share_starter(share_action);

    }

    public void share_twitter(View view) {

        share_action = 2;
        share_starter(share_action);


    }


    public void download_the_boy(String db_file_name, final int share_action) {

//        String image_filename = db_file_name.substring(db_file_name.lastIndexOf("/") + 1);
        final String image_online_loca = STORAGE_BODA_PROMO_IMAGES + db_file_name;


        Log.d("wwwww", "online loca 1 :  " + image_online_loca);

// Create a reference with an initial file path and name
        StorageReference pathReference = mStorageRef.child(image_online_loca);

        pathReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("wwwww", "online loca 2 :  " + bytes);

                Long tsLong = System.currentTimeMillis() / 1000;
                String timestamp = tsLong.toString();

                try {

//                final Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    final File photo = new File(Environment.getExternalStorageDirectory(), "BodaMtaani" + timestamp + ".jpg");

                    if (photo.exists()) {
                        photo.delete();
                    }

                    Log.d("wwwww", "online loca 3 " + photo.getAbsolutePath());


                    FileOutputStream fos = new FileOutputStream(photo.getPath());

                    fos.write(bytes);
                    fos.close();

                    if (photo.exists()) {
                        Log.d("wwwww", "online loca 4a");
                        share_all(photo, share_action);
                    } else {
                        Log.d("wwwww", "online loca 4b");
                    }


                } catch (java.io.IOException e) {
                    Log.e("wwwww", "online loca 4", e);
                }

                dialogController.dialog_dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wwwww", "online loca 5 :  " + image_online_loca);

                dialogController.dialog_dismiss();
            }
        });

//        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // Got the download URL for 'users/me/profile.png'
//
//                String file_name = uri.toString();
//
//                AppUpdate downloadFileFromURL = new AppUpdate(context);
//                downloadFileFromURL.execute(file_name);
//
//                Log.d("wwwww", "online loca 2 :  " + file_name);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//
//                Log.d("wwwww", "online loca :  " + " file " + image_online_loca + " exp " + exception.getMessage());
//            }
//        });
    }


    public void download_the_file(String db_file_name) {

        image_filename = db_file_name.substring(db_file_name.lastIndexOf("/") + 1);


        String image_online_loca = STORAGE_BODA_PROMO_IMAGES + image_filename;

        Log.d("wwwww", "online loca :  " + image_online_loca);

        StorageReference islandRef = mStorageRef.child(image_online_loca);
        File localFile = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/brother_bob_marley/" + local_image_url + ".jpg");

//        File localFile = File.createTempFile("images", "jpg");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created

                Log.d("wwwww", "results  " + taskSnapshot.getBytesTransferred());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors

                Log.d("wwwww", "download error " + exception.getCause().getMessage());
            }
        });
    }


    public void share_starter(final int share_action) {
        dialogController.dialog_show("Sharing, please wait...");
        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        collection_shares.whereEqualTo(COL_SHARING_DRAW_user_id, UID).
                whereEqualTo(COL_SHARING_DRAW_participation_day, times_tamp).
                limit(1).
                get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {

                            if (queryDocumentSnapshots.size() > 0) {
                                can_play = false;
                            } else {
                                can_play = true;
                            }


                            if (can_play) {

                                load_promo_images(share_action);


                            } else {
                                dialogController.dialog_dismiss();
                                Toast.makeText(context, "You can only share one time in a selection", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }


    public void share_all(File image_file, int share_action) {

        Uri imgUri = Uri.parse(image_file.getAbsolutePath());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        if (share_action == 0) {
            // Target whatsapp:
            shareIntent.setPackage("com.whatsapp");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (share_action == 1) {
            // Target whatsapp:
            shareIntent.setPackage("com.facebook.katana");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (share_action == 2) {
            // Target whatsapp:
            shareIntent.setPackage("com.twitter.android");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.getData();


        try {
            startActivityForResult(shareIntent, 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    "Not been installed.",
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());


        if (resultCode != 0) {

            Map<String, Object> note = new HashMap<>();
            note.put(COL_SHARING_DRAW_user_id, UID);
            note.put(COL_SHARING_DRAW_participation_date, date);
            note.put(COL_SHARING_DRAW_is_winner, 0);
            note.put(COL_SHARING_DRAW_share_action, share_action);
            note.put(COL_SHARING_DRAW_participation_day, times_tamp);
            note.put(COL_SHARING_DRAW_participation_user_name, person_identity);


            collection_shares.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(context,
                            "Thank you for sharing, you have entered sharing competition",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(context,
                    "Sharing failed, or was canceled",
                    Toast.LENGTH_SHORT).show();
        }
        Log.d("wwwwwwww", "result code " + resultCode + " request code " + requestCode + " data " + data);


//        if (resultCode != 0) {
//
//
//        } else {
//            Toast.makeText(context,
//                    "Sharing failed",
//                    Toast.LENGTH_SHORT).show();
//        }


    }


    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */

        ProgressDialog progressDialog;
        String file_path = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            Log.d("wwwww", "background url 1 " + f_url);

            String image_url = f_url[0];
            String image_filename = image_url.substring(image_url.lastIndexOf("/") + 1);
            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();
            int count;
            try {
                URL url = new URL(image_url);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                File file_dec = new File(Environment
                        .getExternalStorageDirectory().toString()
                        + "/brother_bob_marley/" + timestamp + ".jpg");

                // Output stream
                OutputStream output = new FileOutputStream(file_dec);

//

                Log.d("wwwww", "back ground 2 " + file_dec);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                return file_path = file_dec.getAbsolutePath();

            } catch (Exception e) {

                Log.d("wwwww", "background 3 " + image_filename);
                Log.e("Error: ", e.getMessage());

                return e.getMessage();
            }


        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            progressDialog.dismiss();
            Log.d("wwwww", "background url  " + file_url);


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {


            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean finelocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarsePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (finelocationPermission && coarsePermission && write_external_storage) {
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
        int write_external_storage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);


        return
                finelocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        coarselocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        write_external_storage == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                WRITE_EXTERNAL_STORAGE

        }, RequestPermissionCode);

    }


    public class AppUpdate extends AsyncTask<String, Void, String> {
        Context ctx;
        ProgressDialog progressDialog;

        AppUpdate(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ctx);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Downloading Updates.....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();


            String url = params[0];

            Log.d("wwwww", "background 1 " + url);


            int i = 0;
            try {

                //initiating a sever connection
                URL url_update = new URL(url);
                HttpURLConnection c = (HttpURLConnection) url_update.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                String FileName = "BodaMtaani";
                // Creating path to store the updated apk file
                File file = new File(Environment.getExternalStorageDirectory(), FileName);
                file.mkdirs();

                File outputFile = new File(file, "" + timestamp);

                //initiating a OUTPUT Stream
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                //put the file into bytes

                byte[] buffer = new byte[1024];
                int line = 0;
                while ((line = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, line);
                    progressDialog.setProgress((int) ((i / (float) buffer.length) * 100));
                    i++;
                }
                fos.close();
                is.close();

                String file_name = outputFile.toString();


                Log.d("wwwww", "background 2 " + file_name);


                return outputFile.toString();


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getCause());
                return e.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ctx, "download complete " + result, Toast.LENGTH_LONG).show();
            Log.d("wwwww", "background 4 " + result);
        }


    }
}
