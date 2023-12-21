package com.smiligence.etmtechAdmin.common;

import android.content.Context;
import android.os.AsyncTask;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;

public class InitTrueTimeAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context ctx;

    public InitTrueTimeAsyncTask(Context context){
        ctx = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            TrueTime.build()
                    .withSharedPreferences(ctx)
                    .withNtpHost("time.google.com")
                    .withLoggingEnabled(false)
                    .withConnectionTimeout(31_428)
                    .initialize();
        } catch (IOException e) {
            e.printStackTrace();
            //Log.d(SyncStateContract.Constants.TAG, "Exception when trying to get TrueTime", e);
        }
        return null;

    }

}
