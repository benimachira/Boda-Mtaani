package com.orion3shoppy.bodamtaani.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ModelWinningUsers {

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



    public ModelWinningUsers() {

    }


    public ModelWinningUsers(String user_id,String participation_date,int is_winner) {
        this.user_id= user_id;
        this.participation_date= participation_date;
        this.is_winner = is_winner;

    }


}
