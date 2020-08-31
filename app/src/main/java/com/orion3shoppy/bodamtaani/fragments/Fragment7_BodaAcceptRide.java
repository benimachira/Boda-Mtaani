package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_JOB_BOARD;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_JOB_REQUEST;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_STATES_TIME_LOG;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_req_status;
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
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;

public class Fragment7_BodaAcceptRide extends Fragment {
    Context context;
    ImageButton btn_accept;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference job_request_ref = db.collection(COL_JOB_REQUEST);
    private CollectionReference trips_ref = db.collection(COL_TRIPS);
    private CollectionReference states_time_log = db.collection(COL_STATES_TIME_LOG);
    private CollectionReference notification_reference = db.collection(COL_NOTIFICATION);

    String UID,user_id;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_boda_accept_ride, container, false);
        context= getContext();
        final String trip_id = getArguments().getString("trip_id");
        user_id = getArguments().getString("user_id");

        Log.d("eeeeeeeeeeee", "eeeee "+trip_id);

        btn_accept = view.findViewById(R.id.btn_accept);
        UID = GetFirebaseUserID();

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_win(trip_id);
            }
        });

        return view;
    }

    private void dialog_win(final  String trip_id) {
        String message = "Confirm you want to accept this ride";


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm trip");
        alertDialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                accept_the_job(trip_id);
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

    public void accept_the_job (String trip_id){
        String date_today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        int trip_state =1;
        String message = "Your parcel tracking number "+trip_id+" has been accepted by the rider, Give the rider a minute to arrive at the set pickup location";

        Map<String, Object> params = new HashMap<>();
        params.put(JOB_REQUEST_req_status, 2);

        Map<String, Object> trip_data = new HashMap<>();
        trip_data.put(TRIPS_trip_state, trip_state);
        trip_data.put(TRIPS_trip_driver_id, UID);
        trip_data.put(TRIPS_payment_trip_is_alive, 1);


        Map<String, Object> params_log_data = new HashMap<>();
        params_log_data.put(STATES_TIME_LOG_state_id, trip_state);
        params_log_data.put(STATES_TIME_LOG_state_time, date_today);
        params_log_data.put(STATES_TIME_LOG_state_actor, UID);
        params_log_data.put(STATES_TIME_LOG_state_doc_id, trip_id);

        Map<String, Object> params_notification = new HashMap<>();
        params_notification.put(NOTIFICATION_notification_trip, trip_id);
        params_notification.put(NOTIFICATION_user_id, user_id);
        params_notification.put(NOTIFICATION_message, message);
        params_notification.put(NOTIFICATION_date, date_today);
        params_notification.put(NOTIFICATION_status, 0);


//        const job_request_data = {
//                //THE RICHEST MAN IN BABYLON
//
//
//                trip_id:trips_doc_id,   //inital request for the job order by the client
//                driver_id: "",   //the driver taking the parcel, LEAVING THIS FOR THE ENGINE TO FILL IN WHEN THE MESSANGER IS FOUND
//                date_requested:timestamp, //marks the day of this order
//                req_status:0,  //initial request which is set at 0 for new request {0,1,2,3, 4 : new , a request is out, accepted, decline, expired hence no rider found }
//                retries:0,
//                request_day:doc_timestamp,
//                expiry_time:expiry_time, // 2 mins after the creation of my new digital gold.
//                trip_user_id:user_id
//
//		}



        // Get a new write batch
        WriteBatch batch = db.batch();

        // Set the value of 'NYC'
        DocumentReference local_request_ref = job_request_ref.document(trip_id);
        batch.set(local_request_ref, params, SetOptions.merge());

        // Set the value of 'NYC'
        DocumentReference trip_reference = trips_ref.document(trip_id);
        batch.set(trip_reference,trip_data, SetOptions.merge());

        DocumentReference local_time_log = states_time_log.document();
        batch.set(local_time_log, params_log_data, SetOptions.merge());


        DocumentReference local_notification = notification_reference.document();
        batch.set(local_notification, params_notification, SetOptions.merge());


        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
        // ...
            Toast.makeText(context,"The job has been accepted",Toast.LENGTH_SHORT).show();


        }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error occurred ",Toast.LENGTH_SHORT).show();
            }
        });


    }




}
