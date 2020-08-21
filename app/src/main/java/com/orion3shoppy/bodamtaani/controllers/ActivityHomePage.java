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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.orion3shoppy.bodamtaani.fragments.Fragment11_Tracking;
import com.orion3shoppy.bodamtaani.fragments.Fragment4_RiderComing;
import com.orion3shoppy.bodamtaani.fragments.Fragment2_ParcelRide;
import com.orion3shoppy.bodamtaani.fragments.Fragment1_PassangerRide;
import com.orion3shoppy.bodamtaani.fragments.Fragment5_InTransit;
import com.orion3shoppy.bodamtaani.fragments.Fragment6_ArrivedRateRider;
import com.orion3shoppy.bodamtaani.fragments.Fragment7_BodaAcceptRide;
import com.orion3shoppy.bodamtaani.fragments.Fragment3_RiderFound;
import com.orion3shoppy.bodamtaani.fragments.Fragment10_BodaCloseTrip;
import com.orion3shoppy.bodamtaani.fragments.Fragment8_Boda_Client_Details;
import com.orion3shoppy.bodamtaani.fragments.Fragment9_BodaConfirmPickup;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelJobRequests;
import com.orion3shoppy.bodamtaani.models.ModelNotification;
import com.orion3shoppy.bodamtaani.models.ModelTrips;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

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
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.createNotification;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_JOB_REQUEST;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TOWNS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_town;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_is_engaged;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_is_online;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_req_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_payment_trip_is_alive;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_firebase_service_id;


public class ActivityHomePage extends AppCompatActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private SupportMapFragment mMapFragment; // MapView UI element

    private GoogleMap mGoogleMap; // object that represents googleMap and allows us to use Google Maps API features

    private Marker driverMarker; // Marker to display driver's location

    public final int RequestPermissionCode = 1;
    public final int RequestgpsCode = 2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference users_ref = db.collection(COL_USERS);
    private CollectionReference trips_ref = db.collection(COL_TRIPS);

    private CollectionReference drivers_ref = db.collection(COL_DRIVERS);
    private CollectionReference towns_reference = db.collection(COL_TOWNS);
    private CollectionReference job_request_ref = db.collection(COL_JOB_REQUEST);
    private CollectionReference REF_COL_NOTIFICATION = db.collection(COL_NOTIFICATION);

    Context context;
    String UID;

    GPSTracker gpsRouter;
    double latitude, longitude;


    Handler handler;

    View headerView;
    TextView tv_header_name, tv_header_other;

    View content_frag_shop, content_frag_delivery;
    Toolbar toolbar;
    public BottomNavigationView navigation;
    TextView ed_pick_loca, ed_delivery_loca, tv_ac_status;
    TextInputLayout et_pick_loca, et_delivery_loca;
    CircleImageView img_user_avatar;

    public static final int AUTOCOMPLETE_REQUEST_CODE = 4;
    public static final int AUTOCOMPLETE_REQUEST_CODE2 = 5;
    public static final int AUTOCOMPLETE_REQUEST_CODE_DELIVERY = 6;
    public static final int AUTOCOMPLETE_REQUEST_CODE2_DELIVERY = 7;
    List<Double> list_of_latitude = new ArrayList<Double>();
    List<Double> list_of_longitude = new ArrayList<Double>();
    double distance, trip_cost;
    ImageView share_competition, img_offers, img_partner;
    public static ActivityHomePage instanceOfActivityHomePage;
    String user_name;
    String user_phone;
    String photo_url;
    long account_type = 0;
    long is_merchant;

    NavigationView navigationView;

    TextView tv_count;

    List<LatLng> drivers_list_long_lat = new ArrayList<>();


    View content_boda_accept_ride, content_rider_found;
    LinearLayout content_ride_content;
    Fragment1_PassangerRide fragment;

    private int new_request = 0;
    private int trip_accepted = 1;
    private int arrived_at_pickup = 2;
    private int pick_up = 3;
    private int destination = 4;
    private int closed = 5;
    private int dispute = 6;
    private int failed = 7;


    LatLng current_LatLng;
    Marker mCurrLocationMarker;

    GPSTracker gpsTracker;
    LocationManager locationManager;
    String locationProvider;
