package com.orion3shoppy.bodamtaani.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orion3shoppy.bodamtaani.R;
import com.orion3shoppy.bodamtaani.models.ModelDrivers;
import com.orion3shoppy.bodamtaani.models.ModelUsers;

import static com.orion3shoppy.bodamtaani.firebase.FirebaseConstant.COL_USERS;

public class Fragment8_Boda_Client_Details extends Fragment {
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users_ref = db.collection(COL_USERS);

    String client_phone_number;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frag_boda_client_info, container, false);
        context= getContext();

        final String user_id = getArguments().getString("user_id");

        get_client_info(user_id);

        return view;
    }

    public void get_client_info(String client_id){

        users_ref.document(client_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    ModelUsers document = documentSnapshot.toObject(ModelUsers.class);
                    client_phone_number = document.getUser_phone();
                }

            }
        });

    }


}
