package com.pma.chat.pmaChat.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;

public class ConnectionService extends AsyncTask<URL, Integer, Boolean> {

    private Context mContext;

    public AsyncResponse mDelegate;

    public interface AsyncResponse {
        void onProcessFinish(Boolean result);
    }

    public ConnectionService(Context context, AsyncResponse delegate) {
        mContext = context;
        mDelegate = delegate;
    }

    @Override
    protected Boolean doInBackground(URL... params) {
        return AppUtils.hasActiveInternetConnection(mContext, params[0]);
    }

    @Override
    protected void onPostExecute(Boolean hasInternetConnection) {
        mDelegate.onProcessFinish(hasInternetConnection);
    }
}
