package com.orion3shoppy.bodamtaani.controllers;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelRideStates;
import com.orion3shoppy.bodamtaani.models.ModelStateTimeLog;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.cooking_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_BODA_TRIP_DISPUTE;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_BODA_TRIP_STATE;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_STATES_TIME_LOG;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_doc_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.STATES_TIME_LOG_state_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_DISPUTE_dispute_message;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_DISPUTE_dispute_trip_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_DISPUTE_dispute_trip_id;

public class ActivityTripStateLog extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference STATES_TIME_LOG_ref = db.collection(COL_STATES_TIME_LOG);
    private CollectionReference BODA_TRIP_STATE_ref = db.collection(COL_BODA_TRIP_STATE);
    private CollectionReference BODA_TRIP_DISPUTE_ref = db.collection(COL_BODA_TRIP_DISPUTE);



    final List<ModelRideStates> material_list = new ArrayList<>();
    final List<ModelStateTimeLog> trip_list = new ArrayList<>();


    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;
    Context context;
    List <String> dates_list = new ArrayList<>();
    List <Integer>  state_list = new ArrayList<>();
    String[] stringArray;
    TextView tv_lable_up;
    String item_name ="";
    String UID;
    String trip_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_state_logs);
        trip_id = getIntent().getExtras().getString("trip_id", "");


        stringArray = getResources().getStringArray(R.array.trip_states);

        context = this;
        UID= GetFirebaseUserID();

        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        init_message_RecyclerView();
        get_trip_info(trip_id);
//        get_trip_state();


    }

    public  void add_new_trip(View view){

        add_material_dialog(trip_id);

    }


    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, trip_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    public void get_trip_state() {

        BODA_TRIP_STATE_ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots.size() > 0) {

                    material_list.clear();


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelRideStates note = doc.toObject(ModelRideStates.class);
                        note.setDocument_id(doc.getId());

                        material_list.add(note);
                    }

                    MrecyclerViewAdapter.notifyDataSetChanged();


                }


            }
        });


    }


    public void get_trip_info(String trip_id) {

        STATES_TIME_LOG_ref.whereEqualTo(STATES_TIME_LOG_state_doc_id,trip_id).orderBy(STATES_TIME_LOG_state_id, Query.Direction.ASCENDING ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }


                if (queryDocumentSnapshots.size() > 0) {

                    trip_list.clear();

                    Toast.makeText(context, "There a trip ", Toast.LENGTH_SHORT).show();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelStateTimeLog note = doc.toObject(ModelStateTimeLog.class);
                        trip_list.add(note);
                    }

                    MrecyclerViewAdapter.notifyDataSetChanged();


                }else {
                    Toast.makeText(context, "No trips", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        //recyclerview adapter extending RecyclerView Adapter and pass a parameter of RecyclerViewAdapter ViewHolder

        List<ModelStateTimeLog> required_items;
        Context context;
        String current_date;
        double quantity;


        public RecyclerViewAdapter(Context context, List<ModelStateTimeLog> required_items) {
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
            TextView tv_details, tv_shop_list;
            ImageButton linear_edit, layout2;

            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                linear_view = (LinearLayout) view.findViewById(R.id.linear_view);
                tv_shop_list = (TextView) view.findViewById(R.id.tv_shop_list);



            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_trip_state, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            final int trip_state = required_items.get(position).getState_id();
            final String  state_time = "" + required_items.get(position).getState_time();
//            final String document_id = "" + required_items.get(position).getDocument_id();
            final String trip_date = "" + required_items.get(position).getState_time();


            holder.tv_shop_list.setText(""  +cooking_time(state_time));
            holder.tv_details.setText("" + stringArray[trip_state]);

        }


        @Override
        public int getItemCount() {
            //returns the size of the datasource suplied to the recyclerview
            return required_items.size();
        }


    }


    public void add_material_dialog(final String trip_id) {



        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_create_dispute, null);
        dialogBuilder.setView(dialogView);



        final Button add_an_item = (Button) dialogView.findViewById(R.id.add_an_item);


        final EditText ed_item_name = (EditText) dialogView.findViewById(R.id.ed_item_name);
        final TextView tv_trip_id = (TextView) dialogView.findViewById(R.id.tv_trip_id);
        tv_trip_id.setText("Tracking no:  "+trip_id);

        // autocomplete_tv.getAdapter().getItem_name()

        dialogBuilder.setCancelable(true);
        final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        add_an_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String times_tamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

                item_name = ed_item_name.getText().toString();

                if (TextUtils.isEmpty(item_name)) {
                    ed_item_name.setError("Please enter dispute description");
                    return;

                }


                Map<String, Object> note_pref = new HashMap<>();
                note_pref.put(TRIP_DISPUTE_dispute_message , item_name);
                note_pref.put(TRIP_DISPUTE_dispute_trip_id , trip_id);
                note_pref.put(TRIP_DISPUTE_dispute_trip_date , times_tamp);


                BODA_TRIP_DISPUTE_ref.add(note_pref).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Dispute submitted", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add", Toast.LENGTH_LONG).show();
                    }
                });

                b.dismiss();

            }
        });


        b.show();

    }


}
