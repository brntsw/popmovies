package com.movies.bruno.udacity.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by BRUNO on 26/12/2015.
 */
public class NetworkUtil {

    private Context context;

    public NetworkUtil(Context context){
        this.context = context;
    }

    public boolean getNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