//    ArrayList markerPoints= new ArrayList();

    ArrayList<LatLng> markerPoints;

    Polyline polylineFinal = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genesis_activity);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

//        initiate_control_panel();

        gpsTracker = new GPSTracker(context);
        fragment = new Fragment1_PassangerRide();
        loadFragment(fragment);


        share_competition = (ImageView) findViewById(R.id.share_competition);
        img_offers = (ImageView) findViewById(R.id.img_offers);
        img_partner = (ImageView) findViewById(R.id.img_partner);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        headerView = navigationView.getHeaderView(0);
        tv_header_name = (TextView) headerView.findViewById(R.id.tv_header_name);
        tv_header_other = (TextView) headerView.findViewById(R.id.tv_header_other);
        tv_ac_status = (TextView) headerView.findViewById(R.id.tv_ac_status);
        img_user_avatar = (CircleImageView) headerView.findViewById(R.id.img_user_avatar);
        tv_count = (TextView) findViewById(R.id.tv_count);


        navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        UID = GetFirebaseUserID();
        markerPoints = new ArrayList<LatLng>();

        user_selection();
        select_user();

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


        share_competition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityShareBenefits.class);
                startActivity(intent);
            }
        });

        img_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityNotification.class);
                startActivity(intent);
            }
        });


        img_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityBundleTrips.class);
                startActivity(intent);
            }
        });

        img_user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityMyProfile.class);
                startActivity(intent);
            }
        });




        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firebase Error", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();


                        Map<String, Object> note_pref1 = new HashMap<>();
                        note_pref1.put(USERS_firebase_service_id, token);


                        users_ref.document(UID).set(note_pref1, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                        Log.d("Fire token", token);
//                        Toast.makeText(context, token, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void loadFragment(androidx.fragment.app.Fragment fragment) {
        // load fragment
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_boda:

                            fragment = new Fragment1_PassangerRide();
                            loadFragment(fragment);

                            return true;
                        case R.id.nav_parcel:
                            Fragment2_ParcelRide fragment2 = new Fragment2_ParcelRide();
                            loadFragment(fragment2);

                            return true;


                    }
                    return false;
                }
            };


    private void user_selection() {


        users_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Show the shopping list is empty message
                    return;
                }
                if (documentSnapshot.exists()) {
                    ModelUsers document = documentSnapshot.toObject(ModelUsers.class);

                    user_name = document.getUser_name();
                    user_phone = document.getUser_phone();
                    photo_url = document.getPhoto_url();
                    account_type = document.getAccount_type();
                    is_merchant = document.getIs_merchant();


                    if (!TextUtils.isEmpty(user_name)) {
                        tv_header_name.setText("" + user_name);
                    } else {
                        tv_header_name.setText("Name: N/A");
                    }
                    if (!TextUtils.isEmpty(user_phone)) {
                        tv_header_other.setText("" + user_phone);
                    } else {
                        tv_header_other.setText("Phone : N/A");
                    }

                    if (account_type == 2) {

                        tv_ac_status.setText("Boda boda account");

                    } else if (account_type == 3) {

                        tv_ac_status.setText("Business delivery account");

                    } else if (account_type == 7) {

                        tv_ac_status.setText("Admin");

                    } else {
                        tv_ac_status.setText("");
                    }

                    if (!TextUtils.isEmpty(photo_url)) {
                        Glide.with(getApplicationContext())
                                .load(photo_url)
                                .into(img_user_avatar);
                    }


                    if (account_type == 2) {
                        //listen for driver for requests and update when actions are done by driver
                        boda_watch_job_request();
                        boda_rider_trip_watcher();

                    } else {
                        //if your a client watch to find out if you have any trips to watch
                        client_trip_watcher();

                    }


                    hideItem();
                }


            }
        });


    }


    public void boda_watch_job_request() {

        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        job_request_ref.whereEqualTo(JOB_REQUEST_driver_id, UID).
                whereEqualTo(JOB_REQUEST_req_status, 1).limit(1).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("wwwwww", "we is 2 ");
                            return;

                        }

                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                ModelJobRequests note = doc.toObject(ModelJobRequests.class);
                                String trip_id = note.getTrip_id();
                                String user_id = note.getTrip_user_id();

                                navigation.setVisibility(View.GONE);

                                Bundle bundle = new Bundle();
                                bundle.putString("trip_id", trip_id);
                                bundle.putString("user_id", user_id);


                                Fragment7_BodaAcceptRide fragment2 = new Fragment7_BodaAcceptRide();
                                fragment2.setArguments(bundle);
                                loadFragment(fragment2);


                            }
                        } else {
                            Log.d("wwwwww", "No driver for this");
                        }

                    }
                });


    }


    public void client_trip_watcher() {


        trips_ref.whereEqualTo(TRIPS_payment_trip_is_alive, 1).
                whereEqualTo(TRIPS_user_id, UID).
                whereEqualTo(TRIPS_trip_bundle_status, 0).
                limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d("eeeeeeeee", "wwww " + e.getMessage());
                    return;
                }

                if (queryDocumentSnapshots.size() > 0) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelTrips note = doc.toObject(ModelTrips.class);
                        note.setDocument_id(doc.getId());

                        int trip_state = note.getTrip_state();
                        String driver_id = note.getTrip_driver_id();
                        final String user_id = note.getUser_id();
                        final String trip_id = note.getDocument_id();


                        final LatLng picku_up_loca = new LatLng(note.getLat_1(), note.getLog_1());
                        final LatLng destination_loca = new LatLng(note.getLat_2(), note.getLog_2());
                        set_up_markers(picku_up_loca, 1);
                        set_up_markers(destination_loca, 2);

                        add_polyline(picku_up_loca, destination_loca);


                        Log.d("fffffffffff ", "wwww " + driver_id);

                        if (trip_state == trip_accepted) {
                            //show fragment rider found -1

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("driver_id", driver_id);
                            bundle.putString("user_id", user_id);
                            bundle.putString("trip_id", trip_id);


                            Fragment3_RiderFound fragment3 = new Fragment3_RiderFound();
                            fragment3.setArguments(bundle);
                            loadFragment(fragment3);

                        } else if (trip_state == arrived_at_pickup) {
                            //show fragment rider arrived -2
                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("trip_id", doc.getId());
                            bundle.putString("driver_id", driver_id);
                            bundle.putString("user_id", user_id);


                            Fragment4_RiderComing fragment11 = new Fragment4_RiderComing();
                            fragment11.setArguments(bundle);
                            loadFragment(fragment11);


                        } else if (trip_state == pick_up) {
                            //show driver confirmed pickup now at destination 3

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("trip_id", doc.getId());
                            bundle.putInt("account_type", (int) account_type);

                            Fragment5_InTransit fragment5 = new Fragment5_InTransit();
                            fragment5.setArguments(bundle);
                            loadFragment(fragment5);

                        } else if (trip_state == destination) {
                            //show arrived at destination -4

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("driver_id", driver_id);

                            Fragment6_ArrivedRateRider fragment6 = new Fragment6_ArrivedRateRider();
                            fragment6.setArguments(bundle);
                            loadFragment(fragment6);

                        } else if (trip_state == closed) {
                            //show rate rider -5

                            Fragment1_PassangerRide fragment = new Fragment1_PassangerRide();
                            loadFragment(fragment);
                            navigation.setVisibility(View.VISIBLE);


                        } else if (trip_state == failed) {
                            //show fail after the job dint find driver -7

                        }


                        Log.d("eeeeeeeee", "wwww : " + trip_state);


                    }
                } else {
                    Log.d("eeeeeeeee", "wwww : NO things");
                }

            }
        });


    }


    private void select_user() {


        REF_COL_NOTIFICATION.whereEqualTo(NOTIFICATION_user_id, UID)
                .whereEqualTo(NOTIFICATION_status, 0).
                limit(10).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (queryDocumentSnapshots.size() > 0) {

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ModelNotification note = doc.toObject(ModelNotification.class);


                            }

                            tv_count.setVisibility(View.VISIBLE);
                            tv_count.setText("" + queryDocumentSnapshots.size());

                            int color_red = ContextCompat.getColor(context, R.color.yellow_A200);
                            ImageViewCompat.setImageTintList(img_offers, ColorStateList.valueOf(color_red));

                            tv_count.setTextColor(getResources().getColor(R.color.yellow_A200));


                            createNotification("This is the one", context);

                        }

                    }
                });


    }

    public void add_polyline(LatLng pick_up_loca, LatLng destination_loca) {

        if (polylineFinal != null) {
            polylineFinal.remove();
            markerPoints.clear();
        } else {

        }

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


    public void boda_rider_trip_watcher() {

        //RIDERS TRIP WATCH


        trips_ref.whereEqualTo(TRIPS_payment_trip_is_alive, 1).
                whereEqualTo(TRIPS_trip_driver_id, UID).
                limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots.size() > 0) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ModelTrips note = doc.toObject(ModelTrips.class);
                        int trip_state = note.getTrip_state();
                        String get_user_id = note.getUser_id();

                        if (trip_state == 1) {
                            //show fragment rider found -1

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", UID);

                            Fragment8_Boda_Client_Details fragment8 = new Fragment8_Boda_Client_Details();
                            fragment8.setArguments(bundle);
                            loadFragment(fragment8);

                        } else if (trip_state == arrived_at_pickup) {
                            //show fragment rider arrived -2

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("trip_id", doc.getId());

                            Fragment9_BodaConfirmPickup fragment9 = new Fragment9_BodaConfirmPickup();
                            fragment9.setArguments(bundle);
                            loadFragment(fragment9);

                        } else if (trip_state == pick_up) {
                            //Driver has authorized pick up now in transit -3

                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("trip_id", doc.getId());
                            bundle.putInt("account_type", (int) account_type);


                            Fragment5_InTransit fragment5 = new Fragment5_InTransit();
                            fragment5.setArguments(bundle);
                            loadFragment(fragment5);

                        } else if (trip_state == destination) {
                            //show arrived at destination -4
                            navigation.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("trip_id", doc.getId());
                            bundle.putString("user_id", get_user_id);

                            Fragment10_BodaCloseTrip fragment10 = new Fragment10_BodaCloseTrip();
                            fragment10.setArguments(bundle);
                            loadFragment(fragment10);


                        } else if (trip_state == closed) {
                            //show rate rider -5


                        } else if (trip_state == failed) {
                            //show fail after the job dint find driver -7

                        }


                    }
                }

            }
        });


    }

    public void client_display_boda_watcher() {

        //RIDERS TRIP WATCH

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int town_id = settings.getInt("town_id", 0);


        drivers_ref.whereEqualTo(DRIVERS_current_town, town_id).whereEqualTo(DRIVERS_driver_status, 1).whereEqualTo(DRIVERS_is_online, 1).whereEqualTo(DRIVERS_driver_is_engaged, 0).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {


                    return;
                }

                if (queryDocumentSnapshots.size() > 0) {


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ModelDrivers note = doc.toObject(ModelDrivers.class);
                        double latitude = note.getCurrent_lat();
                        double longitude = note.getCurrent_log();

                        LatLng latLng = new LatLng(latitude, longitude);

                        show_boda_markers(latLng);
                    }
                } else {


                }

            }
        });


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void hideItem() {

        Menu nav_Menu = navigationView.getMenu();
        //user
        MenuItem nav_ac = nav_Menu.findItem(R.id.nav_my_account);
        MenuItem nav_my_trips = nav_Menu.findItem(R.id.nav_my_trips);
        MenuItem nav_partner_account = nav_Menu.findItem(R.id.nav_partner_account);
        MenuItem nav_grant_access = nav_Menu.findItem(R.id.nav_grant_access);
        MenuItem nav_wasup = nav_Menu.findItem(R.id.nav_wasup);
        MenuItem nav_share = nav_Menu.findItem(R.id.nav_share);
        MenuItem nav_become_our_partner = nav_Menu.findItem(R.id.nav_become_our_partner);


        nav_ac.setVisible(true);
        nav_my_trips.setVisible(true);
        nav_wasup.setVisible(true);
        nav_share.setVisible(true);

        nav_partner_account.setVisible(false);
        nav_grant_access.setVisible(false);
        img_partner.setVisibility(View.GONE);


        if (account_type == 3) {
            //extra
            nav_partner_account.setVisible(true);
            img_partner.setVisibility(View.VISIBLE);

        } else if (account_type == 7) {
            //extra
            nav_partner_account.setVisible(true);
            nav_grant_access.setVisible(true);
            img_partner.setVisibility(View.VISIBLE);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_account) {
            Intent intent = new Intent(context, ActivityMyProfile.class);
            startActivity(intent);

        } else if (id == R.id.nav_my_trips) {

            Intent intent = new Intent(context, ActivityMyTrips.class);
            startActivity(intent);


        } else if (id == R.id.nav_partner_account) {

            Intent intent = new Intent(context, ActivityBundleTrips.class);
            startActivity(intent);


        } else if (id == R.id.nav_become_our_partner) {

            Intent intent = new Intent(context, ActivityMarchantRequest.class);
            startActivity(intent);

        } else if (id == R.id.nav_grant_access) {

            Intent intent = new Intent(context, ActivityGrantMerchantRequest.class);
            startActivity(intent);

        } else if (id == R.id.nav_tracking) {

          Intent intent = new Intent(context, ActivityTracking.class);
          startActivity(intent);

        } else if (id == R.id.nav_wasup) {

            PackageManager packageManager = context.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);

            try {
                String url = "https://api.whatsapp.com/send?phone=" + "254745946768" + "&text=" + URLEncoder.encode("Hello Boda Mtaani support.", "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_share) {

            Intent intent = new Intent(context, ActivityShareBenefits.class);
            startActivity(intent);


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
        This method is called when the map is completely set up. After the map is setup,
        the passenger will be subscribed to the driver's location channel, so their location
        can be updated on the MapView. We use the reference to the GoogleMap object googleMap
        to utilize any Google Maps API features.
     */
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
        client_display_boda_watcher();

    }


    public void show_boda_markers(LatLng new_location) {
        int height = 60;
        int width = 70;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.boda_mataani_logo);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


        MarkerOptions markerOptions_meters = new MarkerOptions().position(new_location);
        Marker marker_meters = mGoogleMap.addMarker(new MarkerOptions().position(new_location).
                icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

    }


    /*
        Animates car by moving it by fractions of the full path and finally moving it to its
        destination in a duration of 5 seconds.
     */
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

    @Override
    public void onLocationChanged(Location location) {

    }


    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
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


//
//    private class DownloadTask extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            String data = "";
//
//            try {
//                data = downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            ParserTask parserTask = new ParserTask();
//
//
//            parserTask.execute(result);
//
//        }
//
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap>> doInBackground(String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                DirectionsJSONParser parser = new DirectionsJSONParser();
//
//                routes = parser.parse(jObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap>> result) {
//            ArrayList points = null;
//            PolylineOptions lineOptions = null;
//            MarkerOptions markerOptions = new MarkerOptions();
//
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList();
//                lineOptions = new PolylineOptions();
//
//                List<HashMap> path = result.get(i);
//
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                lineOptions.addAll(points);
//                lineOptions.width(12);
//                lineOptions.color(Color.RED);
//                lineOptions.geodesic(true);
//
//            }
//
//// Drawing polyline in the Google Map for the i-th route
//            mGoogleMap.addPolyline(lineOptions);
//        }
//
//
//    }
//
//
//    private String getDirectionsUrl(LatLng origin, LatLng dest) {
//
//        // Origin of route
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//
//        // Destination of route
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//
//        // Sensor enabled
//        String sensor = "sensor=false";
//        String mode = "mode=driving";
//
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
//
//        // Output format
//        String output = "json";
//
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//
//
//        return url;
//    }

    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
