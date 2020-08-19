package com.orion3shoppy.bodamtaani.models;

import java.util.Date;

public class ModelMerchantRequest {

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }


    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getBoda_registration() {
        return boda_registration;
    }

    public int getAccess_granted() {
        return access_granted;
    }

    public Date getRequest_date() {
        return request_date;
    }

    public String getPhone_no() {
        return phone_no;
    }

    String user_id;
    String user_name;
    String boda_registration;
    int access_granted;
    Date request_date;
    String phone_no;
    String document_id;
    String id_number;
    String biz_name;
    String biz_number;
    int request_type;
    int town_id;
    double latitude;
    double longitude;

    public int getTown_id() {
        return town_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public int getRequest_type() {
        return request_type;
    }

    public String getBiz_name() {
        return biz_name;
    }

    public String getBiz_number() {
        return biz_number;
    }




    public String getId_number() {
        return id_number;
    }





    public ModelMerchantRequest() {

    }


    public ModelMerchantRequest( String user_id,String user_name,String boda_registration,
                                 int access_granted,Date request_date, String phone_no,
                                 String id_number, String biz_name,String biz_number,
                                 int request_type,int town_id,double latitude,double longitude) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.boda_registration = boda_registration;
        this.access_granted = access_granted;
        this.request_date = request_date;
        this.phone_no = phone_no;
        this.id_number = id_number;
        this.biz_name = biz_name;
        this.biz_number = biz_number;
        this.request_type = request_type;
        this.town_id = town_id;
        this.latitude = latitude;
        this.longitude = longitude;




    }


}
