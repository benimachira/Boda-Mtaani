package com.orion3shoppy.bodamtaani.models;

import java.util.Date;

public class ModelNotification {

    public String getNotification_trip() {
        return notification_trip;
    }

    public String getNotification_user_id() {
        return notification_user_id;
    }

    public String getNotification_date() {
        return notification_date;
    }

    public int getNotification_status() {
        return notification_status;
    }

    String notification_trip;
    String notification_user_id;
    String notification_date;
    int notification_status;

    public String getNotification_message() {
        return notification_message;
    }

    String notification_message;





    public ModelNotification(String notification_trip, String notification_user_id,
                             String notification_date,int notification_status,String notification_message){

        this. notification_trip=notification_trip;
        this. notification_user_id= notification_user_id;
        this. notification_date= notification_date;
        this. notification_status= notification_status;
        this. notification_message= notification_message;



    }


    public ModelNotification(){

    }

}
