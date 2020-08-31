package com.orion3shoppy.bodamtaani.firebase;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class FirebaseConstant {

    //App id
    public static final String APP_ID = "BODAMTAANI";
    public static final String STORAGE_BODA_DRIVER_DOCS=  "BODA_DRIVER_DOC/";
    public static final String STORAGE_BODA_PROFILE_PICS=  "STORAGE_BODA_PROFILE_PICS/";
    public static final String STORAGE_BODA_PROMO_IMAGES=  "BODA_PROMO_IMAGES/";

    //collections
    public static String COL_USERS = "BODA_users";
    public static String COL_TRIPS = "BODA_trips";
    public static String COL_DRIVERS = "BODA_drivers";
    public static String COL_BUSINESS = "BODA_businesses";
    public static String COL_PARCEL_STATES = "BODA_parcel_states";
    public static String COL_RIDE_STATES = "BODA_ride_states";
    public static String COL_STATES_TIME_LOG = "BODA_states_time_log";
    public static String COL_SHARING_DRAW = "BODA_sharing_competition";
    public static String COL_MERCHANT_REQUEST = "BODA_merchant_request";
    public static String COL_PAYMENTS = "BODA_payments";
    public static String COL_RIDERS_DOCUMENT = "BODA_driver_document";
    public static String COL_PROMO_IMAGES = "Boda_promo_images";
    public static String COL_TOWNS = "Boda_towns";
    public static String COL_JOB_REQUEST = "Boda_job_request";
    public static String COL_JOB_BOARD = "Boda_job_board";
    public static String COL_DRIVER_RATING = "Boda_driver_rating";
    public static String COL_RATINGS= "Boda_ratings";
    public static String COL_TRIP_BUNDLE= "Boda_trip_bundle";
    public static String COL_NOTIFICATION= "Boda_notifications";
    public static String COL_BODA_TRIP_STATE= "BodaTripState";
    public static String COL_BODA_TRIP_DISPUTE= "BodaTripDispute";





    public static String USERS_user_id = "user_id";
    public static String USERS_user_name = "user_name";
    public static String USERS_user_phone = "user_phone";
    public static String USERS_account_status = "account_status";
    public static String USERS_access_level = "access_level";
    public static String USERS_account_type = "account_type";
    public static String USERS_is_online = "is_online";
    public static String USERS_is_merchant = "is_merchant";
    public static String USERS_email_adress = "email_adress";
    public static String USERS_photo_url = "photo_url";
    public static String USERS_firebase_service_id = "firebase_service_id";

    public static String TRIPS_user_id = "user_id";
    public static String TRIPS_trip_type = "trip_type";
    public static String TRIPS_trip_state = "trip_state";
    public static String TRIPS_trip_driver_id = "trip_driver_id";
    public static String TRIPS_lat_1 = "lat_1";
    public static String TRIPS_lat_2 = "lat_2";
    public static String TRIPS_log_1 = "log_1";
    public static String TRIPS_log_2 = "log_2";
    public static String TRIP_payment_verified = "payment_verified";
    public static String TRIP_payment_town_id = "town_id";
    public static String TRIPS_payment_trip_is_alive = "trip_is_alive";
    public static String TRIPS_trip_bundle_status = "trip_bundle_status";
    public static String TRIPS_trip_bundle_day = "trip_bundle_day";
    public static String TRIPS_trip_date = "trip_date";
    public static String TRIPS_trip_cost = "trip_cost";
    public static String TRIPS_final_total_trip_cost = "final_total_trip_cost";
    public static String TRIPS_trip_parcel_cost = "trip_parcel_cost";
    public static String TRIPS_trip_destination_town = "trip_destination_town";

    public static String DRIVERS_user_id = "user_id";
    public static String DRIVERS_driver_name = "driver_name";
    public static String DRIVERS_driver_id_number= "driver_id_number";
    public static String DRIVERS_driver_licence_number = "driver_licence_number";
    public static String DRIVERS_car_reg_no = "car_reg_no";
    public static String DRIVERS_drivers_phone = "drivers_phone";
    public static String DRIVERS_drivers_id_number = "drivers_id_number";
    public static String DRIVERS_driver_status = "driver_status";
    public static String DRIVERS_photo_of_passport= "photo_of_passport";
    public static String DRIVERS_photo_of_id= "photo_of_id";
    public static String DRIVERS_photo_of_licence= "photo_of_licence";
    public static String DRIVERS_is_online= "is_online";
    public static String DRIVERS_current_lat= "current_lat";
    public static String DRIVERS_current_log= "current_log";
    public static String DRIVERS_document_review_status= "document_review_status";
    public static String DRIVERS_driver_is_engaged= "driver_is_engaged";
    public static String DRIVERS_current_town = "current_town";



    public static String BUSINESSES_biz_name = "biz_name";
    public static String BUSINESSES_biz_phone = "biz_phone";
    public static String BUSINESSES_biz_town = "biz_town";
    public static String BUSINESSES_registration_date = "registration_date";



    //parcel states
    public static String PARCEL_STATES_state_id = "state_id";
    public static String PARCEL_STATES_state_name = "state_name";

    public static String RIDE_STATES_state_id = "state_id";
    public static String RIDE_STATES_state_name = "state_name";


    public static String STATES_TIME_LOG_state_time = "state_time";
    public static String STATES_TIME_LOG_state_actor = "state_actor";
    public static String STATES_TIME_LOG_state_doc_id = "state_doc_id";
    public static String STATES_TIME_LOG_state_id = "state_id";


    public static String COL_SHARING_DRAW_user_id = "user_id";
    public static String COL_SHARING_DRAW_participation_date = "participation_date";
    public static String COL_SHARING_DRAW_is_winner = "is_winner";
    public static String COL_SHARING_DRAW_share_action = "share_action";
   public static String COL_SHARING_DRAW_participation_day = "participation_day";
    public static String COL_SHARING_DRAW_participation_user_name = "participation_user_name";

    public static String MERCHANT_REQUEST_user_id = "user_id";
    public static String MERCHANT_REQUEST_user_name = "user_name";
    public static String MERCHANT_REQUEST_boda_registration = "boda_registration";
    public static String MERCHANT_REQUEST_access_granted = "access_granted";
    public static String MERCHANT_REQUEST_request_date = "request_date";
    public static String MERCHANT_REQUEST_phone_no = "phone_no";
    public static String MERCHANT_REQUEST_id_number = "id_number";
    public static String MERCHANT_REQUEST_request_type = "request_type";
    public static String MERCHANT_REQUEST_biz_name = "biz_name";
    public static String MERCHANT_REQUEST_biz_number = "biz_number";
    public static String MERCHANT_REQUEST_town_id = "town_id";
    public static String MERCHANT_REQUEST_latitude = "latitude";
    public static String MERCHANT_REQUEST_longitude = "longitude";

    
    public static String RIDERS_DOCUMENT_rider_id = "rider_id";
    public static String RIDERS_DOCUMENT_document_name = "document_name";
    public static String RIDERS_DOCUMENT_document_type = "document_type";
    public static String RIDERS_DOCUMENT_expiry_state = "expiry_state";
    public static String RIDERS_DOCUMENT_document_verified = "document_verified";


    public static String JOB_REQUEST_driver_id = "driver_id";
    public static String JOB_REQUEST_date_requested = "date_requested";
    public static String JOB_REQUEST_req_status = "req_status"; //{0,1}
    public static String JOB_REQUEST_req_retries = "retries";//{3 max}
    public static String JOB_REQUEST_expiry_time = "expiry_time";//{3 max}
    public static String JOB_REQUEST_request_sent = "request_sent";//{3 max}
    public static String JOB_REQUEST_request_day = "request_day";//{3 max}
    public static String JOB_REQUEST_trip_user_id = "trip_user_id";//{3 max}
    public static String JOB_REQUEST_trip_id = "trip_id";//{3 max}






    public static String JOB_BOARD_job_id = "job_id";
    public static String JOB_BOARD_job_customer = "job_customer";
    public static String JOB_BOARD_job_driver = "job_driver";
    public static String JOB_BOARD_job_day = "job_day";



    public static String DRIVER_RATING_driver_id = "driver_id";
    public static String DRIVER_RATING_user_id = "user_id";
    public static String DRIVER_RATING_date = "date";

    public static String RATINGS_average_rating = "average_rating";
    public static String RATINGS_total_rating = "total_rating";

    public static String TRIP_BUNDLE_bundle_name = "bundle_name";
    public static String TRIP_BUNDLE_date_created = "date_created";
    public static String TRIP_BUNDLE_user_id = "user_id";
    public static String TRIP_BUNDLE_date_identifier = "date_identifier";

    public static String NOTIFICATION_notification_trip = "notification_trip";
    public static String NOTIFICATION_user_id = "notification_user_id";
    public static String NOTIFICATION_date = "notification_date";
    public static String NOTIFICATION_status = "notification_status";
    public static String NOTIFICATION_message = "notification_message";

    public static String TRIP_DISPUTE_dispute_message = "dispute_message";
    public static String TRIP_DISPUTE_dispute_trip_id = "dispute_trip_id";
    public static String TRIP_DISPUTE_dispute_trip_date = "dispute_trip_date";
    public static String TRIP_DISPUTE_dispute_user_id = "dispute_user_id";
    public static String TRIP_DISPUTE_dispute_status = "dispute_status";





}
