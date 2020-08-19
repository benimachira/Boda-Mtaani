package com.orion3shoppy.bodamtaani.models;

import java.util.Date;

public class ModelDriversDocuments {

    public String getRider_id() {
        return rider_id;
    }


    public String getDocument_name() {
        return document_name;
    }


    public int getDocument_type() {
        return document_type;
    }



    public Date getExpiry_date() {
        return expiry_date;
    }



    public int getDocument_verified() {
        return document_verified;
    }


    String rider_id;
    String document_name;
    int document_type;
    Date expiry_date;
    int document_verified;

    public ModelDriversDocuments(String rider_id,String document_name,int document_type,Date expiry_date,int document_verified) {
        this.rider_id = rider_id;
        this.document_name = document_name;
        this.document_type = document_type;
        this.expiry_date = expiry_date;
        this.document_verified = document_verified;

    }


}
