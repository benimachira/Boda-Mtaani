package com.orion3shoppy.bodamtaani.controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelNotification;
import com.orion3shoppy.bodamtaani.models.ModelTrips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.cooking_time;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_NOTIFICATION;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.JOB_REQUEST_req_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.NOTIFICATION_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;

public class ActivityNotification extends AppCompatActivity {

    public final int RequestPermissionCode = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference REF_COL_SALES = db.collection(COL_TRIPS);
    private CollectionReference REF_COL_NOTIFICATION = db.collection(COL_NOTIFICATION);

    Context context;


    String UID;


    boolean is_active_batch = false;

    double local_budget_price, local_quantity, total_budget;
    final List<ModelNotification> material_list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter MrecyclerViewAdapter;

    DialogController dialogController;
    String supa_name = "";
    private NotificationManager notifManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        context = this;

        dialogController = new DialogController(context);

        UID = GetFirebaseUserID();


        init_message_RecyclerView();

        select_user();



    }


//    public void createNotification(String aMessage, Context context) {
//
//        final int NOTIFY_ID = 0; // ID of notification
//        String id = "GENERAL_NOTIFICATION"; // default_channel_id
//        String title = "Head 1"; // Default Channel
//        Intent intent;
//        PendingIntent pendingIntent;
//        NotificationCompat.Builder builder;
//        if (notifManager == null) {
//            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
//            if (mChannel == null) {
//                mChannel = new NotificationChannel(id, title, importance);
//                mChannel.enableVibration(true);
//                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                notifManager.createNotificationChannel(mChannel);
//            }
//            builder = new NotificationCompat.Builder(context, id);
//            intent = new Intent(context, ActivityHomePage.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            builder.setContentTitle(aMessage)                            // required
//                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
//                    .setContentText(context.getString(R.string.app_name)) // required
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .setTicker(aMessage)
//                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//        }
//        else {
//            builder = new NotificationCompat.Builder(context, id);
//            intent = new Intent(context, ActivityHomePage.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            builder.setContentTitle(aMessage)                            // required
//                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
//                    .setContentText(context.getString(R.string.app_name)) // required
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .setTicker(aMessage)
//                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
//                    .setPriority(Notification.PRIORITY_HIGH);
//        }
//        Notification notification = builder.build();
//        notifManager.notify(NOTIFY_ID, notification);
//    }



    private void init_message_RecyclerView() {

        MrecyclerViewAdapter = new RecyclerViewAdapter(context, material_list);
        recyclerView.setAdapter(MrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void select_user() {


        REF_COL_NOTIFICATION.whereEqualTo(NOTIFICATION_user_id,UID).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                material_list.clear();
                total_budget = 0;


                if (queryDocumentSnapshots.size() > 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(NOTIFICATION_status, 1);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ModelNotification note = doc.toObject(ModelNotification.class);

                        REF_COL_NOTIFICATION.document(doc.getId()).set(params, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        material_list.add(note);

                    }



//                    createNotification("This is the one", context);
                    MrecyclerViewAdapter.notifyDataSetChanged();

                }

            }
        });


    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        //recyclerview adapter extending RecyclerView Adapter and pass a parameter of RecyclerViewAdapter ViewHolder

        List<ModelNotification> required_items;
        Context context;
        String current_date;
        double quantity;


        public RecyclerViewAdapter(Context context, List<ModelNotification> required_items) {
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
            TextView tv_details, tv_number, tv_time, tv_shop_list,tv_details_lable;
            ImageButton linear_edit, layout2;

            public ViewHolder(View view) {
                //constracter for this ViewHolder
                super(view);
                mView = view;

                tv_details = (TextView) view.findViewById(R.id.tv_details);
                linear_view = (LinearLayout) view.findViewById(R.id.linear_view);
                tv_details_lable= (TextView) view.findViewById(R.id.tv_details_lable);
                tv_shop_list= (TextView) view.findViewById(R.id.tv_shop_list);

            }


        }
        //jj


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //oncreatviewHolder method to link the views to this class, similar to an activity oncreate
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);

            return new RecyclerViewAdapter.ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            //on bind view holder >>this happens when the views have been binded to the reyclerview

            final String trip_state = required_items.get(position).getNotification_trip();
            final String notification_message = required_items.get(position).getNotification_message();
            final String notification_date = required_items.get(position).getNotification_date();

            holder.tv_details_lable.setText("Trip no: " + trip_state);
            holder.tv_details.setText(" " + notification_message);
            holder.tv_shop_list.setText(" " + cooking_time(notification_date));




        }


        @Override
        public int getItemCount() {
            //returns the size of the datasource suplied to the recyclerview
            return required_items.size();
        }


    }


}
