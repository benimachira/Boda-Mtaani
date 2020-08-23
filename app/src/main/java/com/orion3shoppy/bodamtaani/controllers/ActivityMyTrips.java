package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.cooking_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;

public class ActivityMyTrips extends AppCompatActivity {

    public final int RequestPermissionCode = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference REF_COL_TRIPS = db.collection(COL_TRIPS);

    Context context;


    String UID;
    String item_name, quantity, budget_price;
    int unit_of_measure;

    boolean is_active_batch = false;
    String batch_doc_ref;

    String local_item_name, local_supa;
    int local_unit_of_measure, local_filled_status;
    double local_budget_price, local_quantity, total_budget;
    final List<ModelTrips> material_list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;

    DialogController dialogController;
    String supa_name = "";
    LinearLayout linear_none, linear_some;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linear_none= (LinearLayout) findViewById(R.id.linear_none);
        linear_some= (LinearLayout) findViewById(R.id.linear_some);


        context = this;

        dialogController = new DialogController(context);

        UID = GetFirebaseUserID();


        init_message_RecyclerView();

        select_user();

        ;

    }


    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, material_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void select_user() {

        dialogController.dialog_show("Loading my trips... ");
        REF_COL_TRIPS.whereEqualTo(TRIPS_user_id, UID).limit(50).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {




                if (queryDocumentSnapshots.size() > 0) {
                    linear_none.setVisibility(View.GONE);
                    linear_some.setVisibility(View.VISIBLE);


                    material_list.clear();
                    total_budget = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelTrips note = doc.toObject(ModelTrips.class);
                        note.setDocument_id(doc.getId());


                        material_list.add(note);

                    }

                    MrecyclerViewAdapter.notifyDataSetChanged();

                }else {
                    linear_none.setVisibility(View.VISIBLE);
                    linear_some.setVisibility(View.GONE);
                }

                dialogController.dialog_dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogController.dialog_dismiss();
            }
        });

    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        //recyclerview adapter extending RecyclerView Adapter and pass a parameter of RecyclerViewAdapter ViewHolder

        List<ModelTrips> required_items;
        Context context;
        String current_date;
        double quantity;


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
            LinearLayout linear_view;
            TextView tv_details, tv_shop_list, tv_details_lable,tv_trip_cost,tv_track_id;
            ImageButton linear_edit, layout2;

            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                linear_view = (LinearLayout) view.findViewById(R.id.linear_view);
                tv_shop_list = (TextView) view.findViewById(R.id.tv_shop_list);
                tv_details_lable = (TextView) view.findViewById(R.id.tv_details_lable);
                tv_trip_cost = (TextView) view.findViewById(R.id.tv_trip_cost);
                tv_track_id = (TextView) view.findViewById(R.id.tv_track_id);

            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_my_trips, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            final int trip_state = required_items.get(position).getTrip_state();
            final String display_text = "" + required_items.get(position).getUser_id();
            final String document_id = "" + required_items.get(position).getDocument_id();
            final String trip_date = "" + required_items.get(position).getTrip_date();
            final double trip_cost = required_items.get(position).getTrip_cost();
            final double trip_type = required_items.get(position).getTrip_type();

            String trip_type_title = "";
            if(trip_type ==1){
                trip_type_title = "Passenger ride trip";
            }else if(trip_type ==2){
                trip_type_title = "Parcel delivery trip";
            }

            holder.tv_details_lable.setText("" + (1 + position) + ". "+trip_type_title);
            holder.tv_details.setText("Tracking number. - " );
            holder.tv_track_id.setText("" +document_id);
            holder.tv_shop_list.setText("" + cooking_time(trip_date));
            holder.tv_trip_cost.setText("Trip cost: Ksh " +trip_cost);






            holder.linear_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    change_activity(document_id);

                }
            });

            holder.tv_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    change_activity(document_id);

                }
            });

            holder.tv_details_lable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    change_activity(document_id);

                }
            });


        }

        public void change_activity(String document_id){
            Intent intent = new Intent(context, ActivityTripStateLog.class);
            intent.putExtra("trip_id", document_id);
            startActivity(intent);
        }


        @Override
        public int getItemCount() {
            //returns the size of the datasource suplied to the recyclerview
            return required_items.size();
        }


    }


}
