package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelMerchantRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_DRIVERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_MERCHANT_REQUEST;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_car_reg_no;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_lat;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_log;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_current_town;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_document_review_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_id_number;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_is_engaged;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_licence_number;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_driver_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_drivers_id_number;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_drivers_phone;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_is_online;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.DRIVERS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_access_granted;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_message;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_notification_trip;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_account_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.USERS_is_merchant;


public class ActivityGrantMerchantRequest extends AppCompatActivity {

    public final int RequestPermissionCode = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shopy_merchant_req_ref = db.collection(COL_MERCHANT_REQUEST);
    private CollectionReference users_ref = db.collection(COL_USERS);
    private CollectionReference drivers_reference = db.collection(COL_DRIVERS);

    Context context;

    String UID;
    final List<ModelMerchantRequest> material_list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;
    LinearLayout linear_none, linear_add_new;
    TextView tv_total, tv_fare, tv_total_home;
    String[] stringArray;
    int doc_number = 1;
    EditText ed_item_shoplist_name;


    @ServerTimestamp
    private Date shopping_date;
    int filled_status;
    String shopping_list_name;
    int accepted_status;
    String DOC_ID;
    boolean not_first_timer = false;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_merchant_acess);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linear_none = (LinearLayout) findViewById(R.id.linear_none);



        context = this;


        UID = GetFirebaseUserID();


        init_message_RecyclerView();

        select_data_trips();


    }


    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, material_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void select_data_trips() {


        shopy_merchant_req_ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    return;
                }


                material_list.clear();


                if (queryDocumentSnapshots.size() > 0) {


                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelMerchantRequest note = doc.toObject(ModelMerchantRequest.class);
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

        List<ModelMerchantRequest> required_items;
        Context context;
        String current_date;
        double quantity;
        Map<String, Object> note_pref = new HashMap<>();



        public RecyclerViewAdapter(Context context, List<ModelMerchantRequest> required_items) {
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
            TextView tv_details;
            CheckBox check_shop_list;
            LinearLayout linear_edit;

            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                check_shop_list = (CheckBox) view.findViewById(R.id.check_shop_list);
                linear_edit = (LinearLayout) view.findViewById(R.id.linear_mother);

            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_grant_merchant_acess, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            int i_quantity;
            double d_quantity;
            String account = "";
            //update drivers info

//            final String user_name = required_items.get(position).getUser_name();
//            final int access_granted = required_items.get(position).getAccess_granted();
//            final String document_id = required_items.get(position).getDocument_id();
            final  int request_type = required_items.get(position).getRequest_type();
            final String user_id = required_items.get(position).getUser_id();
            final String user_name = required_items.get(position).getUser_name();
            final String boda_registration = required_items.get(position).getBoda_registration();
            final int access_granted = required_items.get(position).getAccess_granted();
            Date request_date = required_items.get(position).getRequest_date();
            final String phone_no = required_items.get(position).getPhone_no();
            final String document_id = required_items.get(position).getDocument_id();
            final String id_number = required_items.get(position).getId_number();
            final String biz_name = required_items.get(position).getBiz_name();
            final String biz_phone = required_items.get(position).getBiz_number();
            final int town_id = required_items.get(position).getTown_id();
            final double latitude = required_items.get(position).getLatitude();
            final double longitude = required_items.get(position).getLongitude();


            if(request_type ==1){
                holder.tv_details.setText("Boda : " + user_name + " - " + boda_registration);
            }else if(request_type ==2){
                holder.tv_details.setText("Dsp : " + biz_name + " - " + biz_phone);

            }



            if (access_granted == 1) {
                holder.check_shop_list.setChecked(true);
            }

            holder.check_shop_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //update merchant request
                    Map<String, Object> note_pref = new HashMap<>();


                    if(request_type ==1){

                        note_pref.put(DRIVERS_is_online, 1);
                        note_pref.put(DRIVERS_driver_status, 1);
                        note_pref.put(DRIVERS_driver_is_engaged, 0);
                        note_pref.put(DRIVERS_user_id, user_id);
                        note_pref.put(DRIVERS_drivers_phone, phone_no);
                        note_pref.put(DRIVERS_driver_name, user_name);
                        note_pref.put(DRIVERS_document_review_status, 1);
                        note_pref.put(DRIVERS_drivers_id_number, id_number);
                        note_pref.put(DRIVERS_car_reg_no, boda_registration);
                        note_pref.put(DRIVERS_current_lat, latitude);
                        note_pref.put(DRIVERS_current_log, longitude);
                        note_pref.put(DRIVERS_current_town, town_id);

                        create_shop_dem(document_id, request_type,note_pref );

                    }else if(request_type ==2){
                        holder.tv_details.setText("Dsp : " + biz_name + " - " + biz_phone);

                    }






                }
            });


        }



        public void create_shop_dem(final String document_id, int request_type, final Map<String, Object> note_pref) {
            // Get a new write batch
            WriteBatch batch = db.batch();
            String user_type= "";


            String date_today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());


            if(request_type ==1 ) {
                user_type= "boda boda operator";
            }else if(request_type == 2){
                user_type = "business operator";
            }
            String message = "Thank you for choosing us as a partner.  Welcome to Boda Mtaani as "+user_type;


            //update merchant request
            Map<String, Object> note_pref1 = new HashMap<>();
            note_pref1.put(MERCHANT_REQUEST_access_granted, 1);

            //update users information
            Map<String, Object> note_pref2 = new HashMap<>();

            if(request_type ==1 ) {
                note_pref2.put(USERS_account_type, 2);
            }else if(request_type == 2){
                note_pref2.put(USERS_account_type, 3);
            }else {
                note_pref2.put(USERS_account_type, 0);
            }

            note_pref2.put(USERS_is_merchant, 1);



            Map<String, Object> params_notification = new HashMap<>();
            params_notification.put(NOTIFICATION_notification_trip, "Merchant Welcome");
            params_notification.put(NOTIFICATION_user_id, document_id);
            params_notification.put(NOTIFICATION_message, message);
            params_notification.put(NOTIFICATION_date, date_today);
            params_notification.put(NOTIFICATION_status, 0);






            DocumentReference driversReference = drivers_reference.document(document_id);
            DocumentReference merchantreqReference = shopy_merchant_req_ref.document(document_id);
            DocumentReference usersReference = users_ref.document(document_id);

            batch.set(driversReference, note_pref, SetOptions.merge());
            batch.set(merchantreqReference, note_pref1, SetOptions.merge());
            batch.set(usersReference, note_pref2, SetOptions.merge());

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error , try again", Toast.LENGTH_SHORT).show();
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
