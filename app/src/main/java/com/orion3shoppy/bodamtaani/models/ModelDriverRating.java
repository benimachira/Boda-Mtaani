package com.orion3shoppy.bodamtaani.models;

public class ModelDriverRating {


    public double getAverage_rating() {
        return average_rating;
    }

    public int getTotal_rating() {
        return total_rating;
    }

    double average_rating;
    int total_rating;


    public ModelDriverRating(double avarage_rating,int total_rating){

        this. average_rating=avarage_rating;
        this. total_rating= total_rating;

    }


    public ModelDriverRating(){

    }

}
