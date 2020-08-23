package com.developer.sparty.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection {
    Context context;
    public CheckConnection(Context context) {
        this.context = context;
    }
    public boolean CheckNet(){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnec=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnec=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConnec!=null && wifiConnec.isConnectedOrConnecting()) || (mobileConnec!=null && mobileConnec.isConnectedOrConnecting()) ){
              return true;
        }
        else {
            return false;
        }
    }
}
