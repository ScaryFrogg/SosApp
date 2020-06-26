package me.vojinpuric.sosapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class EmailHelper {
    private Context context;
    private String email;
    private String lat;
    private String lon;

    EmailHelper(Context context, String email, String lat, String lon, String trackingId) {
        this.context = context;

        if (isNetworkAvailable()) {
            new RetrieveFeedback().execute(email, lat, lon, trackingId);
        } else {
            Toast.makeText(context, "Please turn on your internet", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    static class RetrieveFeedback extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... args) {
            try {
                GMail androidEmail = new GMail(args[0], args[1], args[2], args[3]);
                androidEmail.createEmailMessage();
                androidEmail.sendEmail();
            } catch (Exception e) {
                Log.e("ErrorSendingMail", e.getMessage());
            }
            return null;
        }


    }
}
