package com.developer.sparty.Extras;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.developer.sparty.R;

public class CustomDialog {
    private Activity activity;
    private AlertDialog dialog;

     public CustomDialog(Activity mAct) {
       activity=mAct;
    }
    public void startLoadingDialog(){
         AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog,null));
        builder.setCancelable(false);
        dialog= builder.create();
        dialog.show();
    }
    public void dismissLoadingDialog(){
       dialog.dismiss();
    }
}
