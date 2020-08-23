package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_STATES_TIME_LOG;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_is_engaged;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_notification_trip;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_actor;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_doc_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_payment_trip_is_alive;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;

public class Fragment5_InTransit extends Fragment {
    Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference trips_ref = db.collection(COL_TRIPS);
    private CollectionReference boda_state_log = db.collection(COL_STATES_TIME_LOG);
    private CollectionReference boda_driver = db.collection(COL_DRIVERS);
    private CollectionReference notification_refence = db.collection(COL_NOTIFICATION);


    String UID;
    Button end_trip;
    String user_id;

    LinearLayout linear_end_trip_midway;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_in_transit, container, false);


        context= getContext();
        final String trip_id = getArguments().getString("trip_id");
        int account_type = getArguments().getInt("account_type");

        user_id = getArguments().getString("user_id");


        UID = GetFirebaseUserID();
        end_trip =view.findViewById(R.id.end_trip);
        linear_end_trip_midway=view.findViewById(R.id.linear_end_trip_midway);

        if(account_type ==2){
            linear_end_trip_midway.setVisibility(View.VISIBLE);
        }


        end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_win(trip_id);
            }
        });



        return view;
    }


    private void dialog_win(final  String trip_id) {
        String message = "You want to end this trip";


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm end trip");
        alertDialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                close_the_job(trip_id);
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

    public void close_the_job(String trip_id){

        String date_today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        int trip_state = 5;
        String message = "Your trip tracking number "+trip_id+" completed, Thank your for choosing Boda Mtaani. ";

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




        Map<String, Object> params_notification = new HashMap<>();
        params_notification.put(NOTIFICATION_notification_trip, trip_id);
        params_notification.put(NOTIFICATION_user_id, user_id);
        params_notification.put(NOTIFICATION_date, date_today);
        params_notification.put(NOTIFICATION_status, 0);



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

        DocumentReference notification_reference = notification_refence.document(UID);
        batch.set(notification_reference,params_notification, SetOptions.merge());


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
                Toast.makeText(context,"Error occured ",Toast.LENGTH_SHORT).show();
            }
        });


    }


}
