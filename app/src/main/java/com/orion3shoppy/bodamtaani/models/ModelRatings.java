package com.orion3shoppy.bodamtaani.models;

import java.util.Date;

public class ModelRatings {
    public String getDriver_id() {
        return driver_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    String driver_id;
    String user_id;
    String date;
    String my_rating;


    public String getMy_rating() {
        return my_rating;
    }




    public ModelRatings(String driver_id, String user_id, String date,String my_rating){

        this.driver_id = driver_id;
        this.user_id = user_id;
        this.date = date;
        this.my_rating = my_rating;


    }


    public ModelRatings(){

    }

}
