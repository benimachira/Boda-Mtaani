package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.orion3shoppy.bodamtaani.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.calculate_distance;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.doubleToStringNoDecimal;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.remove_null;

public class ActivityAddBulkTrips extends AppCompatActivity {

    Context context;
    String UID ;
    TextView ed_pick_loca,ed_delivery_loca,tv_pass_cost;
    Button request_rider_pass;
    Spinner spinner_parcel_size;

    List<LatLng> pickup_lat_log = new ArrayList<>();
    List<LatLng> destination_lat_log = new ArrayList<>();

    public static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int AUTOCOMPLETE_REQUEST_CODE2 = 2;
    double trip_cost;
    double percel_cost;
    int spinner_value;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bulk_trip);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        context= this;
        UID = GetFirebaseUserID();

        ed_pick_loca = (TextView) findViewById(R.id.et_bulk_pick_loca);
        ed_delivery_loca= (TextView) findViewById(R.id.et_bulk_delivery_loca);
        request_rider_pass= (Button) findViewById(R.id.request_bulk_rider_pass);
        tv_pass_cost= (TextView) findViewById(R.id.tv_pass_cost);
        spinner_parcel_size= (Spinner) findViewById(R.id.spinner_parcel_size);


        ed_pick_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled(1);
            }
        });

        ed_delivery_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onSearchCalled(2);
            }
        });

        request_rider_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(destination_lat_log.size()>0 && pickup_lat_log.size() >0){
                    request_pass_rider();
                }else {
                    Toast.makeText(context,"Please set both pickup and delivery locations", Toast.LENGTH_LONG).show();
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



    }


    public void onSearchCalled(int loca_id) {
        int request_code = 0;

        if (loca_id == 1) {
            request_code = AUTOCOMPLETE_REQUEST_CODE;
        } else if (loca_id == 2) {
            request_code = AUTOCOMPLETE_REQUEST_CODE2;
        }
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, request_code);
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


            tv_pass_cost.setVisibility(View.VISIBLE);
            tv_pass_cost.setText("Ksh " + doubleToStringNoDecimal(trip_cost));

        } else {
            tv_pass_cost.setVisibility(View.GONE);
        }


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


        @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = remove_null(place.getName()) + " " + remove_null(place.getAddress());
                LatLng latLng = place.getLatLng();

                if(pickup_lat_log.size()>0) {
                    pickup_lat_log.set(0, latLng);
                }else {
                    pickup_lat_log.add(latLng);
                }

                ed_pick_loca.setText("From: "+address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                ed_pick_loca.setText("Failed try again");
                ed_pick_loca.setTextColor(getResources().getColor(R.color.red_700));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show();
            }


        }else if (requestCode == AUTOCOMPLETE_REQUEST_CODE2) {

            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                Toast.makeText(AutocompleteFromIntentActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = remove_null(place.getName()) + " " + remove_null(place.getAddress());
                LatLng latLng = place.getLatLng();

                if(destination_lat_log.size() > 0){
                    destination_lat_log.set(0,latLng);
                }else {
                    destination_lat_log.add(latLng) ;
                }

                ed_delivery_loca.setText("To: "+address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                ed_delivery_loca.setText("Failed, try again");
                ed_delivery_loca.setTextColor(getResources().getColor(R.color.red_700));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

            show_costing();



    }



    public void request_pass_rider() {

        int TRIPS_trip_type=  2;
        int TRIPS_trip_state=  0;
        double TRIPS_lat_1=  pickup_lat_log.get(0).latitude;
        double TRIPS_lat_2=  destination_lat_log.get(0).latitude;
        double TRIPS_log_1=  destination_lat_log.get(0).longitude;
        double TRIPS_log_2= destination_lat_log.get(0).longitude;

        Intent intent = new Intent(context, ActivityPayments.class);
        intent.putExtra("TRIPS_user_id",UID);
        intent.putExtra("TRIPS_trip_type",TRIPS_trip_type);
        intent.putExtra("TRIPS_trip_state",TRIPS_trip_state);
        intent.putExtra("TRIPS_lat_1",TRIPS_lat_1);
        intent.putExtra("TRIPS_lat_2",TRIPS_lat_2);
        intent.putExtra("TRIPS_log_1",TRIPS_log_1);
        intent.putExtra("TRIPS_log_2",TRIPS_log_2);
        intent.putExtra("bundle_status",1);
        intent.putExtra("trip_cost", trip_cost);
        intent.putExtra("parcel_cost", percel_cost);


        startActivity(intent);



    }
}
