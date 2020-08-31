package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelTowns;
import com.orion3shoppy.bodamtaani.models.ModelTrips;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_STATES_TIME_LOG;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TOWNS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_lat;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_log;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_town;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_date;
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
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_user_id;

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    public double longitude, latitude, accuracy = 0.0;

    Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private static final long MIN_TIME_BW_UPDATES = 180000; //every 3 minute
//    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
//    private static final long MIN_TIME_BW_UPDATES = 30000; //every 3 minute


    protected LocationManager locationManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);
    private CollectionReference users_ref = db.collection(COL_USERS);
    private CollectionReference trips_ref = db.collection(COL_TRIPS);
    public static final String MyPREFERENCES = "my_town_preference";
    private CollectionReference states_time_log = db.collection(COL_STATES_TIME_LOG);
    private CollectionReference notification_reference = db.collection(COL_NOTIFICATION);

    String user_id;
    int my_town_id = 0;
    int account_type;
    String trip_id;

    LatLng pick_up_latlng;
    LatLng destination_latlng;
    LatLng current_location_latlng;
    int trip_status=0;
    String trip_user_id;


    public GPSTracker(Context context) {
        this.context = context;
        user_id = GetFirebaseUserID();

        getLocation();
        show_user();
        get_trip_info();

        get_local_user_loca();

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                        }
                    }

                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public double getaccuracy() {
        if (location != null) {
            accuracy = location.getAccuracy();
        }

        return accuracy;
    }

    public int get_my_town_id() {
        return my_town_id;
    }


    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng newLocation = new LatLng(latitude, longitude);
        current_location_latlng = new LatLng(latitude, longitude);


        if (account_type == 2) {
            //broadcasting drivers current location
            update_loca_info(newLocation);


            //track the trip state for the
            if(trip_status == 1){

                if((pick_up_latlng !=null) && (current_location_latlng!=null)){
                    check_trip_destination(pick_up_latlng, current_location_latlng, trip_status);

                }else {
                    Log.d("DDDDDDD ", "some lat log is empty "+ pick_up_latlng+" curr : "+current_location_latlng );
                }


            }else if(trip_status ==3){

                if((destination_latlng !=null) && (current_location_latlng!=null)){
                    check_trip_destination(destination_latlng, current_location_latlng, trip_status);

                }else {
                    Log.d("DDDDDDD ", "some lat log is empty "+ pick_up_latlng+" curr : "+current_location_latlng );
                }

            }


        }else {

        }




    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void show_user() {

        users_ref.document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                if (doc.exists()) {
                    ModelUsers note = doc.toObject(ModelUsers.class);
                    account_type = note.getAccount_type();
                }
            }
        });


    }


    public void get_trip_info() {

        trips_ref.whereEqualTo(TRIPS_trip_driver_id, user_id).
                whereEqualTo(TRIPS_payment_trip_is_alive, 1).
                limit(1).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }


                        if (queryDocumentSnapshots.size() > 0) {

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                ModelTrips note = doc.toObject(ModelTrips.class);

                                trip_id = doc.getId();

                                trip_status= note.getTrip_state();
                                trip_user_id= note.getUser_id();

                                double pick_up_lat = note.getLat_1();
                                double pick_up_lng = note.getLog_1();
                                double destination_lat = note.getLat_2();
                                double destination_lng = note.getLog_2();

                                pick_up_latlng = new LatLng(pick_up_lat, pick_up_lng);
                                destination_latlng = new LatLng(destination_lat, destination_lng);

                            }


                        } else {
                            Log.d("DDDDDDD ", "No trip for me");
                        }


                    }
                });
    }


    public void check_trip_destination(LatLng latLngA, LatLng latLngB, final int trip_status) {

        //##################||
        //Method requirement
        //##################||
        //1. trip pick_up loca
        //2. trip destination loca
        //3. current loca

        //  ####  ||
        //steps
        // #####||


        //1. (distance < 100 m  pick up loca, the driver is here);
        //2. current trip state is 1(accepted)
        //3. set trip state to 2


        //if (distance < 100m destination , "You have arrived to the destination")
        //trip state is 3
        //set trip state to 4

        String date_today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());


        int new_trip_status = 0;
        if(trip_status == 1){
            new_trip_status = 2;
        }else if(trip_status ==3){
            new_trip_status =4;
        }

        Location locationA= new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);

        Location locationB= new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        float distance = locationA.distanceTo(locationB);


        if(distance < 200) {


            Map<String, Object> trip_data = new HashMap<>();
            trip_data.put(TRIPS_trip_state, new_trip_status);

            Map<String, Object> params_log_data = new HashMap<>();
            params_log_data.put(STATES_TIME_LOG_state_id, new_trip_status);
            params_log_data.put(STATES_TIME_LOG_state_time, date_today);
            params_log_data.put(STATES_TIME_LOG_state_actor, user_id);
            params_log_data.put(STATES_TIME_LOG_state_doc_id, trip_id);

            Map<String, Object> params_notification = new HashMap<>();
            params_notification.put(NOTIFICATION_notification_trip, trip_id);
            params_notification.put(NOTIFICATION_user_id, trip_user_id);
            params_notification.put(NOTIFICATION_date, date_today);
            params_notification.put(NOTIFICATION_status, 0);


            // Get a new write batch
            WriteBatch batch = db.batch();

            // Set the value of 'NYC'
            DocumentReference trip_reference = trips_ref.document(trip_id);
            batch.set(trip_reference, trip_data, SetOptions.merge());

            DocumentReference local_time_log = states_time_log.document();
            batch.set(local_time_log, params_log_data, SetOptions.merge());


            DocumentReference local_notification = notification_reference.document();
            batch.set(local_notification, params_notification, SetOptions.merge());


            // Commit the batch
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                    Log.d("DDDDDDD ", "new trip status is now set" + trip_status);
                    Toast.makeText(context, "The driver has arrived", Toast.LENGTH_SHORT).show();


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error occurred , with driver arriving ", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(context, "You have not reached there yet "+distance, Toast.LENGTH_SHORT).show();
            Log.d("ggggggggggggggggg", "dis "+distance+" A "+locationA+" B "+locationB);

        }

    }


    public void update_loca_info(final LatLng newLocation) {


        CollectionReference towns_collection = db.collection(COL_TOWNS);
        towns_collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    List<Float> distances = new ArrayList();

                    float min_value = 0;
                    List<LatLng> latLngs = new ArrayList<>();
                    List<String> town_names = new ArrayList<>();
                    List<Integer> list_town_id = new ArrayList<>();

                    Location locationA = new Location("point A");
                    locationA.setLatitude(newLocation.latitude);
                    locationA.setLongitude(newLocation.longitude);


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ModelTowns note = doc.toObject(ModelTowns.class);
                        float town_lat = (float) note.getTown_lat();
                        float town_log = (float) note.getTown_log();
                        String town_name = note.getTown_name();
                        int l_my_town_id = note.getTown_id();

                        LatLng latLng = new LatLng(town_lat, town_log);
                        latLngs.add(latLng);
                        town_names.add(town_name);
                        list_town_id.add(l_my_town_id);

                        Location locationB = new Location("point B");
                        locationB.setLatitude(town_lat);
                        locationB.setLongitude(town_log);

                        float distance = locationA.distanceTo(locationB);
//                        Log.d("eeeee", "dist\n "+distance+"aa \n "+customer_lat+"bb\n "+customer_log+"cc\n "+town_lat+"dd\n "+town_log);

//
//                        int distance_btwn = meterDistanceBetweenPoints(customer_lat,customer_log,town_lat,town_log );
                        distances.add(distance);


                    }

                    int min_distance = minIndex(distances);
                    LatLng nearest_town = latLngs.get(min_distance);
                    final int town_id = list_town_id.get(min_distance);


                    if (town_id > 0) {

                        Map<String, Object> params = new HashMap<>();
                        params.put(DRIVERS_current_lat, latitude);
                        params.put(DRIVERS_current_log, longitude);
                        params.put(DRIVERS_current_town, town_id);


                        drivers_ref.document(user_id).set(params, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(context, "Your have updated town to " + town_id, Toast.LENGTH_LONG).show();

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt("town_id", town_id);
                                editor.apply();
                            }
                        });
                    } else {

//                        Toast.makeText(context, "Your are have no boda " + town_id, Toast.LENGTH_LONG).show();

                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void get_local_user_loca() {


        CollectionReference towns_collection = db.collection(COL_TOWNS);
        towns_collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    List<Float> distances = new ArrayList();

                    float min_value = 0;
                    List<LatLng> latLngs = new ArrayList<>();
                    List<String> town_names = new ArrayList<>();
                    List<Integer> list_town_id = new ArrayList<>();

                    Location locationA = new Location("point A");
                    locationA.setLatitude(getLatitude());
                    locationA.setLongitude(getLongitude());


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ModelTowns note = doc.toObject(ModelTowns.class);
                        float town_lat = (float) note.getTown_lat();
                        float town_log = (float) note.getTown_log();
                        String town_name = note.getTown_name();
                        int l_my_town_id = note.getTown_id();

                        LatLng latLng = new LatLng(town_lat, town_log);
                        latLngs.add(latLng);
                        town_names.add(town_name);
                        list_town_id.add(l_my_town_id);

                        Location locationB = new Location("point B");
                        locationB.setLatitude(town_lat);
                        locationB.setLongitude(town_log);

                        float distance = locationA.distanceTo(locationB);
//                        Log.d("eeeee", "dist\n "+distance+"aa \n "+customer_lat+"bb\n "+customer_log+"cc\n "+town_lat+"dd\n "+town_log);

//
//                        int distance_btwn = meterDistanceBetweenPoints(customer_lat,customer_log,town_lat,town_log );
                        distances.add(distance);


                    }

                    int min_distance = minIndex(distances);
                    LatLng nearest_town = latLngs.get(min_distance);
                    final int town_id = list_town_id.get(min_distance);


                    if (town_id > 0) {

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("town_id", town_id);
                        editor.apply();
//                        Toast.makeText(context, "BBBBBB " + town_id, Toast.LENGTH_LONG).show();


                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public static int minIndex(List<Float> list) {
        return list.indexOf(Collections.min(list));
    }


}
