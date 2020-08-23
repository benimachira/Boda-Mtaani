package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelSharingInfo;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_is_winner;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_participation_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_SHARING_DRAW_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;

public class ActivityPreviousWinners extends AppCompatActivity {

    public final int RequestPermissionCode = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private CollectionReference collection_shares = db.collection(COL_SHARING_DRAW);
    Context context;


    String UID;
    String item_name, quantity, budget_price;
    int unit_of_measure;

    boolean is_active_batch = false;
    String batch_doc_ref;

    String local_item_name, local_supa;
    int local_unit_of_measure, local_filled_status;
    double local_budget_price, local_quantity, total_budget;
    final List<ModelSharingInfo> material_list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;

    DialogController dialogController;
    String supa_name = "";

    LinearLayout linear_none, linear_some;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_winner);

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


        load_firebase();


    }


    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, material_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    public void load_firebase() {


        collection_shares.
                whereEqualTo(COL_SHARING_DRAW_is_winner, 1)
                .limit(21)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            linear_none.setVisibility(View.VISIBLE);
                            linear_some.setVisibility(View.GONE);
                            return;
                        }


                        if (queryDocumentSnapshots.size() > 0) {

                            linear_none.setVisibility(View.GONE);
                            linear_some.setVisibility(View.VISIBLE);

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ModelSharingInfo note = doc.toObject(ModelSharingInfo.class);
                                material_list.add(note);

                            }

                            MrecyclerViewAdapter.notifyDataSetChanged();

                        } else {

                            linear_none.setVisibility(View.VISIBLE);
                            linear_some.setVisibility(View.GONE);
                        }


                    }
                });


    }




    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        //recyclerview adapter extending RecyclerView Adapter and pass a parameter of RecyclerViewAdapter ViewHolder

        List<ModelSharingInfo> required_items;
        Context context;
        String current_date;
        double quantity;


        public RecyclerViewAdapter(Context context, List<ModelSharingInfo> required_items) {
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
            TextView tv_details, tv_details_lable;
            ImageButton linear_edit, layout2;

            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                tv_details_lable= (TextView) view.findViewById(R.id.tv_details_lable);
                linear_view = (LinearLayout) view.findViewById(R.id.linear_view);


            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_previous_winners, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            final String user_id = required_items.get(position).getUser_id();
            final String participation_date = required_items.get(position).getParticipation_date();
            final String participation_user_name = required_items.get(position).getParticipation_user_name();
            final String participation_day = required_items.get(position).getParticipation_day();

            holder.tv_details.setText("" + (1 + position) + ". " + participation_user_name);
            holder.tv_details_lable.setText("Day : "+participation_day);




        }


        @Override
        public int getItemCount() {
            //returns the size of the datasource suplied to the recyclerview
            return required_items.size();
        }


    }


}
