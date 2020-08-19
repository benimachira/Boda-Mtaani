package com.orion3shoppy.bodamtaani.models;

public class ModelDrivers {



    String user_id;
    String driver_name;
    String driver_id_number;
    String driver_licence_number;
    String drivers_phone;
    String car_reg_no;
    int driver_status;
    String photo_of_passport;
    String photo_of_id;
    String photo_of_licence;
    double current_lat;
    double current_log;

    public double getCurrent_lat() {
        return current_lat;
    }

    public double getCurrent_log() {
        return current_log;
    }



    public int getDriver_is_engaged() {
        return driver_is_engaged;
    }

    int driver_is_engaged;


    public String getDrivers_phone() {
        return drivers_phone;
    }

    public int getIs_online() {
        return is_online;
    }

    int is_online;
    double drivers_balance;


    public String getUser_id() {
        return user_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public String getDriver_id_number() {
        return driver_id_number;
    }

    public String getDriver_licence_number() {
        return driver_licence_number;
    }

    public String getCar_reg_no() {
        return car_reg_no;
    }

    public int getDriver_status() {
        return driver_status;
    }

    public String getPhoto_of_passport() {
        return photo_of_passport;
    }

    public String getPhoto_of_id() {
        return photo_of_id;
    }

    public String getPhoto_of_licence() {
        return photo_of_licence;
    }


    public double getDrivers_balance() {
        return drivers_balance;
    }

    public ModelDrivers() {

    }


    public ModelDrivers(String user_id,  String driver_name,String driver_id_number,String driver_licence_number,
            String car_reg_no,int driver_status,String photo_of_passport,String photo_of_id,String photo_of_licence,
                        double drivers_balance,String drivers_phone,int is_online,int driver_is_engaged) {
        this.user_id = user_id;
        this.driver_name = driver_name;
        this.driver_id_number = driver_id_number;
        this.driver_licence_number = driver_licence_number;
        this.car_reg_no = car_reg_no;
        this.drivers_phone = drivers_phone;
        this.driver_status = driver_status;
        this.photo_of_passport = photo_of_passport;
        this.photo_of_id = photo_of_id;
        this.photo_of_licence = photo_of_licence;
        this.drivers_balance = drivers_balance;
        this.is_online = is_online;
        this.driver_is_engaged = driver_is_engaged;







    }


}
