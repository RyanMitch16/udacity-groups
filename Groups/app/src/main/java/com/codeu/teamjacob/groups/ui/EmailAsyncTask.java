package com.codeu.teamjacob.groups.ui;

/**
 * Created by saryal on 8/10/15.
 */

import android.os.AsyncTask;
import android.util.Log;
public class EmailAsyncTask extends AsyncTask<String, Void, Void> {


    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {
        final EmailSender sender = new EmailSender();
        try {
            sender.sendEmail(params[0], params[1],params[2]);
        } catch (RuntimeException e) {
            Log.e("RunTimeException", e.getMessage(), e);
        }
        return null;
    }

    protected void onProgressUpdate() {

    }

}