package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelTripBundle;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.cooking_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIP_BUNDLE;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_BUNDLE_bundle_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_BUNDLE_date_created;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_BUNDLE_user_id;

public class ActivityBundleTrips extends AppCompatActivity {

    public final int RequestPermissionCode = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference trip_bundle_reference = db.collection(COL_TRIP_BUNDLE);

    private CollectionReference trips_ref = db.collection(COL_TRIPS);

    Context context;
    String UID;

    double total_budget;
    final List<ModelTrips> material_list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;
    EditText ed_item_shoplist_name;
    String shopping_list_name;

    TextView tv_details, tv_shop_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_bundle);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        context = this;

        UID = GetFirebaseUserID();
        init_message_RecyclerView();
        select_data_trips();


    }

    public void add_new_trip(View view) {

        Intent intent = new Intent(context, ActivityAddBulkTrips.class);
        startActivity(intent);


    }


    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, material_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void select_data_trips() {

        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        trips_ref.whereEqualTo(TRIPS_user_id,UID).whereEqualTo(TRIPS_trip_bundle_status,1 ).whereEqualTo(TRIPS_trip_bundle_day,times_tamp ).orderBy(TRIPS_trip_date, Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }


                material_list.clear();
                total_budget = 0;


                if (queryDocumentSnapshots.size() > 0) {


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelTrips note = doc.toObject(ModelTrips.class);
                        note.setDocument_id(doc.getId());
                        material_list.add(note);

                    }

                    MrecyclerViewAdapter.notifyDataSetChanged();

                }
            }
        });


    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        //recyclerview adapter extending RecyclerView Adapter and pass a parameter of RecyclerViewAdapter ViewHolder

        List<ModelTrips> required_items;
        Context context;
        String current_date;


        public RecyclerViewAdapter(Context context, List<ModelTrips> required_items) {
            //define a constructer for this RecyclerViewAdapter

            this.required_items = required_items;
            this.context = context;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            current_date = dateFormat.format(date);


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //ViewHolder class which holds the views for this recyclerview

            public final View mView;
            LinearLayout linear_material;
            TextView tv_details, tv_shop_list,tv_details_lable;
            LinearLayout linear_mother;


            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                tv_shop_list = (TextView) view.findViewById(R.id.tv_shop_list);
                tv_details_lable= (TextView) view.findViewById(R.id.tv_details_lable);
                linear_mother= (LinearLayout) view.findViewById(R.id.linear_mother);

            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_trip_bundle, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            final String trip_id = required_items.get(position).getDocument_id();
            final String date_created = required_items.get(position).getTrip_date();


            holder.tv_details_lable.setText((position + 1) + ". Tracking no -");
            holder.tv_details.setText("" + trip_id);

            holder.tv_shop_list.setText("" + cooking_time(date_created));

            holder.linear_mother.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityTripStateLog.class);
                    intent.putExtra("trip_id", trip_id);
                    startActivity(intent);
                }
            });



        }


        @Override
        public int getItemCount() {
            //returns the size of the datasource suplied to the recyclerview
            return required_items.size();
        }


    }


}
