package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.controllers.mpesa.ApiUtils;
import com.orion3shoppy.bodamtaani.controllers.mpesa.STKPush;
import com.orion3shoppy.bodamtaani.controllers.mpesa.STKPushService;
import com.orion3shoppy.bodamtaani.controllers.mpesa.utils.Utils;
import com.orion3shoppy.bodamtaani.models.ModelTowns;
import com.orion3shoppy.bodamtaani.models.ModelTrips;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.APP_ID;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TOWNS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_TRIPS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_lat_1;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_lat_2;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_log_1;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_log_2;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_payment_trip_is_alive;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_day;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_bundle_status;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_cost;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_parcel_cost;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_state;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_trip_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIPS_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_payment_town_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.TRIP_payment_verified;

public class ActivityPayments extends AppCompatActivity {

    Context context;

    EditText ed_phone_no;
    String _phone;
    TextView tv_trip_cost;

    String mFireBaseRegId;
    ProgressDialog dialog;
    private String _CONSUMER_KEY = "xNFFv90Grv3ram7rw7UmL25XwExZKAkz";
    private String _CONSUMER_SECRET = "DnNGeegAmcYyFNNA";
    //private String _CALLBACKURL = "https://orion3shoppy.com/boda_boda_api/firestore_stk.js";
    private String _CALLBACKURL = "http://orion3shoppy.com/api_stk_payments/stk_process";

    private String _BUSINESS_SHORT_CODE = "136823";
    private String _PARTYB = "136823";
    private String _PASSKEY = "0d797ac303f043f30ba204a0d02a88a68b13f522dd69feafa5a137d126cf72dd";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String TOKEN_URL = "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collection_trips = db.collection(COL_TRIPS);
    private CollectionReference towns_reference = db.collection(COL_TOWNS);

    private String token = null;
    Call<STKPush> call;
    STKPushService stkPushService;
    String DOC_ID, trips_DOC_ID;
    boolean filled_status = false;
    String callback_url;


    String UID;

    double gross_cost;

    String local_user_id;
    int local_trip_type;
    int local_trip_state;
    double local_lat_1;
    double local_lat_2;
    double local_log_1;
    double local_log_2;
    int bundle_status= 0;

    DialogController dialogController;

    double trip_cost,parcel_cost;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        context = this;
        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        DOC_ID = getIntent().getExtras().getString("document_id","");
//        local_DOC_ID = getIntent().getExtras().getString("trips_DOC_ID","");

        local_trip_type = getIntent().getExtras().getInt("TRIPS_trip_type", 0);
        local_trip_state = getIntent().getExtras().getInt("TRIPS_trip_state", 0);
        local_lat_1 = getIntent().getExtras().getDouble("TRIPS_lat_1", 0);
        local_lat_2 = getIntent().getExtras().getDouble("TRIPS_lat_2", 0);
        local_log_1 = getIntent().getExtras().getDouble("TRIPS_log_1", 0);
        local_log_2 = getIntent().getExtras().getDouble("TRIPS_log_2", 0);
        bundle_status = getIntent().getExtras().getInt("bundle_status", 0);
        trip_cost= getIntent().getExtras().getDouble("trip_cost", 0);
        parcel_cost= getIntent().getExtras().getDouble("parcel_cost", 0);


        getToken(_CONSUMER_KEY, _CONSUMER_SECRET);
        getFirebaseRegId();

        UID = GetFirebaseUserID();
        dialogController = new DialogController(context);


        dialog = new ProgressDialog(this);
        ed_phone_no = (EditText) findViewById(R.id.ed_phone_no);
        tv_trip_cost= (TextView) findViewById(R.id.tv_trip_cost);

