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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class Fragment2_ParcelRide extends Fragment {
    Context context;
    String UID;

    List<LatLng> pickup_lat_log = new ArrayList<>();
    List<LatLng> destination_lat_log = new ArrayList<>();

    TextView tv_parcel_pickup,et_delivery_loca,tv_delivery_cost;
    Spinner spinner_parcel_size;
    Button request_delivery;

    int spinner_value;
    double trip_cost = 0;
    int percel_cost = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_parcel, container, false);
        context = getContext();
        UID = GetFirebaseUserID();

        tv_parcel_pickup = (TextView) view.findViewById(R.id.tv_parcel_pickup);
        et_delivery_loca = (TextView) view.findViewById(R.id.tv_parcel_destination);
        request_delivery = (Button) view.findViewById(R.id.request_delivery);
        tv_delivery_cost = (TextView) view.findViewById(R.id.tv_delivery_cost);
        spinner_parcel_size = (Spinner) view.findViewById(R.id.spinner_parcel_size);


        tv_parcel_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHomePage) getActivity()).onSearchCalled(1);
            }
        });

        et_delivery_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHomePage) getActivity()).onSearchCalled(2);
            }
        });

        request_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (destination_lat_log.size() > 0 && pickup_lat_log.size() > 0) {
                    if(spinner_value == 0){
                        Toast.makeText(context, "Please parcel size", Toast.LENGTH_LONG).show();
                        return;
                    }

                    request_pass_rider();
                } else {
                    Toast.makeText(context, "Please set both pickup and delivery locations", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });


        ArrayAdapter<CharSequence> adapter_units;

        adapter_units = ArrayAdapter.createFromResource(context, R.array.parcel_size, R.layout.spinner_item);
        adapter_units.setDropDownViewResource(R.layout.spinner_item_normal);
        spinner_parcel_size.setAdapter(adapter_units);

        spinner_parcel_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_value = position;

                show_costing();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    public void show_costing(){


        if (pickup_lat_log.size() > 0 && destination_lat_log.size() > 0) {

            double distance = calculate_distance(pickup_lat_log.get(0).latitude, pickup_lat_log.get(0).longitude, destination_lat_log.get(0).latitude, destination_lat_log.get(0).longitude);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            int town_id = settings.getInt("town_id", 0);
            if(town_id == 0){
                Toast.makeText(context, "Your location cannot be determined at the moment", Toast.LENGTH_LONG).show();
                return ;
            }else if(town_id ==1 ){
                percel_cost = cook_price(spinner_value,distance );

                trip_cost = (distance * 0.05)+percel_cost ;

            }else {
                percel_cost = cook_price(spinner_value,distance );
                trip_cost = (distance * 0.033) + percel_cost;

            }


            tv_delivery_cost.setVisibility(View.VISIBLE);
            tv_delivery_cost.setText("Ksh " + doubleToStringNoDecimal(trip_cost));

        } else {
            tv_delivery_cost.setVisibility(View.GONE);
        }


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

                tv_parcel_pickup.setText("From: " + address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                et_delivery_loca.setText("Failed try again");
                et_delivery_loca.setTextColor(getResources().getColor(R.color.red_700));
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

                et_delivery_loca.setText("To: " + address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                et_delivery_loca.setText("Failed, try again");
                et_delivery_loca.setTextColor(getResources().getColor(R.color.red_700));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        show_costing();

    }



    public int cook_price(int range, double in_distance){
        double temp_dist = in_distance/1000;



        int return_price = 0;

        if(range == 2){
            //50-100kg
            // +50 ksh per 1000 meters
            return_price = 50*(int)temp_dist;

        }else if(range ==3){
            //100- 150kg
            // +100 ksh per 1000 meters
            return_price = 100*(int)temp_dist;

        }else if(range ==4){
            //150- 200kg
            // +150 ksh per 1000 meters

            return_price = 150*(int)temp_dist;
        }

        return  return_price;
    }


    public void request_pass_rider() {

        int TRIPS_trip_type = 2;
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
        intent.putExtra("parcel_cost", percel_cost);



        startActivity(intent);


    }


}
