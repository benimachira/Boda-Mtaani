package com.orion3shoppy.bodamtaani.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ModelSharingInfo {

    public String getUser_id() {
        return user_id;
    }

    public String getParticipation_date() {
        return participation_date;
    }

    public int getIs_winner() {
        return is_winner;
    }

    String user_id;
    String participation_date;
    int is_winner;

    public String getParticipation_day() {
        return participation_day;
    }

    String participation_day;

    public String getParticipation_user_name() {
        return participation_user_name;
    }

    String participation_user_name;



    public ModelSharingInfo() {

    }


    public ModelSharingInfo(String user_id, String participation_date,
                            int is_winner,String participation_user_name,
                            String participation_day) {
        this.user_id= user_id;
        this.participation_date= participation_date;
        this.is_winner = is_winner;
        this.participation_user_name = participation_user_name;
        this.participation_day = participation_day;

    }


}
