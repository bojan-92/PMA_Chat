package com.pma.chat.pmaChat.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUtils {

    public static final String FILE_PROVIDER = "com.pma.chat.pmaChat.fileprovider";

    public static boolean hasActiveInternetConnection(Context context, URL url) {
        Log.d("AppUtils", "hasActiveInternetConnection call");
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (url.openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                Log.d("AppUtils", "Firebase available!");
                return (urlc.getResponseCode() == 200);
            } catch (Exception e) {
                Log.e("AppUtils", "Error while checking internet connection", e);
            }
        } else {
            Log.d("AppUtils", "Firebase not available!");
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
