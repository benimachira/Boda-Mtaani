package com.orion3shoppy.bodamtaani.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.fragments.Fragment1_PassangerRide;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelTrips;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_town;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_is_engaged;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_is_online;


public class ActivityTracking extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mMapFragment; // MapView UI element

    private GoogleMap mGoogleMap; // object that represents googleMap and allows us to use Google Maps API features

    private Marker driverMarker; // Marker to display driver's location

    public final int RequestPermissionCode = 1;
    public final int RequestgpsCode = 2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference trips_ref = db.collection(COL_TRIPS);
    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);

    Context context;
    String UID;
    Toolbar toolbar;
    GPSTracker gpsTracker;

    ArrayList<LatLng> markerPoints;

    Polyline polylineFinal = null;
    ProgressBar progress_loading;
    EditText ed_tracking_no;
    String trip_id;


    TextView tv_current_state,tv_delivery_history;
    LinearLayout linear_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        context = this;
        gpsTracker =new GPSTracker(context);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progress_loading= (ProgressBar) findViewById(R.id.progress_loading);
        ed_tracking_no= (EditText) findViewById(R.id.ed_tracking_no);
        tv_current_state= (TextView) findViewById(R.id.tv_current_state);
        tv_delivery_history= (TextView) findViewById(R.id.tv_delivery_history);
        linear_info =  (LinearLayout) findViewById(R.id.linear_info);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        UID = GetFirebaseUserID();
        markerPoints = new ArrayList<LatLng>();

        if (CheckingPermissionIsEnabledOrNot()) {
        } else {
            //Calling method to enable permission and.
            RequestMultiplePermission();
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        } else {
            showGPSDisabledAlertToUser();
            return;
        }

        mMapFragment.getMapAsync(this);


        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }


        tv_delivery_history.setText(R.string.delivery_info);
        tv_delivery_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(trip_id)){
                    Toast.makeText(context, "Trip id is not provided", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, ActivityTripStateLog.class);
                intent.putExtra("trip_id",trip_id );
                startActivity(intent);

            }
        });


    }

    public void track_parcel(View view){
        trip_id= ed_tracking_no.getText().toString();

        if(TextUtils.isEmpty(trip_id)){
            ed_tracking_no.setError("Please enter tracking no. ");
            return;
        }

        check_trip_info(trip_id);


    }

    public void check_trip_info(String trip_id){
        progress_loading.setVisibility(View.VISIBLE);
        Log.e("Sssssssss","ssssss1"+trip_id);

        trips_ref.document(trip_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){

                    progress_loading.setVisibility(View.GONE);

                    Log.e("Sssssssss","ssssss"+e.getMessage());

                    return;
                }

                if (documentSnapshot.exists()) {

                    ModelTrips note = documentSnapshot.toObject(ModelTrips.class);
                    LatLng latLng_pick_up = new LatLng(note.getLat_1(),note.getLog_1());
                    LatLng latLng_destination = new LatLng(note.getLat_2(),note.getLog_2());
                    String driver_id = note.getTrip_driver_id();
                    int trip_is_alive = note.getTrip_is_alive();

                    int trip_state = note.getTrip_state();
                    String[] stringArray = getResources().getStringArray(R.array.trip_states);
                    String trip_status = stringArray[trip_state];



                    if(trip_is_alive ==1 ) {



                        set_up_markers(latLng_pick_up ,1);
                        set_up_markers(latLng_destination ,2);

                        if (!TextUtils.isEmpty(driver_id)) {
                            client_display_boda_watcher(driver_id);
                        }

                        add_polyline(latLng_pick_up, latLng_destination);

                    }

                    linear_info.setVisibility(View.VISIBLE);
                    tv_current_state.setText("Status: "+trip_status);
//


                }else {
                    linear_info.setVisibility(View.VISIBLE);
                    tv_delivery_history.setText("");
                    tv_current_state.setText("Trip not found");
                    remove_poly_line();
                }

                Log.d("Sssssssss","fffffffffffffffff2"+documentSnapshot.getId());

                progress_loading.setVisibility(View.GONE);
            }
        });



    }
    public void client_display_boda_watcher(String driver_id) {

        drivers_ref.document(driver_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){
                    ModelDrivers note = documentSnapshot.toObject(ModelDrivers.class);
                    double latitude = note.getCurrent_lat();
                    double longitude = note.getCurrent_log();

                    LatLng latLng = new LatLng(latitude, longitude);

                    set_up_markers(latLng,3);

                }


            }
        });

    }

    public void remove_poly_line(){
        if (polylineFinal != null) {
            polylineFinal.remove();
            markerPoints.clear();
        }
    }


    public void add_polyline(LatLng pick_up_loca, LatLng destination_loca) {
        remove_poly_line();

        // Already two locations
        if (markerPoints.size() > 1) {
            markerPoints.clear();
        }


        // Adding origin point to the ArrayList
        markerPoints.add(pick_up_loca);

//        LatLng destination=new LatLng(-1.292066, 36.821945);
//
        // Adding destination point to the ArrayList
        markerPoints.add(destination_loca);

        Log.d("wwwwwwwwwwww", "" + pick_up_loca + " " + destination_loca);

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {

            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

//        String key_acces = "key=AIzaSyCJPd3_QNzimlIuZtiPG3wiqo-hOgPs_3I";

        String key_acces = "key=AIzaSyArsA5HFv_Wpu_xjehyVT_SF5x_iZpC6z0";


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key_acces;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d("eeeeeeeeeeeeeeee", url);

        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... post_url) {

            // For storing data from web service
            String data = "";


            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(post_url[0]);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();
                iStream.close();
                urlConnection.disconnect();


            } catch (Exception e) {
                data = "E";
            } finally {

            }

            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("resulllt", result);

            if (result.contentEquals("E")) {

                Toast.makeText(getBaseContext(), "Cannot get directions, please check your internet connection",
                        Toast.LENGTH_LONG).show();

            } else {

                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;


            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";


            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points " + result, Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLACK);
            }

            // tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
            polylineFinal = mGoogleMap.addPolyline(lineOptions);
        }
    }


    public class DirectionsJSONParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        /**
         * Method to decode polyline points
         * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }





    private void showGPSDisabledAlertToUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, RequestgpsCode);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        if (mMapFragment != null) {
            mMapFragment.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapFragment != null) {
            mMapFragment.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapFragment != null) {
            mMapFragment.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapFragment != null) {
            mMapFragment.onLowMemory();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mGoogleMap = googleMap;

            // Changing map type
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Showing / hiding your current location
            mGoogleMap.setMyLocationEnabled(true);


            // Enable / Disable zooming controls
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            mGoogleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);


        } catch (SecurityException e) {
            e.printStackTrace();
        }


        double l_latitude = gpsTracker.getLatitude();
        double l_longitude = gpsTracker.getLongitude();

        LatLng newLocation = new LatLng(l_latitude, l_longitude);
        show_current_marker(newLocation);

    }


    private void animateCar(final LatLng destination) {
        final LatLng startPosition = driverMarker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                    driverMarker.setPosition(newPosition);
                } catch (Exception ex) {
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();
    }


    public void set_up_markers(LatLng my_latlng,  int determiner) {

        MarkerOptions markerOptions = new MarkerOptions().position(my_latlng);
        if(determiner == 1){

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker marker = mGoogleMap.addMarker(markerOptions.title("Pick up "));

        }else if(determiner ==2 ) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker marker = mGoogleMap.addMarker(markerOptions.title("Destination"));
        }else if(determiner ==3){

            int height = 60;
            int width = 70;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.boda_mataani_logo);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


            MarkerOptions markerOptions_meters = new MarkerOptions().position(my_latlng);
            Marker marker_meters = mGoogleMap.addMarker(new MarkerOptions().position(my_latlng).
                    icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        }



    }

    public void show_current_marker(final LatLng current_LatLng) {
        if (current_LatLng != null) {

            MarkerOptions markerOptions = new MarkerOptions().position(current_LatLng);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            Marker marker = mGoogleMap.addMarker(markerOptions.title(""));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(current_LatLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }




    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements ActivityTracking.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
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
                    boolean phone_call_permisions = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (finelocationPermission && coarsePermission && write_external_storage && phone_call_permisions) {
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
        int phone_call_permisions = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);


        return
                finelocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        coarselocPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        write_external_storage == PackageManager.PERMISSION_GRANTED &&
                        phone_call_permisions == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                WRITE_EXTERNAL_STORAGE,
                CALL_PHONE

        }, RequestPermissionCode);

    }




}
