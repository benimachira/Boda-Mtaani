package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;
import com.orion3shoppy.bodamtaani.controllers.ActivityPayments;
import com.orion3shoppy.bodamtaani.controllers.DialogController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.orion3shoppy.bodamtaani.controllers.ActivityHomePage.AUTOCOMPLETE_REQUEST_CODE;
import static com.orion3shoppy.bodamtaani.controllers.ActivityHomePage.AUTOCOMPLETE_REQUEST_CODE2;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.calculate_distance;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.doubleToStringNoDecimal;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.remove_null;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_JOB_REQUEST;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_date_requested;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_expiry_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_req_retries;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_req_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_request_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_trip_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_trip_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_message;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_notification_trip;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_lat_1;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_lat_2;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_log_1;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_log_2;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_payment_trip_is_alive;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_cost;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_destination_town;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_parcel_cost;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_payment_town_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_payment_verified;

public class Fragment1_PassangerRide extends Fragment {

    Context context;
    String UID;
    TextView ed_pick_loca, ed_delivery_loca, tv_pass_cost;
    Button request_rider_pass;

    List<LatLng> pickup_lat_log = new ArrayList<>();
    List<LatLng> destination_lat_log = new ArrayList<>();
    double trip_cost = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collection_trips = db.collection(COL_TRIPS);
    private CollectionReference collection_notification = db.collection(COL_NOTIFICATION);
    private CollectionReference collection_job_request = db.collection(COL_JOB_REQUEST);
    String destination_town = "";
    DialogController dialogController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_people, container, false);
        context = getContext();
        UID = GetFirebaseUserID();

        dialogController =new DialogController(context);

        ed_pick_loca = (TextView) view.findViewById(R.id.ed_pick_loca);
        ed_delivery_loca = (TextView) view.findViewById(R.id.ed_delivery_loca);
        request_rider_pass = (Button) view.findViewById(R.id.request_rider_pass);
        tv_pass_cost = (TextView) view.findViewById(R.id.tv_pass_cost);


        ed_pick_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHomePage) getActivity()).onSearchCalled(1);
            }
        });

        ed_delivery_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHomePage) getActivity()).onSearchCalled(2);
            }
        });

        request_rider_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (destination_lat_log.size() > 0 && pickup_lat_log.size() > 0) {

                    show_dialog_request_rider();

                } else {
                    Toast.makeText(context, "Please set both pickup and delivery locations", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = remove_null(place.getName()) + " " + remove_null(place.getAddress());
//                String address = remove_null(place.getName());
                LatLng latLng = place.getLatLng();

                if (pickup_lat_log.size() > 0) {
                    pickup_lat_log.set(0, latLng);
                } else {
                    pickup_lat_log.add(latLng);
                }

                ed_pick_loca.setText("From: " + address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                ed_pick_loca.setText("Failed try again");
                ed_pick_loca.setTextColor(getResources().getColor(R.color.red_700));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE2) {

            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                Toast.makeText(AutocompleteFromIntentActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = remove_null(place.getName()) + " " + remove_null(place.getAddress());
                LatLng latLng = place.getLatLng();

                destination_town =remove_null(place.getName());

                if (destination_lat_log.size() > 0) {
                    destination_lat_log.set(0, latLng);
                } else {
                    destination_lat_log.add(latLng);
                }

                ed_delivery_loca.setText("To: " + address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                ed_delivery_loca.setText("Failed, try again");
                ed_delivery_loca.setTextColor(getResources().getColor(R.color.red_700));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (pickup_lat_log.size() > 0 && destination_lat_log.size() > 0) {

            double distance = calculate_distance(pickup_lat_log.get(0).latitude, pickup_lat_log.get(0).longitude, destination_lat_log.get(0).latitude, destination_lat_log.get(0).longitude);


            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            int town_id = settings.getInt("town_id", 0);
           if(town_id == 0){

               Toast.makeText(context, "Your location cannot be determined at the moment", Toast.LENGTH_LONG).show();
               return;
           }else if(town_id ==1 ){
               trip_cost = doubleToStringNoDecimal((distance * 0.05));
           }else {
               trip_cost = doubleToStringNoDecimal((distance * 0.033));
           }


            tv_pass_cost.setVisibility(View.VISIBLE);
            tv_pass_cost.setText("Ksh " + (int)trip_cost);

        } else {
            tv_pass_cost.setVisibility(View.GONE);
        }

    }


//    public void request_pass_rider() {
//
//        int TRIPS_trip_type = 1;
//        int TRIPS_trip_state = 0;
//        double TRIPS_lat_1 = pickup_lat_log.get(0).latitude;
//        double TRIPS_log_1 = pickup_lat_log.get(1).longitude;
//        double TRIPS_lat_2 = destination_lat_log.get(0).latitude;
//        double TRIPS_log_2 = destination_lat_log.get(1).longitude;
//
//        Intent intent = new Intent(context, ActivityPayments.class);
//        intent.putExtra("TRIPS_user_id", UID);
//        intent.putExtra("TRIPS_trip_type", TRIPS_trip_type);
//        intent.putExtra("TRIPS_trip_state", TRIPS_trip_state);
//        intent.putExtra("TRIPS_lat_1", TRIPS_lat_1);
//        intent.putExtra("TRIPS_lat_2", TRIPS_lat_2);
//        intent.putExtra("TRIPS_log_1", TRIPS_log_1);
//        intent.putExtra("TRIPS_log_2", TRIPS_log_2);
//        intent.putExtra("bundle_status", 0);
//        intent.putExtra("trip_cost", trip_cost);
//        intent.putExtra("parcel_cost", 0.0);
//
//        startActivity(intent);
//
//        fetch_loca_payment ();
//
//
//    }

//    public void fetch_loca_payment (){
//
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        int town_id = settings.getInt("town_id", 0);
//
//
//        if(town_id > 0){
//            insert_trip(town_id);
//            Toast.makeText(context,"Your area has Bodas "+town_id,Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(context,"Boda Mtaani is not available in this area "+town_id,Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public void submit_trip (){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int town_id = settings.getInt("town_id", 0);


        if(town_id > 0){
            insert_trip(town_id);
            Toast.makeText(context,"Your area has Bodas "+town_id,Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context,"Boda Mtaani is not available in this area "+town_id,Toast.LENGTH_SHORT).show();
        }

    }

    public void insert_trip( int town_id) {

        dialogController.dialog_show("Sending your request ... ");



        int parcel_cost =0;
        int local_trip_type = 1;
        int local_trip_state = 0;
        double local_lat_1 = pickup_lat_log.get(0).latitude;
        double local_log_1 = pickup_lat_log.get(0).longitude;
        double local_lat_2 = destination_lat_log.get(0).latitude;
        double local_log_2 = destination_lat_log.get(0).longitude;


        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String todays_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> params = new HashMap<>();
        params.put(TRIPS_user_id, UID);
        params.put(TRIPS_trip_type, local_trip_type);
        params.put(TRIPS_trip_state, local_trip_state);
        params.put(TRIPS_lat_1, local_lat_1);
        params.put(TRIPS_log_1, local_log_1);
        params.put(TRIPS_lat_2, local_lat_2);
        params.put(TRIPS_log_2, local_log_2);
        params.put(TRIP_payment_verified, 1);
        params.put(TRIP_payment_town_id, town_id);
        params.put(TRIPS_payment_trip_is_alive, 0);
        params.put(TRIPS_trip_bundle_status, 0);
        params.put(TRIPS_trip_bundle_day, times_tamp);
        params.put(TRIPS_trip_date, todays_date);
        params.put(TRIPS_trip_cost, trip_cost);
        params.put(TRIPS_trip_parcel_cost, parcel_cost);
        params.put(TRIPS_trip_destination_town, destination_town);


        collection_trips.add(params).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String trip_id = documentReference.getId();
                set_trips_extra(trip_id);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogController.dialog_dismiss();
                Toast.makeText(context, "Error , try again", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void set_trips_extra (String trip_id){


        long ONE_MINUTE_IN_MILLIS=180000;//3 mins
        Calendar date = Calendar.getInstance();
        long t= date.getTimeInMillis();
        Date exp_date=new Date(t + (10 * ONE_MINUTE_IN_MILLIS));

        String notification_message = "Your trip to "+destination_town+" has been sent, A rider will be sent to your pickup location in a few minutes ";

        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String todays_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());
        String expiry_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(exp_date);



        WriteBatch batch = db.batch();


        DocumentReference notification_Reference = collection_notification.document();
        DocumentReference jobs_Reference = collection_job_request.document(trip_id);



        Map<String, Object> params_notification = new HashMap<>();
        params_notification.put(NOTIFICATION_notification_trip, trip_id);
        params_notification.put(NOTIFICATION_user_id, UID);
        params_notification.put(NOTIFICATION_message, notification_message);
        params_notification.put(NOTIFICATION_date, todays_date);
        params_notification.put(NOTIFICATION_status, 0);

        Map<String, Object> params_job_request = new HashMap<>();
        params_job_request.put(JOB_REQUEST_trip_id, trip_id);
        params_job_request.put(JOB_REQUEST_driver_id, "");
        params_job_request.put(JOB_REQUEST_date_requested, todays_date);
        params_job_request.put(JOB_REQUEST_req_status, 0);
        params_job_request.put(JOB_REQUEST_req_retries, 0);
        params_job_request.put(JOB_REQUEST_request_day, times_tamp);
        params_job_request.put(JOB_REQUEST_expiry_time, expiry_date);
        params_job_request.put(JOB_REQUEST_trip_user_id, UID);


        batch.set(notification_Reference, params_notification);
        batch.set(jobs_Reference, params_job_request);


        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(context, "Trip request sent wait for a rider", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, "Error , try again", Toast.LENGTH_SHORT).show();
            }
        });


        dialogController.dialog_dismiss();
    }





//    public String get_town_name (double lat, double lng){
//
//        try {
//
//            Geocoder gcd = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
//            if (addresses.size() > 0) {
//                System.out.println(addresses.get(0).getLocality());
////                String cityName = addresses.get(0).getAddressLine(0);
//
//                return addresses.get(0).getLocality();
//            } else {
//                return  "N/A";
//            }
//
//        }catch (Exception e){
//            return  "N/A";
//        }
//
//
//    }



    public void show_dialog_request_rider() {

        Button btn_preview_dismiss;
        TextView title_reg_ok, message_reg;
        ImageView img_top_photo;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_confirm_trip, null);
        dialogBuilder.setView(dialogView);

        title_reg_ok = (TextView) dialogView.findViewById(R.id.title_reg_ok);
        message_reg = (TextView) dialogView.findViewById(R.id.message_reg);
        img_top_photo= (ImageView) dialogView.findViewById(R.id.img_top_photo);

        img_top_photo.setImageResource(R.drawable.ic_motor_pass);

        title_reg_ok.setText("Confirm request");
        message_reg.setText("You are requesting a trip to "+destination_town);



        btn_preview_dismiss = (Button) dialogView.findViewById(R.id.ok_btn_reg_done);
        //  dialogBuilder.setTitle("Select login if you already have an account");
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        btn_preview_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();


                submit_trip ();

            }
        });
        b.show();

    }


}
