package com.orion3shoppy.bodamtaani.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ModelStateTimeLog {

    public String getState_time() {
        return state_time;
    }

    public String getState_actor() {
        return state_actor;
    }

    public String getState_doc_id() {
        return state_doc_id;
    }

    public int getState_id() {
        return state_id;
    }

    String state_time;
    String state_actor;
    String state_doc_id;
    int state_id;

    public ModelStateTimeLog() {

    }


    public ModelStateTimeLog(String state_time, String state_actor,int state_id, String state_doc_id) {

        this.state_time = state_time;
        this.state_actor = state_actor;
        this.state_id = state_id;
        this.state_doc_id = state_doc_id;



    }


}
