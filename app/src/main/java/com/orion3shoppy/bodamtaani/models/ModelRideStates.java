package com.orion3shoppy.bodamtaani.models;

public class ModelRideStates {


    String trip_state_name;
    int trip_state_id;
    String document_id;


    public String getDocument_id() {
        return document_id;
    }
    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }


    public String getTrip_state_name() {
        return trip_state_name;
    }

    public int getTrip_state_id() {
        return trip_state_id;
    }

    public ModelRideStates() {

    }


    public ModelRideStates(String trip_state_name,int trip_state_id) {
        this.trip_state_name = trip_state_name;
        this.trip_state_id = trip_state_id;

    }


}