        tv_trip_cost.setText("Ksh "+(int)trip_cost);
    }

    private void dialog_approve() {
        String message = "Please confirm you want to request a boda boda. The total trip cost is " + gross_cost;
        String ok_btn = "Confirm";
        String cancel = "Cancel";
        String title = "Confirm request";


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(ok_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
        alertDialogBuilder.setNegativeButton(cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



    public void show_dialog_feedback() {

        Button btn_preview_dismiss;
        TextView title_reg_ok, message_reg;


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_ok_registration, null);
        dialogBuilder.setView(dialogView);

        title_reg_ok = (TextView) dialogView.findViewById(R.id.title_reg_ok);
        message_reg = (TextView) dialogView.findViewById(R.id.message_reg);

        title_reg_ok.setText("Payment Received");
        message_reg.setText("Your payment has been received, A rider will be at your service in a minute.");

        btn_preview_dismiss = (Button) dialogView.findViewById(R.id.ok_btn_reg_done);
        //  dialogBuilder.setTitle("Select login if you already have an account");
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        btn_preview_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();

                Intent intent = new Intent(getApplicationContext(), ActivityHomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                if (getIntent().getBooleanExtra("EXIT", false)) {
                    finish();
                }

            }
        });
        b.show();

    }


    public void pay_now(View view) {

        if (filled_status) {
            Toast.makeText(context, "You have already paid for this shopping", Toast.LENGTH_SHORT).show();
            return;
        }

        _phone = ed_phone_no.getText().toString();

        if (TextUtils.isEmpty(_phone) || !isValidMobile(_phone)) {
            ed_phone_no.setError("Please enter a valid phone no.");
            return;

        }
        dialogController.dialog_show("Initiating payment ...");

        fetch_loca_payment ( _phone);



    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 10 || phone.length() > 12) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }


    public void getFirebaseRegId() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("ERR", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        mFireBaseRegId = token;

                        // Log and toast

//                        Log.d("ERR", token);
//                        Toast.makeText(ActivityPayments.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private String buildURI(String url, Map<String, String> params) {

        // build url with parameters.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build().toString();
    }


    public void fetch_loca_payment (final String _phone){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int town_id = settings.getInt("town_id", 0);


        if(town_id > 0){
            insert_trip(_phone, town_id);
            Toast.makeText(context,"Your area has Bodas "+town_id,Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context,"Boda Mtaani is not available in this area "+town_id,Toast.LENGTH_SHORT).show();
        }

    }

    public static int minIndex (List<Float> list) {
        return list.indexOf (Collections.min(list)); }



    public void insert_trip(final String _phone, int town_id) {
        String times_tamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String todays_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> params = new HashMap<>();
        params.put(TRIPS_user_id, UID);
        params.put(TRIPS_trip_type, local_trip_type);
        params.put(TRIPS_trip_state, local_trip_state);
        params.put(TRIPS_lat_1, local_lat_1);
        params.put(TRIPS_lat_2, local_lat_2);
        params.put(TRIPS_log_1, local_log_1);
        params.put(TRIPS_log_2, local_log_2);
        params.put(TRIP_payment_verified, 0);
        params.put(TRIP_payment_town_id, town_id);
        params.put(TRIPS_payment_trip_is_alive, 0);
        params.put(TRIPS_trip_bundle_status, bundle_status);
        params.put(TRIPS_trip_bundle_day, times_tamp);
        params.put(TRIPS_trip_date, todays_date);
        params.put(TRIPS_trip_cost, trip_cost);
        params.put(TRIPS_trip_parcel_cost, parcel_cost);








        collection_trips.add(params).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String document = documentReference.getId();

                performSTKPush(_phone, document);
                listen_to_changes(document);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogController.dialog_dismiss();
                Toast.makeText(context,"Internal error 2",Toast.LENGTH_SHORT).show();

            }
        });




    }




    public void listen_to_changes(String doc_id) {

        collection_trips.document(doc_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    ModelTrips trips = documentSnapshot.toObject(ModelTrips.class);
                    if(trips.getPayment_verified() == 1){
                        show_dialog_feedback();
                    }
                }

            }
        });
    }

    public void performSTKPush(final String phone_number, final String trips_DOC_ID) {
        dialogController.update_messaging("Processing payment...");
        Toast.makeText(context,"Internal error 2",Toast.LENGTH_SHORT).show();


        getFirebaseRegId();


        Map<String, String> params = new HashMap<String, String>();
        params.put("REGSTRATION_ID", mFireBaseRegId);
        params.put("TRIPS_user_id", UID);
        params.put("DOC_ID", trips_DOC_ID);

        callback_url = buildURI(_CALLBACKURL, params);

        Log.d("eeeeeeeeeee", "call_back"+callback_url);

//        return;

        String timestamp = Utils.getTimestamp();

        final STKPush stkPush = new STKPush(_BUSINESS_SHORT_CODE,
                Utils.getPassword(_BUSINESS_SHORT_CODE, _PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(1),
                Utils.sanitizePhoneNumber(phone_number),
                _PARTYB,
                Utils.sanitizePhoneNumber(phone_number), callback_url,
                Utils.sanitizePhoneNumber(phone_number), //The account reference, work on this
                "SPESA"); //The transaction description

        try {
            call = stkPushService.sendPush(stkPush);


            call.enqueue(new Callback<STKPush>() {
                @Override
                public void onResponse(Call<STKPush> call, retrofit2.Response<STKPush> response) {
                    dialogController.dialog_dismiss();



                    try {
                        Log.e("Response from api", response.toString());

                        if (response.isSuccessful()) {

                            Log.d("unableA", "post submitted to API." + response.body().toString());
                            Log.d("unableA", "getCheckoutRequestID" + response.body().getCheckoutRequestID());

                            String MerchantRequestID = response.body().getMerchantRequestID();
                            String CheckoutRequestID = response.body().getCheckoutRequestID();


                        } else {
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.e("Mpesa -e", e.getMessage());

                    }
                }

                @Override
                public void onFailure(Call<STKPush> call, Throwable t) {
                    dialogController.dialog_dismiss();
                    Log.e("unableA ", "Unable to submit post to API." + t.getMessage());
                    t.printStackTrace();
                    Log.e("Error message", t.getLocalizedMessage());
                }
            });
            //Log.e("Method end", "method end");

        } catch (Exception ex) {
            dialogController.dialog_dismiss();


            Toast.makeText(ActivityPayments.this, "Check your internet connection to proceed", Toast.LENGTH_LONG).show();
//            View snackBarViewc = snackbar.getView();
//            TextView textView = (TextView) snackBarView.findViewById(androidx.appcompat.);
//            textView.setTextColor(Color.RED);
//            snackbar.show();


        }

    }


    public String getToken(String clientKey, String clientSecret) {

        try {
            String keys = clientKey + ":" + clientSecret;
            OkHttpClient client = new OkHttpClient();

            String auth = Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);

            System.out.println(auth);

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(TOKEN_URL)
                    .get()
                    .addHeader("authorization", "Basic " + auth)
                    .addHeader("cache-control", "no-cache")
                    .build();

            Log.d("bytes: ", "bytes " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP));

            client.newCall(request)
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Fetching token failed. Contact admin", Toast.LENGTH_LONG).show();
                                }
                            });
                        }


                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                            try {

                                String res = response.body().string();

                                Log.e("Token", "" + call.request().toString());
                                Log.e("response", "response " + response);

                                token = res;


                                JsonParser jsonParser = new JsonParser();
                                JsonObject jo = jsonParser.parse(token).getAsJsonObject();
//                                Assert.assertNotNull(jo);
//                                Log.e("Token", token + jo.get("access_token"));

                                token = jo.get("access_token").getAsString();


                                stkPushService = ApiUtils.getTasksService(token);
                            } catch (Exception IO) {
                                Log.d("weeeeeeeeee", "" + IO.getMessage().toString());

                            }
                        }


                    });
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(getActivity(), "Please add your app credentials", Toast.LENGTH_LONG).show();
        }
        return token;
    }


}
