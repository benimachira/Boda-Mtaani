package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;
import com.orion3shoppy.bodamtaani.controllers.ActivityPayments;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.orion3shoppy.bodamtaani.controllers.ActivityHomePage.AUTOCOMPLETE_REQUEST_CODE;
import static com.orion3shoppy.bodamtaani.controllers.ActivityHomePage.AUTOCOMPLETE_REQUEST_CODE2;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.calculate_distance;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.doubleToStringNoDecimal;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.remove_null;

public class Fragment1_PassangerRide extends Fragment {
    Context context;
    String UID;
    TextView ed_pick_loca, ed_delivery_loca, tv_pass_cost;
    Button request_rider_pass;

    List<LatLng> pickup_lat_log = new ArrayList<>();
    List<LatLng> destination_lat_log = new ArrayList<>();
    double trip_cost = 0;



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

                    request_pass_rider();
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


    public void request_pass_rider() {

        int TRIPS_trip_type = 1;
        int TRIPS_trip_state = 0;
        double TRIPS_lat_1 = pickup_lat_log.get(0).latitude;
        double TRIPS_lat_2 = destination_lat_log.get(0).latitude;
        double TRIPS_log_1 = destination_lat_log.get(0).longitude;
        double TRIPS_log_2 = destination_lat_log.get(0).longitude;

        Intent intent = new Intent(context, ActivityPayments.class);
        intent.putExtra("TRIPS_user_id", UID);
        intent.putExtra("TRIPS_trip_type", TRIPS_trip_type);
        intent.putExtra("TRIPS_trip_state", TRIPS_trip_state);
        intent.putExtra("TRIPS_lat_1", TRIPS_lat_1);
        intent.putExtra("TRIPS_lat_2", TRIPS_lat_2);
        intent.putExtra("TRIPS_log_1", TRIPS_log_1);
        intent.putExtra("TRIPS_log_2", TRIPS_log_2);
        intent.putExtra("bundle_status", 0);
        intent.putExtra("trip_cost", trip_cost);
        intent.putExtra("parcel_cost", 0.0);

        startActivity(intent);


    }


}
