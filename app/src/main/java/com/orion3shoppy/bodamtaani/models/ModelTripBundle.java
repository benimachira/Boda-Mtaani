package com.orion3shoppy.bodamtaani.models;

public class ModelTripBundle {
    public String getDocument_id() {
        return document_id;
    }
    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }


    public String getBundle_name() {
        return bundle_name;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    String bundle_name;
    String date_created;
    String user_id;
    String document_id;


    public ModelTripBundle() {

    }


    public ModelTripBundle(String bundle_name,String date_created, String user_id) {
        this.user_id = user_id;
        this.bundle_name = bundle_name;
        this.date_created = date_created;




    }


}
