package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;
import com.orion3shoppy.bodamtaani.controllers.GPSTracker;
import com.orion3shoppy.bodamtaani.models.ModelTrips;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.calculate_distance;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.doubleToStringNoDecimal;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.fix_display_null_strings;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_STATES_TIME_LOG;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_is_engaged;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_message;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_notification_trip;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_actor;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_doc_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_payment_trip_is_alive;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;

public class Fragment13_Boda_CompleteTrip extends Fragment {
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users_ref = db.collection(COL_USERS);
    private CollectionReference trips_ref = db.collection(COL_TRIPS);
    private CollectionReference boda_state_log = db.collection(COL_STATES_TIME_LOG);
    private CollectionReference boda_driver = db.collection(COL_DRIVERS);

    String client_phone_number;
    TextView tv_name_profile;
    ImageView img_pic_min;
    TextView tv_destination_town;
    ImageButton btn_end_trip;
    String UID;
    GPSTracker gpsTracker;
    double trip_cost = 0;
    double temp_price = 0;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_boda_complete_trip, container, false);
        context= getContext();
        tv_name_profile= view.findViewById(R.id.tv_name_profile);
        img_pic_min= view.findViewById(R.id.img_pic_min);
        tv_destination_town = view.findViewById(R.id.tv_destination_town);
        btn_end_trip = view.findViewById(R.id.btn_end_trip);

        gpsTracker = new GPSTracker(context);
        UID = GetFirebaseUserID();

        final String user_id = getArguments().getString("user_id");
        final String trip_id = getArguments().getString("trip_id");

        get_client_info(user_id);
        get_trip_information(trip_id);

        btn_end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_win(trip_id);

            }
        });

        return view;
    }

    public void get_client_info(String client_id){

        users_ref.document(client_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    ModelUsers document = documentSnapshot.toObject(ModelUsers.class);
                    String client_phone_number = document.getUser_phone();
                    String u_name = document.getUser_name();
                    String email_adress = document.getEmail_adress();
                    String photo_url = document.getPhoto_url();


                    if(!TextUtils.isEmpty(photo_url)){

                        Glide.with(context)
                                .load(photo_url)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(img_pic_min);
                    }

                    if(TextUtils.isEmpty(u_name) && TextUtils.isEmpty(client_phone_number) && TextUtils.isEmpty(email_adress)){
                        tv_name_profile.setText("N/A");
                        return;
                    }

                    if(!TextUtils.isEmpty(u_name)){
                        tv_name_profile.setText(""+u_name);
                        return;
                    }else if(!TextUtils.isEmpty(client_phone_number)){
                        tv_name_profile.setText(""+client_phone_number);
                        return;
                    }else if(!TextUtils.isEmpty(email_adress)){
                        tv_name_profile.setText(""+email_adress);
                        return;
                    }






                }

            }
        });

    }


    public void get_trip_information(final  String trip_id){





        trips_ref.document(trip_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    return;
                }

                if(documentSnapshot.exists()){
                    ModelTrips document = documentSnapshot.toObject(ModelTrips.class);

                    double lat_1 = document.getLat_1();
                    double log_1 = document.getLog_1();
                    double trip_parcel_cost = document.getTrip_parcel_cost();

//                    double lat_2 = document.getLat_2();
//                    double log_2 = document.getLog_2();


                    double lat_2 = gpsTracker.getLatitude();
                    double log_2 = gpsTracker.getLongitude();

                    double distance = calculate_distance(lat_1, log_1, lat_2,log_2);

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    int town_id = settings.getInt("town_id", 0);
                    if(town_id == 0){
                        Toast.makeText(context, "Your location cannot be determined at the moment", Toast.LENGTH_LONG).show();
                        return;
                    }else if(town_id ==1 ){
                        temp_price = (distance * 0.05) +trip_parcel_cost ;
                    }else {
                        temp_price = (distance * 0.033) + trip_parcel_cost;
                    }

                    int rounded_cost = (int) Math.round(temp_price);
                    trip_cost = rounded_cost;


                    Log.d("ewwwwwwww", "lat 1 - "+lat_1+"\n log 1 - "+log_2+"\nlat 2 - "+lat_2+"log 2 - "+log_2);


                    tv_destination_town.setText("Total Cost: "+trip_cost);

                }else {
                    Log.d("ewwwwwwww", "ddddddddd "+trip_id);
                    tv_destination_town.setText(""+trip_cost);
                }


            }
        });
    }


    private void dialog_win(final  String trip_id) {

        String message = "End trip and receive a payment from your client";


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm End trip");
        alertDialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                complete_trip(trip_id);
                            }
                        });
        alertDialogBuilder.setNegativeButton("dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void complete_trip (String trip_id){

        String date_today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        int trip_state = 4;

        Map<String, Object> params = new HashMap<>();
        params.put(STATES_TIME_LOG_state_id, trip_state);
        params.put(STATES_TIME_LOG_state_time, date_today);
        params.put(STATES_TIME_LOG_state_actor, UID);
        params.put(STATES_TIME_LOG_state_doc_id, trip_id);



        Map<String, Object> trip_data = new HashMap<>();
        trip_data.put(TRIPS_trip_state, trip_state);
        trip_data.put(TRIPS_payment_trip_is_alive, 2);

        Map<String, Object> driver_data = new HashMap<>();
        driver_data.put(DRIVERS_driver_is_engaged, 0);





        // Get a new write batch
        WriteBatch batch = db.batch();

        // Set the value of 'NYC'
        DocumentReference local_request_ref = boda_state_log.document();
        batch.set(local_request_ref, params, SetOptions.merge());

        // Set the value of 'NYC'
        DocumentReference trip_reference = trips_ref.document(trip_id);
        batch.set(trip_reference,trip_data, SetOptions.merge());

        DocumentReference driver_reference = boda_driver.document(UID);
        batch.set(driver_reference,driver_data, SetOptions.merge());



        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // ...
                Toast.makeText(context,"The trip is now complete",Toast.LENGTH_SHORT).show();

                Fragment1_PassangerRide fragment2 = new Fragment1_PassangerRide();
                ((ActivityHomePage) getActivity()).loadFragment(fragment2);
                ((ActivityHomePage) getActivity()).navigation.setVisibility(View.VISIBLE);


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error occurred ",Toast.LENGTH_SHORT).show();
            }
        });


    }



}
