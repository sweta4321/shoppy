package com.example.shoppy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.shoppy.Model.User;

public class common {

    public static User currentUser;

    public static String  convertCodeToStatus(String status) {

        if(status.contentEquals("0")){
            return "Placed";
        }
        else if(status.contentEquals("1")){
            return "On The Way";
        }
        else {
            return "Shipped";
        }


    }

    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";


    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

}
