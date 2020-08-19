package com.orion3shoppy.bodamtaani.models;

import java.util.Date;

public class ModelJobRequests {

    public String getDriver_id() {
        return driver_id;
    }

    public String getDate_requested() {
        return date_requested;
    }

    public int getReq_status() {
        return req_status;
    }

    public int getRetries() {
        return retries;
    }

    public String getExpiry_time() {
        return expiry_time;
    }

    public int getRequest_sent() {
        return request_sent;
    }

    String driver_id;
    String date_requested;
    int req_status;
    int retries;
    String expiry_time;
    int request_sent;

    public String getTrip_user_id() {
        return trip_user_id;
    }

    String trip_user_id;


    public String getTrip_id() {
        return trip_id;
    }

    String trip_id;

    public ModelJobRequests(String driver_id,String date_requested,int req_status,
            int retries,String expiry_time,int request_sent,String trip_id,String trip_user_id) {
        this.driver_id = driver_id;
        this.date_requested = date_requested;
        this.req_status = req_status;
        this.retries = retries;
        this.expiry_time = expiry_time;
        this.request_sent = request_sent;
        this.trip_id = trip_id;
        this.trip_user_id = trip_user_id;


    }

    public ModelJobRequests(){

    }


}
