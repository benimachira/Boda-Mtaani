package com.orion3shoppy.bodamtaani.models;

public class ModelUsers {


    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public int getAccount_status() {
        return account_status;
    }

    public int getAccess_level() {
        return access_level;
    }

    public int getAccount_type() {
        return account_type;
    }

    public int getIs_online() {
        return is_online;
    }

    public int getIs_merchant() {
        return is_merchant;
    }

    public String getEmail_adress() {
        return email_adress;
    }

    public String getPhoto_url() {
        return photo_url;
    }
    public String getFirebase_service_id() {
        return firebase_service_id;
    }


    String user_id;
    String user_name;
    String user_phone;
    int account_status;
    int access_level;
    int account_type;
    int is_online;
    int is_merchant;
    String email_adress;
    String photo_url;
    String firebase_service_id;





    public ModelUsers() {

    }


    public ModelUsers(String user_id, String user_name, String user_phone, int account_status, int access_level,
                      int account_type, int is_online, int is_merchant, String email_adress, String photo_url, String firebase_service_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.access_level = access_level;
        this.account_status = account_status;
        this.is_merchant = is_merchant;
        this.email_adress = email_adress;
        this.photo_url = photo_url;
        this.is_online = is_online;
        this.account_type = account_type;
        this.firebase_service_id = firebase_service_id;


    }


}
