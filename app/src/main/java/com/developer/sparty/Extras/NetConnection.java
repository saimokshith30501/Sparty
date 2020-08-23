package com.developer.sparty.Extras;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.developer.sparty.LOGorREG;
import com.developer.sparty.R;

public class NetConnection {
    private Activity activity;
    private AlertDialog dialog;

    public NetConnection(Activity mAct) {
        activity=mAct;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.custom_net_connectivity,null);
        Button retry=view.findViewById(R.id.btn_retry);
        builder.setView(view);
        builder.setCancelable(false);
        dialog= builder.create();
        dialog.show();
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new CheckConnection(activity).CheckNet())
                {
                    Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void dismissLoadingDialog(){
        dialog.dismiss();
    }
}
