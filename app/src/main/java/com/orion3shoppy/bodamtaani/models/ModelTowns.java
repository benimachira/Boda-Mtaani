package com.orion3shoppy.bodamtaani.models;

public class ModelTowns {

    public String getTown_name() {
        return town_name;
    }

    public double getTown_lat() {
        return town_lat;
    }

    public double getTown_log() {
        return town_log;
    }

    String town_name;
    double town_lat;
    double town_log;

    public int getTown_id() {
        return town_id;
    }

    int town_id;



    public ModelTowns() {

    }


    public ModelTowns(String town_name,double town_lat,double town_log,int town_id) {
        this.town_name = town_name;
        this.town_lat = town_lat;
        this.town_log = town_log;
        this.town_id = town_id;

    }


}
