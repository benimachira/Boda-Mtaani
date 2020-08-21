package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;
import com.orion3shoppy.bodamtaani.models.ModelDriverRating;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;

public class Fragment11_Tracking extends Fragment {
    Context context;
    String UID;
    TextView ed_tracking_no;
    Button request_rider_pass;
    ProgressBar progress_loading;
    TextView tracking;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference trips_ref = db.collection(COL_TRIPS);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_track_trip, container, false);
        context = getContext();
        UID = GetFirebaseUserID();

        ed_tracking_no = (TextView) view.findViewById(R.id.ed_tracking_no);
        progress_loading= (ProgressBar) view.findViewById(R.id.progress_loading);



        tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trip_id = ed_tracking_no.getText().toString();


                if(TextUtils.isEmpty(trip_id)){
                    ed_tracking_no.setError("Please enter a tracking number");
                    return;
                }

                check_trip_info(trip_id);


            }
        });


        return view;
    }



    public void check_trip_info(String trip_id){
        progress_loading.setVisibility(View.VISIBLE);

        trips_ref.document(trip_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {

               if(e!=null){
                   progress_loading.setVisibility(View.GONE);
                   return;
               }

                if (doc.exists()) {

                    ModelTrips note = doc.toObject(ModelTrips.class);
                    LatLng latLng_pick_up = new LatLng(note.getLat_1(),note.getLog_1());
                    LatLng latLng_destination = new LatLng(note.getLat_2(),note.getLog_2());

                    ((ActivityHomePage)getActivity()).add_polyline(latLng_pick_up, latLng_destination);

                }



                progress_loading.setVisibility(View.GONE);
            }
        });

    }



}
