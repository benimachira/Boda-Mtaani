package com.orion3shoppy.bodamtaani.controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion3shoppy.bodamtaani.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UniversalMethods {


    private static FirebaseAuth firebaseAuth;
    private   static String UID;
    private  static  FirebaseUser firebaseUser_x;



    public static String fix_null_strings(String s){
        if(!TextUtils.isEmpty(s)){
            return s;
        }else {
            return  "";
        }
    }

    public static String fix_display_null_strings(String s){
        if(!TextUtils.isEmpty(s)){
            return s;
        }else {
            return  "N/A";
        }
    }
    public static String comparision_of_users_single(String user_me, String user_they) {

        int string_result = user_me.compareTo(user_they);
        if (string_result < 0) {

            return user_me ;

        } else {

            return user_they ;

        }

    }


    public  static  String cooking_time(String time){

        String display_time="";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time2 = dateFormat.format(new Date()); // Find todays date
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(time);
            d2 = format.parse(time2);

            if(d2.getTime()>d1.getTime()) {

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                Log.d("ftime", d1 + " " + d2 + " " + diff);

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);
                long diffWeeks = diff / (24 * 60 * 60 * 1000 * 7);
                if(diffMinutes <1 ){
                    display_time = "now";
                }else if(diffHours <1 ){
                    display_time = diffMinutes +" mins ago";
                }else if (diffDays < 1) {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                    String newString = new SimpleDateFormat("H:mm").format(date); // 9:00

                    display_time = diffHours+" hrs ago";

                } else if (diffDays == 1) {

                    display_time = diffDays + " day ago";

                } else if (diffDays > 1 && diffDays < 7) {

                    display_time = diffDays + " days ago";

                } else if (diffWeeks == 1 ) {
                    display_time = diffWeeks + " week ago";

                } else if (diffWeeks > 1 && diffDays < 30) {
                    display_time = diffWeeks + " weeks ago";

                } else if (diffDays > 30) {

                    String inputPattern = "yyyy-MM-dd HH:mm:ss";
                    String outputPattern = "EEE, d MMM yyyy";
                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    Date date = null;
                    date = inputFormat.parse(time);
                    display_time = "on "+outputFormat.format(date);

                } else {
                    display_time = "N/A";
                }

            }else {
                display_time="N/A";
            }


        } catch (ParseException e) {
            e.printStackTrace();
            display_time="N/A";
        }



        return display_time;
//
//        Date and Time Pattern	Result
//        "yyyy.MM.dd G 'at' HH:mm:ss z"	2001.07.04 AD at 12:08:56 PDT
//        "EEE, MMM d, ''yy"	Wed, Jul 4, '01
//        "h:mm a"	12:08 PM
//        "hh 'o''clock' a, zzzz"	12 o'clock PM, Pacific Daylight Time
//        "K:mm a, z"	0:08 PM, PDT
//        "yyyyy.MMMMM.dd GGG hh:mm aaa"	02001.July.04 AD 12:08 PM
//        "EEE, d MMM yyyy HH:mm:ss Z"	Wed, 4 Jul 2001 12:08:56 -0700
//        "yyMMddHHmmssZ"	010704120856-0700
//        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"	2001-07-04T12:08:56.235-0700

    }


    public static String sanitizePhoneNumber(String phone) {
        if(TextUtils.isEmpty(phone)){
            return "";
        }

        if(phone.equals("")){
            return "";
        }

        if (phone.length() < 11 & phone.startsWith("0")) {
            String p = phone.replaceFirst("^0", "254");
            return p;
        }
        if(phone.length() == 13 && phone.startsWith("+")){
            String p = phone.replaceFirst("^+", "");
            return p;
        }
        return phone;
    }

    public static  void call_driver(Context context , String driver_phone_number){

        if(TextUtils.isEmpty(driver_phone_number)){
            Toast.makeText(context, "A phone number is missing ", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + driver_phone_number));
        context.startActivity(intent);
    }

    public static String GetFirebaseUserID (){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_x = firebaseAuth.getCurrentUser();

        if (firebaseUser_x != null) {
            UID = firebaseUser_x.getUid();


        } else {
            UID = "";
        }
        return  UID;
    }

    public static int meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        int distance = 6366000 * (int)tt;

        return distance ;
    }

    public static float getSmallest(float[] a, int total){
        float temp;
        for (int i = 0; i < total; i++)
        {
            for (int j = i + 1; j < total; j++)
            {
                if (a[i] > a[j])
                {
                    temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        return a[0];
    }

    public static void number_input_filters(EditText editText) {

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-")) {
                        return "";
                    }
                }
                return null;
            }
        };


        editText.setFilters(new InputFilter[]{filter});
    }

    public static String remove_null(String s) {
        if (TextUtils.isEmpty(s) || s.contentEquals("null")) {
            return "";
        } else {
            return s;
        }

    }

    public static double calculate_distance(double lat1, double lon1, double lat2, double lon2) {
        double kilometer_unit = 0.621371;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = ((dist * 60 * 1.1515) / kilometer_unit) * 1000;
        return dist;
    }

    static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double doubleToStringNoDecimal(double d) {

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        String str =formatter.format(d);
        Double return_value=Double.parseDouble(str);


        return return_value;

    }



   static NotificationManager notifManager;

    public static void createNotification(String aMessage, Context context) {

        final int NOTIFY_ID = 0; // ID of notification
        String id = "GENERAL_NOTIFICATION"; // default_channel_id
        String title = "Head 1"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ActivityHomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ActivityHomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

}
