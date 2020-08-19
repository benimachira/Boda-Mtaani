package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.orion3shoppy.bodamtaani.models.ModelTowns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TOWNS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_lat;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_log;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_town;

public class SetUpDriverLoca  {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);
    String UID;
    Context context;
    int my_town_id = 0;

    public SetUpDriverLoca(Context context){
        UID = GetFirebaseUserID();
        this.context = context;
    }

    public void update_loca_info (final  double longitude, final double latitude, final  int account_type){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int town_id = settings.getInt("town_id", 0);

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
                    locationA.setLatitude(latitude);
                    locationA.setLongitude(longitude);


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
                    double lat_a = nearest_town.latitude;
                    double lat_b = nearest_town.longitude;
                    String town_name = town_names.get(min_distance);
                    final int town_id = list_town_id.get(min_distance);
                    float min_distances_value = distances.get(min_distance);





                    if (town_id > 0) {

                        Map<String, Object> params = new HashMap<>();
                        params.put(DRIVERS_current_lat, latitude);
                        params.put(DRIVERS_current_log, longitude);
                        params.put(DRIVERS_current_town, my_town_id);

                        if (account_type == 2) {

                            Toast.makeText(context, "Your location is up to date "+town_id, Toast.LENGTH_LONG).show();

                            drivers_ref.document(UID).set(params, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putInt("town_id", town_id);
                                    editor.apply();
                                }
                            });
                        }

                    }else {
                        Toast.makeText(context, "No drivers available in this are "+town_id, Toast.LENGTH_LONG).show();

                    }


//
//
//                    updateUI(drivers_list_long_lat);


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public static int minIndex (List<Float> list) {
        return list.indexOf (Collections.min(list)); }




}
