package com.orion3shoppy.bodamtaani.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ModelTrips {

    public String getDocument_id() {
        return document_id;
    }
    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }


    String user_id;

    public int getTrip_type() {
        return trip_type;
    }

    public int getTrip_state() {
        return trip_state;
    }

    public String getTrip_driver_id() {
        return trip_driver_id;
    }

    public double getLat_1() {
        return lat_1;
    }

    public double getLat_2() {
        return lat_2;
    }

    public double getLog_1() {
        return log_1;
    }

    public double getLog_2() {
        return log_2;
    }

    public int getPayment_verified() {
        return payment_verified;
    }

    public String getUser_id() {
        return user_id;
    }

    int trip_type;
    int trip_state;
    String trip_driver_id;
    double lat_1 ;
    double lat_2 ;
    double log_1 ;
    double log_2 ;
    int payment_verified;
    double trip_cost;

    public double getTrip_cost() {
        return trip_cost;
    }



    public int getTrip_is_alive() {
        return trip_is_alive;
    }

    int trip_is_alive;
    String document_id;


    public int getTrip_bundle_status() {
        return trip_bundle_status;
    }

    public String getTrip_bundle_day() {
        return trip_bundle_day;
    }

    public String getTrip_date() {
        return trip_date;
    }

    int trip_bundle_status;
    String trip_bundle_day;
    String trip_date;


    public ModelTrips() {

    }


    public ModelTrips(String user_id,int trip_state,String trip_driver_id,
                      double lat_1,double lat_2 , double log_1 ,double log_2,int trip_type,
                      int payment_verified, int trip_bundle_status,String trip_bundle_day,
                      String trip_date,int trip_is_alive,double trip_cost) {

        this.user_id = user_id;
        this.trip_state = trip_state;
        this.trip_type = trip_type;
        this.trip_driver_id = trip_driver_id;
        this.lat_1 = lat_1;
        this.lat_2 = lat_2;
        this.log_1 = log_1;
        this.log_2 = log_2;
        this.payment_verified = payment_verified;
        this.trip_bundle_status = trip_bundle_status;
        this.trip_bundle_day = trip_bundle_day;
        this.trip_date = trip_date;
        this.trip_is_alive = trip_is_alive;
        this.trip_cost = trip_cost;



    }


}
