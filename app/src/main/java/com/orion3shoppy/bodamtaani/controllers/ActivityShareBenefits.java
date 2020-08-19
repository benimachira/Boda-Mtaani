package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelWinningUsers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_share_action;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_user_id;

public class ActivityShareBenefits extends AppCompatActivity {

    Context context;
    int share_action;
    String imageURL = "https:/orion3shoppy.com/sharing_images/NEAUCLEAR REACTOR.PNG";

    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser_x;
    String user_id;
    private CollectionReference collection_shares = db.collection(COL_SHARING_DRAW);
    private CollectionReference promo_images = db.collection(COL_PROMO_IMAGES);


    TextView tv_2pm_selection, tv_8pm_selection, tv_yesterday_selection;
    String admin_day;
    String yesterday_winner = "o2hdEUtEGZVhjU4xjcgQmfMYbx62";
    int selection_id = 1;
    String today_winner_1 = "0";
    String today_winner_2 = "0";
    boolean can_play = false;
    androidx.appcompat.widget.Toolbar toolbar;
    String UID;


    int is_winner;
    String local_user_id;

    TextView tv_participation_status, tv_no_participants, tv_yesterday;
    DialogController dialogController;
    String date_today;
    public final int RequestPermissionCode = 1;
    List<String> urlList = new ArrayList<>();

    String local_image_url;

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
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseAuth = FirebaseAuth.getInstance();


        tv_participation_status = (TextView) findViewById(R.id.tv_participation_status);
        tv_yesterday = (TextView) findViewById(R.id.tv_yesterday);


        UID = GetFirebaseUserID();

        date_today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        dialogController = new DialogController(context);


        load_firebase();

        tv_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ActivityPreviousWinners.class);
                startActivity(intent);


            }
        });

    }


    public void load_firebase() {

        collection_shares.whereEqualTo(COL_SHARING_DRAW_user_id, UID).whereEqualTo(COL_SHARING_DRAW_participation_date, date_today).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {

                    if (queryDocumentSnapshots.size() > 0) {


                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            ModelWinningUsers note = doc.toObject(ModelWinningUsers.class);

                            local_user_id = note.getUser_id();
                            is_winner = note.getIs_winner();


                            if (is_winner == 1) {
                                tv_participation_status.setText("Status: Congratulations you have won today");
                            } else {
                                tv_participation_status.setText("Status: Active (shared via WhatsApp)");
                            }

                            tv_participation_status.setText("Inactive");
                            tv_yesterday.setText("See other previous winner");

                        }


                        can_play = false;

//                        admin_day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(participation_date);

                    } else {
                        can_play = true;

                        tv_participation_status.setText("Status: Inactive");
                        tv_yesterday.setText("Previous winners");

                    }

                }
            }
        });


    }


    public void load_promo_images() {

        int random_number = new Random().nextInt(3);


        promo_images.whereEqualTo("img_id", random_number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        local_image_url = doc.getString("image_name");


                    }


                } else {
                    local_image_url = "";

                }

                Log.d("eeeeeeeee","dddd "+queryDocumentSnapshots.size()+" pp "+local_image_url);

                DownloadFile asyncTask = new DownloadFile();
                asyncTask.execute(local_image_url);


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "There is no image to share "+e.getMessage(),Toast.LENGTH_SHORT).show();
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


    public void share_starter(int share_action) {
        dialogController.dialog_show("Sharing, please wait...");

        collection_shares.whereEqualTo(COL_SHARING_DRAW_user_id, UID).whereEqualTo(COL_SHARING_DRAW_participation_date, date_today).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {

                    if (queryDocumentSnapshots.size() > 0) {
                        can_play = false;
                    } else {
                        can_play = true;
                    }


                    if (can_play) {

                        load_promo_images();


                    } else {
                        dialogController.dialog_dismiss();
                        Toast.makeText(context, "You can only share one time in a selection", Toast.LENGTH_LONG).show();
                        return;
                    }

                }
            }
        });


    }


    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }


    class DownloadFile extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(context);// Change Mainactivity.this with your activity name.
        String strFolderName;
        String pic_name;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        }
        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String targetFileName=ts+".jpg";//Change name and subname
                int lenghtOfFile = conexion.getContentLength();
                String PATH = Environment.getExternalStorageDirectory()+ "/BodaMtaani/";
                File folder = new File(PATH);
                pic_name=PATH+targetFileName;
                if(!folder.exists()){
                    folder.mkdir();//If there is no folder it will be created.
                }
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(PATH+targetFileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress ((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
            if(mProgressDialog.getProgress()==mProgressDialog.getMax()){
                mProgressDialog.dismiss();
                Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            File imgFile = new  File(pic_name);

            if(imgFile.exists()){
//                String image_file= imgFile.getAbsolutePath();
                share_all(imgFile, share_action);
            }


        }

    }

    private class GetImages extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;

        private Bitmap bitmap;
        private FileOutputStream fos;

        private GetImages(String requestUrl, String _imagename_) {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();


            this.requestUrl = requestUrl;
            this.imagename_ = ts
            ;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                bitmap = getResizedBitmap(bitmap, 1028, 720);

            } catch (Exception ex) {
                dialogController.dialog_dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            File file = saveToSdCard(bitmap, imagename_);

            if (file.exists()) {
                share_all(file, share_action);
            } else {
                dialogController.dialog_dismiss();
                share_all(file, share_action);
            }

        }
    }

    public File saveToSdCard(Bitmap bitmap, String filename) {

        File folder = AppConstants.AD_IMAGE_STORAGE;//the dot makes this directory hidden to the user
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename + ".jpg");
//        if (file.exists()) {
//            return file;
//        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            return null;
        }
        return file;
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

        if (resultCode != 0) {
            Map<String, Object> note = new HashMap<>();
            note.put(COL_SHARING_DRAW_user_id, UID);
            note.put(COL_SHARING_DRAW_participation_date, date_today);
            note.put(COL_SHARING_DRAW_is_winner, 0);
            note.put(COL_SHARING_DRAW_share_action, share_action);


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
                    "Sharing failed",
                    Toast.LENGTH_SHORT).show();
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
}
