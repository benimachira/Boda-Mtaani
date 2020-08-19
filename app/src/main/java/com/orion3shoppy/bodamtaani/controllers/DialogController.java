package com.orion3shoppy.bodamtaani.controllers;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogController {
    Context context;
    ProgressDialog progressDialog;
    public DialogController(Context context){
        this.context= context;
        progressDialog=new ProgressDialog(context);



    }

    public void dialog_show(String message){
        progressDialog.setMessage(""+message);
        progressDialog.show();

    }

    public void update_messaging(String message){
        if(progressDialog.isShowing()) {
            progressDialog.setMessage("" + message);
        }

    }

    public void dialog_dismiss(){
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }




}
