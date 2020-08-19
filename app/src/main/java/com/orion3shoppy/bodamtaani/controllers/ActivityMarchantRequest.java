package com.orion3shoppy.bodamtaani.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.orion3shoppy.bodamtaani.R;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.GetFirebaseUserID;
import static com.orion3shoppy.bodamtaani.controllers.UniversalMethods.sanitizePhoneNumber;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_MERCHANT_REQUEST;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_access_granted;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_biz_name;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_biz_number;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_boda_registration;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_id_number;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_latitude;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_longitude;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_phone_no;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_request_date;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_request_type;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_town_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_user_id;
import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.MERCHANT_REQUEST_user_name;

public class ActivityMarchantRequest extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shopy_merchant_req_ref = db.collection(COL_MERCHANT_REQUEST);

    Spinner spinner_unit;
    Button add_an_item;
    EditText ed_your_name, ed_reg_no, ed_supa_name, ed_phone_no;
    int account_type;
    String your_name, reg_no, supa_name, phone_no;
    Context context;
    Spinner spinner_account_type;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser_x;
    String UID;
    LinearLayout linear_boda;
    DialogController dialog_c;
    TextInputLayout lay_supa;
    DialogController dialogController;
TextView tv_title;
EditText ed_id_number;
String id_number;

LinearLayout linear_boda_request,linear_dispatch;
    String biz_name,biz_no;
    EditText ed_biz_name, ed_biz_no;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_request);


        context = this;
        dialog_c = new DialogController(context);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_x = firebaseAuth.getCurrentUser();


        UID = GetFirebaseUserID();
        dialogController= new DialogController(context);


        linear_boda = (LinearLayout) findViewById(R.id.linear_boda);
        add_an_item = (Button) findViewById(R.id.add_an_item);
        ed_your_name = (EditText) findViewById(R.id.ed_your_name);
        ed_reg_no = (EditText) findViewById(R.id.ed_reg_no);
        ed_phone_no = (EditText) findViewById(R.id.ed_phone_no);
        tv_title= (TextView) findViewById(R.id.tv_title);
        ed_id_number= (EditText) findViewById(R.id.ed_id_number);
        linear_boda_request= (LinearLayout) findViewById(R.id.linear_boda_request);
        linear_dispatch= (LinearLayout) findViewById(R.id.linear_dispatch);

        ed_biz_name= (EditText) findViewById(R.id.ed_biz_name);
        ed_biz_no= (EditText) findViewById(R.id.ed_biz_no);

        ArrayAdapter<CharSequence> adapter_units;
        Spinner spinner_unit = (Spinner) findViewById(R.id.spinner_account_type);
        adapter_units = ArrayAdapter.createFromResource(this, R.array.merchant_account_type, R.layout.spinner_item);
        adapter_units.setDropDownViewResource(R.layout.spinner_item);
        spinner_unit.setAdapter(adapter_units);

        spinner_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account_type = position;

                hide_show_layout(account_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        number_input_filters(ed_phone_no);

        shopy_merchant_req_ref.document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Show the shopping list is empty message
                    return;
                }
                if(documentSnapshot.exists()){
                    linear_boda.setVisibility(View.GONE);
                    tv_title.setText("You already submitted a request");

                }
            }
        });
    }

    public void hide_show_layout(final int account_type){
        if(account_type ==0){
            linear_boda_request.setVisibility(View.GONE);
            linear_dispatch.setVisibility(View.GONE);
        }else if(account_type ==1){
            linear_boda_request.setVisibility(View.VISIBLE);
            linear_dispatch.setVisibility(View.GONE);

        }else if(account_type ==2){
            linear_boda_request.setVisibility(View.GONE);
            linear_dispatch.setVisibility(View.VISIBLE);
        }
    }

    public static void number_input_filters(EditText editText) {

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-")) {
                        return "";
                    }
                }
                return null;
            }
        };


        editText.setFilters(new InputFilter[]{filter});
    }

    public void request_service_business(View view){

        if(account_type==0){
            Toast.makeText(context,"Please select the account type",Toast.LENGTH_LONG).show();
            return;
        }

        biz_name = ed_biz_name.getText().toString();
        biz_no = ed_biz_no.getText().toString();



        if (TextUtils.isEmpty(biz_name)) {
            ed_biz_name.setError("Please enter your id number");
            return;

        }


        if (TextUtils.isEmpty(biz_no) && isValidMobile(biz_no)) {
            ed_biz_no.setError("Please enter contact phone number");
            return;
        }

        upload_info(account_type);

        phone_no = sanitizePhoneNumber(phone_no);

    }

    public void request_service(View view) {

        your_name = ed_your_name.getText().toString();
        reg_no = ed_reg_no.getText().toString();
        phone_no = ed_phone_no.getText().toString();
        id_number= ed_id_number.getText().toString();



        if(account_type==0) {
            Toast.makeText(context, "Please select the account type", Toast.LENGTH_LONG).show();
            return;
        }

            if (TextUtils.isEmpty(your_name)) {
                ed_your_name.setError("Please enter Your name");
                return;

            }

            if (TextUtils.isEmpty(reg_no)) {
                ed_reg_no.setError("Please enter registration number");
                return;

            }

            if (TextUtils.isEmpty(id_number)) {
                ed_id_number.setError("Please enter your id number");
                return;

            }

            if (TextUtils.isEmpty(phone_no) && isValidMobile(phone_no)) {
                ed_phone_no.setError("Please enter contact phone number");
                return;
            }


            phone_no = sanitizePhoneNumber(phone_no);

            upload_info(account_type);

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

    public void upload_info(int account_type) {
        dialogController.dialog_show("Requesting..");
        Map<String, Object> note_pref = new HashMap<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int town_id = settings.getInt("town_id", 0);

        GPSTracker gpsTracker = new GPSTracker(context);
        double lat= gpsTracker.getLatitude();
        double log= gpsTracker.getLongitude();

        if(account_type ==1) {
            note_pref.put(MERCHANT_REQUEST_user_id, UID);
            note_pref.put(MERCHANT_REQUEST_user_name, your_name);
            note_pref.put(MERCHANT_REQUEST_boda_registration, reg_no);
            note_pref.put(MERCHANT_REQUEST_access_granted, 0);
            note_pref.put(MERCHANT_REQUEST_request_date, FieldValue.serverTimestamp());
            note_pref.put(MERCHANT_REQUEST_phone_no, phone_no);
            note_pref.put(MERCHANT_REQUEST_id_number, id_number);
            note_pref.put(MERCHANT_REQUEST_request_type, account_type);
            note_pref.put(MERCHANT_REQUEST_latitude, lat);
            note_pref.put(MERCHANT_REQUEST_longitude, log);
            note_pref.put(MERCHANT_REQUEST_town_id, town_id);

        }else if(account_type ==2){

            note_pref.put(MERCHANT_REQUEST_user_id, UID);
            note_pref.put(MERCHANT_REQUEST_access_granted, 0);
            note_pref.put(MERCHANT_REQUEST_request_date, FieldValue.serverTimestamp());
            note_pref.put(MERCHANT_REQUEST_request_type, account_type);
            note_pref.put(MERCHANT_REQUEST_biz_name, biz_name);
            note_pref.put(MERCHANT_REQUEST_biz_number, biz_no);



        }



        shopy_merchant_req_ref.document(UID).set(note_pref, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Request sent", Toast.LENGTH_LONG).show();
                dialogController.dialog_dismiss();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogController.dialog_dismiss();
                Toast.makeText(context, "failed to send request", Toast.LENGTH_LONG).show();
            }
        });

    }
}
