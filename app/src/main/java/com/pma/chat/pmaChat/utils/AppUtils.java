package com.pma.chat.pmaChat.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUtils {

    public static final String FILE_PROVIDER = "com.pma.chat.pmaChat.fileprovider";

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static boolean hasActiveInternetConnection(Context context, URL url) {
        Log.d("AppUtils", "hasActiveInternetConnection call");
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (url.openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(5000);
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

    public static boolean isPermissionGrunted(Activity activity, String permission) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(activity,
                permission);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public static void gruntPermission(final Activity activity, final String permission, String message, final int rcCode) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(activity,
                permission);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showMessageOKCancel(activity, message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{permission},
                                        rcCode);
//                                dialog.dismiss();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
                            }
                        });
            }
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    rcCode);
        }
    }

    private static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }

}
