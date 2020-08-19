package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.ActivityHomePage;
import com.orion3shoppy.bodamtaani.models.ModelDriverRating;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelRatings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.comparision_of_users_single;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.doubleToStringNoDecimal;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVER_RATING;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_RATINGS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVER_RATING_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVER_RATING_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVER_RATING_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.RATINGS_average_rating;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.RATINGS_total_rating;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_driver_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;

public class Fragment6_ArrivedRateRider extends Fragment {
    Context context;
    ImageButton btn_rate_later;
    ImageButton btn_rate_driver;
    RatingBar ratingbar, ratingBar_home;
    String UID;
    double rating = 0;
    int total_ratings = 0;
    boolean i_have_rated = false;


    double old_rating = 0;
    long old_count = 0;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference drivers_rating_ref = db.collection(COL_DRIVER_RATING);
    private CollectionReference ratings_ref = db.collection(COL_RATINGS);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_trip_complete, container, false);
        context = getContext();
        btn_rate_later = view.findViewById(R.id.btn_rate_later);
        btn_rate_driver = (ImageButton) view.findViewById(R.id.btn_rate_driver);
        ratingBar_home = (RatingBar) view.findViewById(R.id.ratingBar_home);
        ratingBar_home.setFocusable(false);

        UID = GetFirebaseUserID();
        final String driver_id = getArguments().getString("driver_id");

        get_driver_rating(driver_id);
        get_ratings(driver_id);


        btn_rate_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment1_PassangerRide fragment2 = new Fragment1_PassangerRide();
                ((ActivityHomePage) getActivity()).loadFragment(fragment2);
                ((ActivityHomePage) getActivity()).navigation.setVisibility(View.VISIBLE);



            }
        });


        btn_rate_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!i_have_rated) {
                    add_material_dialog(driver_id);
                } else {
                    Toast.makeText(context, "You have rated this rider before", Toast.LENGTH_SHORT).show();
                }


//
            }
        });

        return view;
    }


    public void get_driver_rating(String driver_id) {

        drivers_rating_ref.document(driver_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {
                    ModelDriverRating document = documentSnapshot.toObject(ModelDriverRating.class);
                    rating = document.getAverage_rating();
                    total_ratings = document.getTotal_rating();
                    ratingBar_home.setRating((float) rating);
                } else {
                    rating = 0;
                }

            }
        });


    }


    public void get_ratings(String driver_id) {

        String document_id = comparision_of_users_single(UID, driver_id);
        //this document contains old avg, total ratings and


        ratings_ref.document(document_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;

                }

                if (documentSnapshot.exists()) {
                    ModelRatings document = documentSnapshot.toObject(ModelRatings.class);
                    i_have_rated = true;

                } else {
                    i_have_rated = false;
                }


            }
        });


    }


    public void add_to_ratings(final String driver_id, final double new_rating) {
       final String times_tamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        final DocumentReference driver_rating_reference = drivers_rating_ref.document(driver_id);

        String document_id = comparision_of_users_single(UID, driver_id);
        final DocumentReference ratings_reference = ratings_ref.document(document_id);


        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(driver_rating_reference);
                if (snapshot.exists()) {
                    old_rating = snapshot.getDouble(RATINGS_average_rating);
                    old_count = snapshot.getLong(RATINGS_average_rating);

                } else {

                    old_rating = 0;
                    old_count = 0;

                }


                double new_avg = rating_system(old_rating, (int) old_count, new_rating);
                int new_count = (int) old_count + 1;

                Map<String, Object> driver_rating_data = new HashMap<>();
                driver_rating_data.put(RATINGS_average_rating, new_avg);
                driver_rating_data.put(RATINGS_total_rating, new_count);


                Map<String, Object> ratings_data = new HashMap<>();
                ratings_data.put(DRIVER_RATING_driver_id, driver_id);
                ratings_data.put(DRIVER_RATING_user_id, UID);
                ratings_data.put(DRIVER_RATING_date, times_tamp);


                transaction.set(driver_rating_reference, driver_rating_data, SetOptions.merge());
                transaction.set(ratings_reference, ratings_data, SetOptions.merge());

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Rated ", Toast.LENGTH_SHORT).show();
                Log.d("eeeeeeeeeeeeeeeeee ", "Transaction success!");

                Fragment1_PassangerRide fragment1 = new Fragment1_PassangerRide();
                ((ActivityHomePage) getActivity()).loadFragment(fragment1);
                ((ActivityHomePage) getActivity()).navigation.setVisibility(View.VISIBLE);


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show();
                        Log.e("eeeeeeeeeeeeeeeeee ", "dddd " + e.getMessage());
                    }
                });
    }


    public void add_material_dialog(final String driver_id) {
        Button btn_submit;


        ((ActivityHomePage) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_rate_driver, null);
        dialogBuilder.setView(dialogView);

        btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);
        ratingbar = (RatingBar) dialogView.findViewById(R.id.ratingBar);


        dialogBuilder.setCancelable(true);
        final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

                String rating = String.valueOf(ratingbar.getRating());
//                Toast.makeText(context, rating, Toast.LENGTH_LONG).show();

                if (TextUtils.isEmpty(rating)) {
                    Toast.makeText(context, "You have not submitted any ratings", Toast.LENGTH_LONG).show();
                    return;
                }

                double new_rate = Double.parseDouble(rating);

                if (new_rate == 0) {
                    Toast.makeText(context, "You have not submitted a ratings", Toast.LENGTH_LONG).show();

                    return;
                }


                add_to_ratings(driver_id, new_rate);


            }
        });


        b.show();

    }


    public double rating_system(double old_avg, int previous_count, double new_rating) {

        double new_count = previous_count + 1;

        double new_avg = ((old_avg * previous_count) + new_rating) / new_count;

        double new_double_avg = doubleToStringNoDecimal(new_avg);
//        Toast.makeText(context, "Your rating is "+new_double , Toast.LENGTH_LONG).show();

        return new_double_avg;


    }

}
